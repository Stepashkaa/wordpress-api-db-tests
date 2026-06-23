package ru.simbirsoft.test;

import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.model.PostModel;
import ru.simbirsoft.model.PageRequestBody;

import java.util.List;
import java.util.Optional;

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
    void shouldReturnPageInPagesList() {
        int createdPageId = createPublishedPage("Страница для списка D1", "Текст страницы для списка D1");

        Response response = pageRequests.getAll()
                .then()
                .statusCode(HTTP_OK)
                .body("$", not(empty()))
                .extract()
                .response();

        List<Integer> apiPageIds = response.jsonPath().getList("id", Integer.class);

        assertThat(apiPageIds)
                .as("Должна присутствовать созданная страница с ID %s", createdPageId)
                .contains(createdPageId);

        assertThat(postRepository.countByTypeAndStatus(PAGE_TYPE, "publish"))
                .as("В таблице wp_posts должна быть хотя бы одна опубликованная страница")
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("TC-WP-PAGE-02: добавление страницы")
    void shouldCreatePageItInDatabase() {
        PageRequestBody request = new PageRequestBody(
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

        Optional<PostModel> pageFromDb = postRepository.findById(pageId);

        assertThat(pageFromDb)
                .as("Страница с ID %s должна быть найдена в таблице wp_posts после создания", pageId)
                .isPresent();

        PostModel page = pageFromDb.get();

        assertThat(page.title())
                .as("В БД должен сохраниться заголовок созданной страницы")
                .isEqualTo("Страница D1");

        assertThat(page.content())
                .as("В БД должен сохраниться текст созданной страницы")
                .contains("Текст страницы");

        assertThat(page.status())
                .as("Созданная страница должна иметь статус publish")
                .isEqualTo("publish");

        assertThat(page.type())
                .as("Созданный объект должен иметь тип page")
                .isEqualTo(PAGE_TYPE);
    }

    @Test
    @DisplayName("TC-WP-PAGE-03: редактирование страницы")
    void shouldUpdatePageInDatabase() {
        int pageId = createPublishedPage("Страница D1", "Текст страницы");

        PageRequestBody updateRequest = new PageRequestBody(
                "Страница D1 — изменена",
                "Обновлённый текст",
                "publish"
        );

        pageRequests.update(pageId, updateRequest)
                .then()
                .statusCode(HTTP_OK);

        Optional<PostModel> pageFromDb = postRepository.findById(pageId);

        assertThat(pageFromDb)
                .as("Страница с ID %s должна быть найдена в таблице wp_posts после редактирования", pageId)
                .isPresent();

        PostModel page = pageFromDb.get();

        assertThat(page.title())
                .as("После редактирования должен обновиться заголовок страницы")
                .isEqualTo("Страница D1 — изменена");

        assertThat(page.content())
                .as("После редактирования должен обновиться текст страницы")
                .contains("Обновлённый текст");

        assertThat(page.status())
                .as("После редактирования страница должна остаться опубликованной")
                .isEqualTo("publish");

        assertThat(page.type())
                .as("После редактирования объект должен остаться страницей с post_type = page")
                .isEqualTo(PAGE_TYPE);
    }

    @Test
    @DisplayName("TC-WP-PAGE-04: удаление страницы в корзину")
    void shouldMovePageToTrash() {
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
                .as("Удалённая страница с ID %s не должна отображаться среди опубликованных страниц", pageId)
                .doesNotContain(pageId);

        Optional<PostModel> pageFromDb = postRepository.findById(pageId);

        assertThat(pageFromDb)
                .as("Страница с ID %s должна остаться в таблице wp_posts после удаления", pageId)
                .isPresent();

        PostModel page = pageFromDb.get();

        assertThat(page.status())
                .as("После удаления поле post_status должно быть равно trash")
                .isEqualTo("trash");

        assertThat(page.type())
                .as("После удаления в корзину объект должен остаться страницей")
                .isEqualTo(PAGE_TYPE);
    }
}