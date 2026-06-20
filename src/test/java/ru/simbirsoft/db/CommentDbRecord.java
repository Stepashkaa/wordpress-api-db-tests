package ru.simbirsoft.db;

public record CommentDbRecord(
        int id,
        int postId,
        String author,
        String authorEmail,
        String content,
        String approved
) {
}
