package br.ufmg.dcc.speed.lemonade.serving.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Used to parse LeapFrame JSON. Same format used by MLeap Serving.
 */
public class TransformDto {
    private SchemaDto schema;
    private List<List<Object>> rows;
    private Set<String> selectedFields;
    private boolean returnOnlyScalarFields;

    @JsonProperty
    public SchemaDto getSchema() {
        return schema;
    }

    public boolean isReturnOnlyScalarFields() {
        return returnOnlyScalarFields;
    }

    public void setReturnOnlyScalarFields(boolean onlyScalarFields) {
        this.returnOnlyScalarFields = onlyScalarFields;
    }

    public Set<String> getSelectedFields() {
        return selectedFields;
    }

    public void setSelectedFields(Set<String> selectedFields) {
        this.selectedFields = selectedFields;
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
