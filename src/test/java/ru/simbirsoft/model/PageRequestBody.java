package ru.simbirsoft.model;

public record PageRequestBody(
        String title,
        String content,
        String status
){}