package ru.simbirsoft.model;

public record PostRequest (
        String title,
        String content,
        String status
){}
