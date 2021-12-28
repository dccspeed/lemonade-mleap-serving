package br.ufmg.dcc.speed.lemonade.serving.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Used to parse JSON request. Similiar to MLeap Serving format, but adds some
 * extra properties.
 */
public class RequestDto {
    /**
     * Define which fields will be returned in response.
     */
    private List<String> selectFields;
    /**
     * If true, remove any field that is not scalar (e.g. SparseVector, 
     * DenseVector).
     */
    private boolean selectOnlyScalarFields = false;

    @JsonProperty
    public List<String> getSelectFields() {
        return selectFields;
    }
    @JsonProperty
    public boolean isSelectOnlyScalarFields() {
        return selectOnlyScalarFields;
    }

    public void setSelectOnlyScalarFields(boolean selectOnlyScalarFields) {
        this.selectOnlyScalarFields = selectOnlyScalarFields;
    }

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }
}
