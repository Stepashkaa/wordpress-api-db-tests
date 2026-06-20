package ru.simbirsoft.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentRequest (
        Integer post,

        @JsonProperty("author_name")
        String authorName,

        @JsonProperty("author_email")
        String authorEmail,

        String content,
        String status
){
}
