package ru.simbirsoft.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RenderedDto (
    String rendered
) {
}
