package br.ufmg.dcc.speed.lemonade.serving.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Used to parse LeapFrame JSON. Same format used by MLeap Serving.
 */
public class TransformDto {
    private SchemaDto schema;
    private List<List<Object>> rows;

    @JsonProperty
    public SchemaDto getSchema() {
        return schema;
    }

    @JsonProperty
    public List<List<Object>> getRows() {
        return rows;
    }

    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    public void setSchema(SchemaDto schema) {
        this.schema = schema;
    }
}
