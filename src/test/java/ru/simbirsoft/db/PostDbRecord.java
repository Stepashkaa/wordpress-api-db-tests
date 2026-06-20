package ru.simbirsoft.db;

public record PostDbRecord(
        int id,
        String title,
        String content,
        String status,
        String type
) {
}
