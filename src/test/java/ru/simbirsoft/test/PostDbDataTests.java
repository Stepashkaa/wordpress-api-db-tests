package ru.simbirsoft.test;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@Epic("Задание D2")
public class PostDbDataTests extends DbDataTestBase {

    private static final String POST_TITLE = "D2_title";
    private static final String POST_CONTENT = "D2_content";

    @Test
    @DisplayName("TC-WP-POST-01: Получение записи, созданной в БД")
    void shouldReturnPostCreatedInDatabase() {
        int postId = createPublishPostInDatabase(POST_TITLE, POST_CONTENT);

        Response response = postRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract().response();

        List<Integer> apiPostIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPostIds)
                .as("В ответе API должна быть запись с ID %s, созданная напрямую в БД", postId)
                .contains(postId);

        String actualTitle = response.jsonPath()
                .getString("find { it.id == " + postId + " }.title.rendered");

        String actualContent = response.jsonPath()
                .getString("find { it.id == " + postId + " }.content.rendered");

        assertThat(actualTitle)
                .as("Заголовок записи должен совпадать с заголовком из БД")
                .contains(POST_TITLE);

        assertThat(actualContent)
                .as("Контент записи должен совпадать с контентом из БД")
                .contains(POST_CONTENT);
    }

    @Test
    @DisplayName("TC-WP-POST-02: Получение записи по ID, созданной в БД")
    void shouldReturnDatabasePostById(){
        String postTitle = "post_by_id";
        String postContent = "post_content_by_id";

        int postId = createPublishPostInDatabase(postTitle, postContent);
        Response response = postRequests.getById(postId)
                .then()
                .statusCode(HTTP_OK)
                .extract().response();

        int actualPostId = response.jsonPath().getInt("id");
        String actualTitle = response.jsonPath().getString("title.rendered");
        String actualContent = response.jsonPath().getString("content.rendered");
        String actualStatus = response.jsonPath().getString("status");
        String actualType = response.jsonPath().getString("type");

        assertThat(actualPostId)
                .as("ID записи должен совпадать с ID записи, созданной напрямую в БД")
                .isEqualTo(postId);

        assertThat(actualTitle)
                .as("Заголовок записи должен совпадать с заголовком из БД")
                .contains(postTitle);

        assertThat(actualContent)
                .as("Контент записи должен совпадать с контентом из БД")
                .contains(postContent);

        assertThat(actualStatus)
                .as("Статус записи из API должен быть publish")
                .isEqualTo("publish");

        assertThat(actualType)
                .as("Тип объекта из API должен быть post")
                .isEqualTo("post");
    }
}
