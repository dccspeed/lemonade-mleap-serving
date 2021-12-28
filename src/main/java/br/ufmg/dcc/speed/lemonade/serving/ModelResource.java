package br.ufmg.dcc.speed.lemonade.serving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufmg.dcc.speed.lemonade.serving.dto.ResponseDto;
import br.ufmg.dcc.speed.lemonade.serving.dto.TransformDto;
import ml.combust.mleap.core.types.StructField;
import ml.combust.mleap.core.types.StructType;
import ml.combust.mleap.runtime.frame.DefaultLeapFrame;
import ml.combust.mleap.runtime.frame.Row;
import ml.combust.mleap.runtime.frame.Transformer;
import ml.combust.mleap.runtime.javadsl.LeapFrameBuilder;
import scala.collection.JavaConversions;
import scala.util.Try;

@Path("/model")
@Produces(MediaType.APPLICATION_JSON)
public class ModelResource {
    private LeapFrameBuilder frameBuilder = new LeapFrameBuilder();
    private AppConfig configuration;
    private String modelPath;
    private String status;
    private Transformer transformer;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ModelResource(AppConfig configuration, Transformer transformer, String modelPath,
            String status) {
        this.configuration = configuration;
        this.transformer = transformer;
        this.modelPath = modelPath;
        this.status = status;
        logger.info("Model resource loaded with status {} and model {}",
                status, modelPath);
    }

    public String getStatus() {
        return status;
    }

    public String getModelPath() {
        return modelPath;
    }

    private DefaultLeapFrame buildFrame(TransformDto dto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(dto.getSchema());
        StructType schema = frameBuilder.createSchema(s);

        List<Row> rows = ((List<List<Object>>) dto.getRows()).stream()
                .map(row -> frameBuilder.createRowFromIterable(row))
                .collect(Collectors.toList());

        return frameBuilder.createFrame(schema, rows);
    }

    @POST
    @Path("/infer")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResponseDto infer(@Context final HttpServletRequest request,
            @NotNull @Valid TransformDto dto) {

        ResponseDto responseDto;
        if ("OK".equals(status)) {
            try {
                DefaultLeapFrame leapFrame = buildFrame(dto);
                Try<DefaultLeapFrame> result = transformer.transform(leapFrame);
                if (result.isFailure()) {
                    final Throwable error = result.failed().get();
                    responseDto = new ResponseDto("ERROR", error.getLocalizedMessage());
                } else {

                    DefaultLeapFrame defaultLeapFrame = result.get();
                    responseDto = new ResponseDto("OK", "Success",
                            convertToResponseDto(defaultLeapFrame, dto));
                }
            } catch (Exception e) {
                responseDto = new ResponseDto("ERROR", e.getLocalizedMessage());
            }
        } else {
            responseDto = new ResponseDto("ERROR", "App inicialization failure");
        }
        return responseDto;
    }

    private List<Map<String, Object>> convertToResponseDto(DefaultLeapFrame defaultLeapFrame,
            TransformDto dto) {
        List<Row> resultFrame = JavaConversions.seqAsJavaList(
                defaultLeapFrame.collect());
        List<StructField> fields = JavaConversions.seqAsJavaList(
                defaultLeapFrame.schema().fields());

        List<Map<String, Object>> rows = new ArrayList<>();

        resultFrame.stream()
                .forEach(row -> {
                    Map<String, Object> resultRow = new HashMap<>();
                    rows.add(resultRow);
                    IntStream.range(0, fields.size()).forEach(index -> {
                        StructField f = fields.get(index);
                        Set<String> selected = dto.getSelectedFields();
                        if (selected == null || selected.isEmpty()
                                || selected.contains(f.name())) {
                            if ("scalar".equals(f.dataType().simpleString())) {
                                resultRow.put(f.name(), row.get(index));
                            } else if (!dto.isReturnOnlyScalarFields()) {
                                List<Object> values = new ArrayList<>();
                                java.util.Iterator<Object> it = JavaConversions
                                        .asJavaIterator(row.getTensor(index)
                                                .toDense().rawValuesIterator());
                                it.forEachRemaining(values::add);

                                resultRow.put(f.name(), values);
                            }
                        }
                    });
                });
        return rows;
    }

    @GET
    public String get() {
        return "OK";
    }
}
