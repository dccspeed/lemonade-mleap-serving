package br.ufmg.dcc.speed.lemonade.serving;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Configuration
 */
public class AppConfig extends Configuration {
    private String model;

    @JsonProperty
    public String getModel() {
        return model;
    }

    @JsonProperty
    public void setModel(String model) {
        this.model = model;
    }
}