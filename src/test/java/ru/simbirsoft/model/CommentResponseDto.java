package ru.simbirsoft.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommentResponseDto(
        int id,
        int post,

        @JsonProperty("author_name")
        String authorName,

        RenderedDto content
) {
}