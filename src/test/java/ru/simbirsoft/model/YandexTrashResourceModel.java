package ru.simbirsoft.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexTrashResourceModel {

    private String path;
    private String name;
    private String type;

    @JsonProperty("origin_path")
    private String originPath;
}
