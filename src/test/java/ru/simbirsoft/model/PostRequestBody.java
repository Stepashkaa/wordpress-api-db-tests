package ru.simbirsoft.model;

public record PostRequestBody(
        String title,
        String content,
        String status
){}
