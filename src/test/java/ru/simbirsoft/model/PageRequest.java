package ru.simbirsoft.model;

public record PageRequest (
        String title,
        String content,
        String status
){}