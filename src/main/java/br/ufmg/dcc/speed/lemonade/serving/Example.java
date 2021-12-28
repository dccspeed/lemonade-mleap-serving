package br.ufmg.dcc.speed.lemonade.serving;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import ml.combust.mleap.core.Model;
import ml.combust.mleap.core.types.StructField;
import ml.combust.mleap.core.types.StructType;
import ml.combust.mleap.runtime.MleapContext;
import ml.combust.mleap.runtime.frame.DefaultLeapFrame;
import ml.combust.mleap.runtime.frame.Row;
import ml.combust.mleap.runtime.frame.Transformer;
import ml.combust.mleap.runtime.javadsl.BundleBuilder;
import ml.combust.mleap.runtime.javadsl.ContextBuilder;
import ml.combust.mleap.runtime.javadsl.LeapFrameBuilder;
import ml.combust.bundle.fs.BundleFileSystem;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.util.Try;

/**
 */
public final class Example {
    static LeapFrameBuilder frameBuilder = new LeapFrameBuilder();
    private Example() {
    }

    static DefaultLeapFrame buildFrame() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> map = mapper.readValue(Paths.get("d:/walter/tmp/frame.airbnb.json").toFile(), 
            Map.class);

        String s = mapper.writeValueAsString(map.get("schema"));
        StructType schema = frameBuilder.createSchema(s);
        /*
        List<StructField> fields = Arrays.asList(frameBuilder.createField("bool", frameBuilder.createBoolean()),
                frameBuilder.createField("string", frameBuilder.createString()),
                frameBuilder.createField("byte", frameBuilder.createByte()),
                frameBuilder.createField("short", frameBuilder.createShort()),
                frameBuilder.createField("int", frameBuilder.createInt()),
                frameBuilder.createField("long", frameBuilder.createLong()),
                frameBuilder.createField("float", frameBuilder.createFloat()),
                frameBuilder.createField("double", frameBuilder.createDouble()),
                frameBuilder.createField("byte_string", frameBuilder.createByteString()),
                frameBuilder.createField("list", frameBuilder.createList(frameBuilder.createBasicLong())),
                //frameBuilder.createField("map", frameBuilder.createMap(frameBuilder.createBasicString(), frameBuilder.createBasicDouble())),
                frameBuilder.createField("tensor", frameBuilder.createTensor(frameBuilder.createBasicDouble())));
        StructType schema = frameBuilder.createSchema(fields);
        
        Row row = frameBuilder.createRow(true, "hello", (byte) 1,
        (short) 2, 3, (long) 4, 34.5f, 44.5, new ByteString("hello_there".getBytes()),
        Arrays.asList(23, 44, 55), Vectors.dense(new double[]{23, 3, 4}));
        
        return frameBuilder.createFrame(schema, Arrays.asList(row));*/
        @SuppressWarnings("unchecked")
        List<Row> rows = ((List<List<Object>>) map.get("rows")).stream()
            .map(row -> frameBuilder.createRowFromIterable(row))
            .collect(Collectors.toList());

        return frameBuilder.createFrame(schema, rows);
    }

    /**
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Exception {
        MleapContext context = new ContextBuilder().createMleapContext();
        BundleBuilder bundleBuilder = new BundleBuilder();
        
        File file = new File("d:/walter/tmp/airbnb.model.lr.zip");
        Transformer transformer = bundleBuilder.load(file, context).root();
        
        System.out.println(transformer.inputSchema());
        System.out.println(transformer.outputSchema());

        Model model = transformer.model();
        
        BundleFileSystem fs = new BundleFileSystem() {

            @Override
            public Try<File> load(URI uri) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void save(URI uri, File localFile) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public Seq<String> schemes() {
                // TODO Auto-generated method stub
                return null;
            }
            
        };

        DefaultLeapFrame frame = buildFrame();
        Try<DefaultLeapFrame> result = transformer.transform(frame);
        //System.out.println(result);
        if (result.isFailure()){
            final Throwable error = result.failed().get();
        } else {
            DefaultLeapFrame defaultLeapFrame = result.get();
            List<Row> resultFrame = JavaConversions.seqAsJavaList(defaultLeapFrame.collect());
            List<StructField> fields = JavaConversions.seqAsJavaList(defaultLeapFrame.schema().fields());
            resultFrame.stream()
                .forEach(row -> {
                    IntStream.range(0, fields.size()).forEach(index -> {
                        StructField f = fields.get(index);
                        if ("scalar".equals(f.dataType().simpleString())){
                            System.out.println(f.name() + ": " + row.get(index));
                        } else {
                            java.util.Iterator<Object> it = JavaConversions.asJavaIterator(row.getTensor(index).toDense().rawValuesIterator());
                            System.out.print(f.name() + ": [");
                            while (it.hasNext()){
                                System.out.print(it.next() + ",");

                            }
                            System.out.println("]");
                            //System.out.println(f.dataType().() + " " + f.name() + ": " + row.get(index));
                        }
                    });
                });
        }
        //DefaultLeapFrame frame2 = transformer.transform(frame).get();
        
    }
    
}
