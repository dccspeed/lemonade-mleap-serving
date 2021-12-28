package br.ufmg.dcc.speed.lemonade.serving.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO.
 */
public class ResponseDto {
    /**
     * Processing status. If equals to ERROR, an exception was thrown and details
     * are available in message.
     */
    private String status;
    /**
     * Processing status message.
     */
    private String message;

    /**
     * Rows
     */
    private List<Map<String, Object>> rows;

    public ResponseDto() {

    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public ResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseDto(String status, String message, List<Map<String, Object>> rows) {
        this(status, message);
        this.rows = rows;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
