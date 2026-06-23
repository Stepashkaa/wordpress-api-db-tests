package ru.simbirsoft.model;

public record PostModel(
        int id,
        String title,
        String content,
        String status,
        String type
) {
}
