package ru.simbirsoft.test;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.model.CommentModel;
import ru.simbirsoft.model.CommentRequestBody;

import java.util.List;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@Epic("Задание D1")
class CommentTests extends BaseTest {

    private static final String COMMENT_AUTHOR = "Комментатор";
    private static final String COMMENT_EMAIL = "autotest.comment@example.com";
    private static final String UPDATED_COMMENT_AUTHOR = "Комментатор 2";
    private static final String UPDATED_COMMENT_EMAIL = "autotest.updated@example.com";

    @Test
    @DisplayName("TC-WP-COMMENT-01: получение списка комментариев")
    void shouldReturnCommentInCommentsList() {
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
                .as("Должен присутствовать созданный комментарий с ID %s", commentId)
                .contains(commentId);

        assertThat(commentRepository.countAll())
                .as("В таблице wp_comments должен быть хотя бы один комментарий")
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-WP-COMMENT-02: добавление комментария")
    void shouldCreateCommentAndSaveInDatabase() {
        int postId = createPublishedPost("Создание комментария D1", "Текст записи D1");

        CommentRequestBody request = new CommentRequestBody(
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

        Optional<CommentModel> commentFromDb = commentRepository.findById(commentId);

        assertThat(commentFromDb)
                .as("Комментарий с ID %s должен быть найден в таблице wp_comments после создания", commentId)
                .isPresent();

        CommentModel comment = commentFromDb.get();

        assertThat(comment.postId())
                .as("Комментарий должен быть привязан к записи с ID %s", postId)
                .isEqualTo(postId);

        assertThat(comment.author())
                .as("В БД должно сохраниться имя автора комментария")
                .isEqualTo(COMMENT_AUTHOR);

        assertThat(comment.authorEmail())
                .as("В БД должен сохраниться email автора комментария")
                .isEqualTo(COMMENT_EMAIL);

        assertThat(comment.content())
                .as("В БД должен сохраниться текст созданного комментария")
                .contains("Комментарий D1");

        assertThat(comment.approved())
                .as("Созданный комментарий должен быть одобрен, в БД comment_approved должен быть равен 1")
                .isEqualTo("1");
    }

    @Test
    @DisplayName("TC-WP-COMMENT-03: редактирование комментария")
    void shouldUpdateCommentInDatabase() {
        int postId = createPublishedPost("Редактирование комментария D1", "Текст записи D1");
        int commentId = createComment(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий D1",
                "approved"
        );

        CommentRequestBody updateRequest = new CommentRequestBody(
                null,
                "Комментатор 2",
                "autotest.updated@example.com",
                "Комментарий был изменён",
                "approved"
        );

        commentRequests.update(commentId, updateRequest)
                .then()
                .statusCode(HTTP_OK);

        Optional<CommentModel> commentFromDb = commentRepository.findById(commentId);

        assertThat(commentFromDb)
                .as("Комментарий с ID %s должен быть найден в таблице wp_comments после редактирования", commentId)
                .isPresent();

        CommentModel comment = commentFromDb.get();

        assertThat(comment.author())
                .as("После редактирования в БД должно обновиться имя автора комментария")
                .isEqualTo(UPDATED_COMMENT_AUTHOR);

        assertThat(comment.authorEmail())
                .as("После редактирования в БД должен обновиться email автора комментария")
                .isEqualTo(UPDATED_COMMENT_EMAIL);

        assertThat(comment.content())
                .as("После редактирования в БД должен обновиться текст комментария")
                .contains("Комментарий был изменён");

        assertThat(comment.approved())
                .as("После редактирования комментарий должен остаться одобренным")
                .isEqualTo("1");
    }

    @Test
    @DisplayName("TC-WP-COMMENT-04: удаление комментария в корзину")
    void shouldMoveCommentToTrash() {
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
                .as("Удалённый комментарий с ID %s не должен отображаться среди активных", commentId)
                .doesNotContain(commentId);

        Optional<CommentModel> commentFromDb = commentRepository.findById(commentId);

        assertThat(commentFromDb)
                .as("Комментарий с ID %s должен остаться в таблице wp_comments после перемещения в корзину", commentId)
                .isPresent();

        CommentModel comment = commentFromDb.get();

        assertThat(comment.approved())
                .as("Поле comment_approved должно быть равно trash")
                .isEqualTo("trash");
    }

    @Test
    @DisplayName("TC-WP-COMMENT-05: одобрение комментария")
    void shouldApprovePendingComment() {
        int postId = createPublishedPost("Одобрение комментария D1", "Текст записи D1");
        int commentId = createComment(
                postId,
                COMMENT_AUTHOR,
                COMMENT_EMAIL,
                "Комментарий на проверке D1",
                "hold"
        );

        Optional<CommentModel> pendingCommentFromDb = commentRepository.findById(commentId);

        assertThat(pendingCommentFromDb)
                .as("Комментарий с ID %s должен быть найден перед одобрением", commentId)
                .isPresent();

        CommentModel pendingComment = pendingCommentFromDb.get();

        assertThat(pendingComment.approved())
                .as("Перед одобрением комментарий должен быть в ожидания проверки")
                .isEqualTo("0");

        CommentRequestBody approveRequest = new CommentRequestBody(
                null,
                null,
                null,
                null,
                "approved"
        );

        commentRequests.update(commentId, approveRequest)
                .then()
                .statusCode(HTTP_OK);

        Optional<CommentModel> approvedCommentFromDb = commentRepository.findById(commentId);

        assertThat(approvedCommentFromDb)
                .as("Комментарий с ID %s должен быть найден в БД после одобрения", commentId)
                .isPresent();

        CommentModel approvedComment = approvedCommentFromDb.get();

        assertThat(approvedComment.approved())
                .as("После одобрения поле comment_approved должно быть равно 1")
                .isEqualTo("1");
    }
}