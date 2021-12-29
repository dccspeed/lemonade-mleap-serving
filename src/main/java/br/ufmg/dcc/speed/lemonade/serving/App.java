package br.ufmg.dcc.speed.lemonade.serving;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ml.combust.mleap.runtime.frame.Transformer;
import ml.combust.mleap.runtime.javadsl.BundleBuilder;
import ml.combust.mleap.runtime.javadsl.ContextBuilder;

/**
 * Dropwizard application.
 */
public class App extends Application<AppConfig> {
    static {
        // Handle hdfs:// protocol
        URL.setURLStreamHandlerFactory(protocol -> "hdfs".equals(protocol)
                ? new URLStreamHandler() {
                    protected URLConnection openConnection(URL url) throws IOException {
                        return new URLConnection(url) {
                            public void connect() throws IOException {
                            }
                        };
                    }
                }
                : null);
    }
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "Lemonade MLeap Serving";
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {
        // Required if using environment variables substitution in config.
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor()));
    }

    @Override
    public void run(AppConfig configuration,
            Environment e) {

        // Provides details about JSON parsing failure
        e.jersey().register(new JsonProcessingExceptionMapper(true));

        String status = "OK";
        String modelPath = null;
        Transformer transformer = null;

        try {
            modelPath = loadMleapModel(configuration);
            transformer = new BundleBuilder().load(
                    new File(modelPath),
                    new ContextBuilder().createMleapContext()).root();

        } catch (Exception ex) {
            status = ex.getLocalizedMessage();
            logger.error("Error in run()", ex);
        }
        final ModelResource resource = new ModelResource(configuration,
                transformer, modelPath, status);
        e.jersey().register(resource);
        e.healthChecks().register("AppHealthCheck", new AppHealthCheck(status));
    }

    /**
     * Load MLeap model
     * 
     * @param configuration app config
     * @return Path to MLeap model in local filesystem.
     * @throws IOException        An error occurred
     * @throws URISyntaxException
     */
    private String loadMleapModel(AppConfig configuration) throws IOException,
            URISyntaxException {
        // Try to load MLeap model.
        Path tmpFile = Paths.get(
                System.getProperty("java.io.tmpdir"), "mleap", "bundle.zip");

        Files.createDirectories(tmpFile.getParent());
        String modelPath = tmpFile.toString();

        if (!Files.exists(tmpFile)) {
            URL url = new URL(configuration.getModel());
            if ("hdfs".equals(url.getProtocol())) {
                Configuration conf = new Configuration();
                //conf.set("fs.defaultFS", "hdfs://" + url.getHost() + ":" 
                //    + url.getPort());
                conf.set("dfs.client.use.datanode.hostname", "true");
                conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

                URI hdfsServerUri = new URI(url.getProtocol() + "://" 
                    + url.getHost() + ":" + url.getPort());
                FileSystem fs = FileSystem.get(hdfsServerUri, conf);
                fs.copyToLocalFile(false,
                        new org.apache.hadoop.fs.Path(url.getPath()),
                        new org.apache.hadoop.fs.Path(tmpFile.toString()),
                        true);

            } else if ("http".equals(url.getProtocol()) ||
                    "https".equals(url.getProtocol())) {

                BufferedInputStream in = new BufferedInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile.toString());
                byte dataBuffer[] = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                in.close();
                fileOutputStream.close();
            } else if ("file".equals(url.getProtocol())) {
                Files.copy(Paths.get(url.toURI()), tmpFile, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Unsupported protocol: " + url.getProtocol());
            }
            modelPath = tmpFile.toAbsolutePath().toString();
        }
        return modelPath;
    }

}
