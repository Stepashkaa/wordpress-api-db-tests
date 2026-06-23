package ru.simbirsoft.test;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.db.CommentDbRecord;
import ru.simbirsoft.model.CommentRequest;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@Epic("Задание D1")
class CommentTests extends BaseTest {

    private static final String COMMENT_AUTHOR = "Комментатор";
    private static final String COMMENT_EMAIL = "autotest.comment@example.com";

    @Test
    @DisplayName("TC-WP-COMMENT-01: получение списка комментариев")
    void getComments() {
        int postId = createPublishedPost("Запись для комментария D1", "Текст записи для комментария D1");
        int commentId = createComment(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий D1",
                "approved"
        );

        Response response = commentRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract()
                .response();

        List<Integer> apiCommentIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiCommentIds)
                .contains(commentId);

        assertThat(commentRepository.countAll())
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-WP-COMMENT-02: добавление комментария")
    void createComment() {
        int postId = createPublishedPost("Создание комментария D1", "Текст записи D1");

        CommentRequest request = new CommentRequest(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий D1",
                "approved"
        );

        Response response = commentRequests.create(request)
                .then()
                .statusCode(HTTP_CREATED)
                .extract()
                .response();

        int commentId = response.jsonPath().getInt("id");
        rememberCreatedComment(commentId);

        CommentDbRecord comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AssertionError("Комментарий не найден в wp_comments"));

        assertThat(comment.postId()).isEqualTo(postId);
        assertThat(comment.author()).isEqualTo(COMMENT_AUTHOR);
        assertThat(comment.authorEmail()).isEqualTo(COMMENT_EMAIL);
        assertThat(comment.content()).contains("Комментарий D1");
        assertThat(comment.approved()).isEqualTo("1");
    }

    @Test
    @DisplayName("TC-WP-COMMENT-03: редактирование комментария")
    void updateComment() {
        int postId = createPublishedPost("Редактирование комментария D1", "Текст записи D1");
        int commentId = createComment(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий D1",
                "approved"
        );

        CommentRequest updateRequest = new CommentRequest(
                null,
                "Комментатор 2",
                "autotest.updated@example.com",
                "Комментарий был изменён",
                "approved"
        );

        commentRequests.update(commentId, updateRequest)
                .then()
                .statusCode(HTTP_OK);

        CommentDbRecord comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AssertionError("Комментарий не найден в wp_comments"));

        assertThat(comment.author()).isEqualTo("Комментатор 2");
        assertThat(comment.authorEmail()).isEqualTo("autotest.updated@example.com");
        assertThat(comment.content()).contains("Комментарий был изменён");
        assertThat(comment.approved()).isEqualTo("1");
    }

    @Test
    @DisplayName("TC-WP-COMMENT-04: удаление комментария в корзину")
    void deleteCommentToTrash() {
        int postId = createPublishedPost("Удаления комментария D1", "Текст записи D1");
        int commentId = createComment(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий для удаления D1",
                "approved"
        );

        commentRequests.delete(commentId, false)
                .then()
                .statusCode(HTTP_OK);

        Response response = commentRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response();

        List<Integer> apiCommentIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiCommentIds)
                .doesNotContain(commentId);

        CommentDbRecord comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AssertionError("Комментарий не найден в wp_comments"));

        assertThat(comment.approved()).isEqualTo("trash");
    }

    @Test
    @DisplayName("TC-WP-COMMENT-05: одобрение комментария")
    void approvePendingComment() {
        int postId = createPublishedPost("Одобрение комментария D1", "Текст записи D1");
        int commentId = createComment(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий на проверке D1",
                "hold"
        );

        CommentDbRecord pendingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AssertionError("Комментарий не найден в wp_comments"));

        assertThat(pendingComment.approved()).isEqualTo("0");

        CommentRequest approveRequest = new CommentRequest(
                null,
                null,
                null,
                null,
                "approved"
        );

        commentRequests.update(commentId, approveRequest)
                .then()
                .statusCode(HTTP_OK);

        CommentDbRecord approvedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AssertionError("Комментарий не найден в wp_comments"));

        assertThat(approvedComment.approved()).isEqualTo("1");
    }
}