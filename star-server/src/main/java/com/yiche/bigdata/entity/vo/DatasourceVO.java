package com.yiche.bigdata.entity.vo;

import org.hibernate.validator.constraints.NotBlank;

public class DatasourceVO {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    private String config;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DatasourceVO{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", config='" + config + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
