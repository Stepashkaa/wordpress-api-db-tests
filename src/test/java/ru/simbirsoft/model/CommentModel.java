package ru.simbirsoft.model;

public record CommentModel(
        int id,
        int postId,
        String author,
        String authorEmail,
        String content,
        String approved
) {
}
