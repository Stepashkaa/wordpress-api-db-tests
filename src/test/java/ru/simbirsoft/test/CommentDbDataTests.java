package ru.simbirsoft.test;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.model.CommentResponseDto;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

public class CommentDbDataTests extends DbDataTestBase {

    private static final String POST_TITLE = "D2_title";
    private static final String POST_CONTENT = "D2_content";
    private static final String COMMENT_AUTHOR = "D2_author";
    private static final String COMMENT_EMAIL = "D2_author_email";
    private static final String COMMENT_CONTENT = "D2_content_comment";

    @Test
    @DisplayName("TC-WP-COMMENT-01: получение комментария, созданного в БД")
    void shouldReturnCommentCreatedInDatabase() {
        int postId = createPublishPostInDatabase(POST_TITLE, POST_CONTENT);
        int commentId = createApprovedCommentInDatabase(postId, COMMENT_AUTHOR, COMMENT_EMAIL, COMMENT_CONTENT);

        Response response = commentRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract().response();

        List<CommentResponseDto> apiComments = response.as(new TypeRef<>() {
        });

        CommentResponseDto comment = apiComments.stream()
                .filter(apiComment -> apiComment.id() == commentId)
                .findFirst()
                .orElse(null);

        assertThat(comment)
                .as("В ответе API должен присутствовать комментарий с ID %s, созданный в БД", commentId)
                .isNotNull();

        assertThat(comment.post())
                .as("Комментарий должен быть связан с записью, созданной в БД")
                .isEqualTo(postId);

        assertThat(comment.authorName())
                .as("Имя автора комментария соответствует имени, добавленному в БД")
                .isEqualTo(COMMENT_AUTHOR);

        assertThat(comment.content().rendered())
                .as("Контент комментария соответствует контенту, добавленному в БД")
                .contains(COMMENT_CONTENT);

    }
}
