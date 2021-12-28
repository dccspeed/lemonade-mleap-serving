package br.ufmg.dcc.speed.lemonade.serving.dto;
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

    public ResponseDto(){

    }
    public ResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
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
