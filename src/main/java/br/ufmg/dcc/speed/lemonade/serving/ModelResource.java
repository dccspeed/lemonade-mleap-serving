package br.ufmg.dcc.speed.lemonade.serving;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import br.ufmg.dcc.speed.lemonade.serving.dto.ResponseDto;
import br.ufmg.dcc.speed.lemonade.serving.dto.TransformDto;

@Path("/model")
@Produces(MediaType.APPLICATION_JSON)
public class ModelResource {

    private AppConfig configuration;
    private String modelPath;
    private String status;

    public ModelResource(AppConfig configuration, String modelPath,
            String status) {
        this.configuration = configuration;
        this.setModelPath(modelPath);
        this.setStatus(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    @POST
    @Path("/infer")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResponseDto infer(@Context final HttpServletRequest request,
            @NotNull @Valid TransformDto options) {

        if ("OK".equals(status)) {
            return new ResponseDto("OK", configuration.getModel());
        } else {
            return new ResponseDto("ERROR", status);
        }
    }
}
