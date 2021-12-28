package br.ufmg.dcc.speed.lemonade.serving.dto;

import java.util.List;
/**
 * Representes a LeapFrame schema.
 */
public class SchemaDto {
    private List<FieldDto> fields;

    public List<FieldDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldDto> fields) {
        this.fields = fields;
    }

    
}
