package br.ufmg.dcc.speed.lemonade.serving.dto;

public class FieldDto {
    private String name;
    private String type;
    
    public FieldDto() {
    }
    public FieldDto(String name, String type) {
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
