package ru.simbirsoft.test;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.model.PostModel;
import ru.simbirsoft.model.PostRequestBody;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.assertj.core.api.Assertions.assertThat;



@Epic("Задание D1")
public class PostTests extends BaseTest{

    private static final String POST_TYPE = "post";

    @Test
    @DisplayName("TC-WP-POST-01: получение списка записей")
    void getPublishedPosts(){
        int createdPostId = createPublishedPost("Запись для списка D1", "Текст записи для списка D1");

        Response response = postRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract()
                .response();

        List<Integer> apiPostIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPostIds)
                .contains(createdPostId);

        assertThat(postRepository.countByTypeAndStatus(POST_TYPE, "publish"))
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-WP-POST-02: добавление записи")
    void createPostShouldSavePostInDatabase() {
        PostRequestBody request = new PostRequestBody(
                "Запись D1",
                "Текст записи D1",
                "publish"
        );

        Response response = postRequests.create(request)
                .then()
                .statusCode(HTTP_CREATED)
                .extract()
                .response();

        int postId = response.jsonPath().getInt("id");
        rememberCreatedPost(postId);

        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new AssertionError("Запись не найдена в wp_posts"));

        assertThat(post.title()).isEqualTo("Запись D1");
        assertThat(post.content()).contains("Текст записи D1");
        assertThat(post.status()).isEqualTo("publish");
        assertThat(post.type()).isEqualTo(POST_TYPE);
    }

    @Test
    @DisplayName("TC-WP-POST-03: редактирование записи")
    void updatePost() {
        int postId = createPublishedPost("Запись D1", "Текст записи D1");

        PostRequestBody updateRequest = new PostRequestBody(
                "Запись D1 — изменена",
                "Обновлённый текст",
                "publish"
        );

        postRequests.update(postId, updateRequest)
                .then()
                .statusCode(HTTP_OK);

        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new AssertionError("Запись не найдена в wp_posts"));

        assertThat(post.title()).isEqualTo("Запись D1 — изменена");
        assertThat(post.content()).contains("Обновлённый текст");
        assertThat(post.status()).isEqualTo("publish");
        assertThat(post.type()).isEqualTo(POST_TYPE);
    }

    @Test
    @DisplayName("TC-WP-POST-04: удаление записи в корзину")
    void deletePostToTrash() {
        int postId = createPublishedPost("Запись для удаления D1", "Текст записи для удаления D1");

        postRequests.delete(postId, false)
                .then()
                .statusCode(HTTP_OK);

        Response response = postRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response();

        List<Integer> apiPostIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPostIds)
                .doesNotContain(postId);

        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new AssertionError("Запись не найдена в wp_posts"));

        assertThat(post.status()).isEqualTo("trash");
        assertThat(post.type()).isEqualTo(POST_TYPE);
    }

}
