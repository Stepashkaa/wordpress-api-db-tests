package ru.simbirsoft.test;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

public class D2CommentTests extends D2BaseTest {

    private static final String POST_TITLE = "D2_title";
    private static final String POST_CONTENT = "D2_content";
    private static final String COMMENT_AUTHOR = "D2_author";
    private static final String COMMENT_EMAIL  = "D2_author_email";
    private static final String COMMENT_CONTENT  = "D2_content_comment";

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

        List<Integer> apiComments = response.jsonPath().getList("id", Integer.class);

        assertThat(apiComments)
                .as("В ответе API должен присутствовать комментарий с ID %s, созданный в БД", commentId)
                .contains(commentId);

        Integer actualPostId = response.jsonPath().getInt("find { it.id == " + commentId + " }.post");

        String authorName = response.jsonPath().getString("find { it.id == " + commentId + " }.author_name");

        String actualContent = response.jsonPath().getString("find { it.id == " + commentId + " }.content.rendered");

        assertThat(actualPostId)
                .as("Комментарий должен быть связан с записью, созданной в БД")
                .isEqualTo(postId);

        assertThat(authorName)
                .as("Имя автора комментария соответствует имени, добавленному в БД")
                .isEqualTo(COMMENT_AUTHOR);

        assertThat(actualContent)
                .as("Контент комментария соответствует контенту, добавленному в БД")
                .contains(COMMENT_CONTENT);
    }
}
