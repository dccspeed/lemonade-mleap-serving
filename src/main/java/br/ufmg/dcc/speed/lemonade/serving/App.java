package br.ufmg.dcc.speed.lemonade.serving;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

/**
 * Dropwizard application.
 */
public class App extends Application<AppConfig> {
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

        try {
            modelPath = loadMleapModel(configuration);
        } catch (Exception ex) {
            status = ex.getLocalizedMessage();
        }
        final ModelResource resource = new ModelResource(configuration,
                modelPath, status);
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
        String modelPath = null;
        Path tmpDir = Files.createTempDirectory("hdfs-bundle");
        Path tmpFile = Paths.get(tmpDir.toString(), "bundle.zip");

        if (!Files.exists(tmpFile)) {
            FileSystem fs = FileSystem.get(new Configuration());
            URL url = new URL(configuration.getModel());
            if ("hdfs".equals(url.getProtocol())) {
                fs.copyToLocalFile(new org.apache.hadoop.fs.Path(
                        configuration.getModel()),
                        new org.apache.hadoop.fs.Path(tmpFile.toString()));

            } else if ("http".equals(url.getProtocol()) ||
                    "https".equals(url.getProtocol())) {

                BufferedInputStream in = new BufferedInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile.toString());
                byte dataBuffer[] = new byte[1024];
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
