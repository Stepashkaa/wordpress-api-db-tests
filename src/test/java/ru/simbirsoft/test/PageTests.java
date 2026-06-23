package ru.simbirsoft.test;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.db.PostDbRecord;
import ru.simbirsoft.model.PageRequest;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@Epic("Задание D1")
class PageTests extends BaseTest {

    private static final String PAGE_TYPE = "page";

    @Test
    @DisplayName("TC-WP-PAGE-01: получение списка страниц")
    void getPublishedPages() {
        int createdPageId = createPublishedPage("Страница для списка D1", "Текст страницы для списка D1");

        Response response = pageRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract()
                .response();

        List<Integer> apiPageIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPageIds)
                .contains(createdPageId);

        assertThat(postRepository.countByTypeAndStatus(PAGE_TYPE, "publish"))
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-WP-PAGE-02: добавление страницы")
    void createPage() {
        PageRequest request = new PageRequest(
                "Страница D1",
                "Текст страницы",
                "publish"
        );

        Response response = pageRequests.create(request)
                .then()
                .statusCode(HTTP_CREATED)
                .extract()
                .response();

        int pageId = response.jsonPath().getInt("id");
        rememberCreatedPage(pageId);

        PostDbRecord page = postRepository.findById(pageId)
                .orElseThrow(() -> new AssertionError("Страница не найдена в wp_posts"));

        assertThat(page.title()).isEqualTo("Страница D1");
        assertThat(page.content()).contains("Текст страницы");
        assertThat(page.status()).isEqualTo("publish");
        assertThat(page.type()).isEqualTo(PAGE_TYPE);
    }

    @Test
    @DisplayName("TC-WP-PAGE-03: редактирование страницы")
    void updatePage() {
        int pageId = createPublishedPage("Страница D1", "Текст страницы");

        PageRequest updateRequest = new PageRequest(
                "Страница D1 — изменена",
                "Обновлённый текст",
                "publish"
        );

        pageRequests.update(pageId, updateRequest)
                .then()
                .statusCode(HTTP_OK);

        PostDbRecord page = postRepository.findById(pageId)
                .orElseThrow(() -> new AssertionError("Страница не найдена в wp_posts"));

        assertThat(page.title()).isEqualTo("Страница D1 — изменена");
        assertThat(page.content()).contains("Обновлённый текст");
        assertThat(page.status()).isEqualTo("publish");
        assertThat(page.type()).isEqualTo(PAGE_TYPE);
    }

    @Test
    @DisplayName("TC-WP-PAGE-04: удаление страницы в корзину")
    void deletePageToTrash() {
        int pageId = createPublishedPage("Страница для удаления D1", "Текст страницы для удаления D1");

        pageRequests.delete(pageId, false)
                .then()
                .statusCode(HTTP_OK);

        Response response = pageRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response();

        List<Integer> apiPageIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPageIds)
                .doesNotContain(pageId);

        PostDbRecord page = postRepository.findById(pageId)
                .orElseThrow(() -> new AssertionError("Страница не найдена в wp_posts"));

        assertThat(page.status()).isEqualTo("trash");
        assertThat(page.type()).isEqualTo(PAGE_TYPE);
    }
}