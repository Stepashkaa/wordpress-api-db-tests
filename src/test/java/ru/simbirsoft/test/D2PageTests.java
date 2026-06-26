package ru.simbirsoft.test;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

public class D2PageTests extends D2BaseTest{

    private static final String PAGE_TITLE = "D2_title";
    private static final String PAGE_CONTENT = "D2_content";

    @Test
    @DisplayName("TC-WP-PAGE-01: Получение страницы, созданной в БД")
    void shouldReturnPageCreatedInDatabase() {
        int pageId = createPublishPageInDatabase(PAGE_TITLE, PAGE_CONTENT);

        Response response = pageRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract().response();

        List<Integer> apiPageIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPageIds)
                .as("В ответе API должна быть страница с ID %s, созданная в БД", pageId)
                .contains(pageId);

        String actualTitle = response.jsonPath()
                .getString("find { it.id == " + pageId + " }.title.rendered");

        String actualContent = response.jsonPath()
                .getString("find { it.id == " + pageId + " }.content.rendered");

        String actualType = response.jsonPath()
                .getString("find { it.id == " + pageId + " }.type");

        String actualStatus = response.jsonPath()
                .getString("find { it.id == " + pageId + " }.status");

        assertThat(actualTitle)
                .as("Заголовок страницы должен совпадать с заголовком из БД")
                .contains(PAGE_TITLE);

        assertThat(actualContent)
                .as("Контент страницы должен совпадать с контентом из БД")
                .contains(PAGE_CONTENT);

        assertThat(actualType)
                .as("Тип объекта из API должен быть page")
                .isEqualTo("page");

        assertThat(actualStatus)
                .as("Статус страницы из API должен быть publish")
                .isEqualTo("publish");
    }

}
