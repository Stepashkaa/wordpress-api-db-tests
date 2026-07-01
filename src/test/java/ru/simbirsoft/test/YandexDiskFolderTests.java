package ru.simbirsoft.test;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.model.YandexTrashResourceModel;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class YandexDiskFolderTests extends YandexDiskBaseTest{

    private static final String FOLDER_NAME = "D4_AUTOTEST";
    private static final String FOLDER_PATH = "/" + FOLDER_NAME;

    private static final String FOLDER_NAME_DELETE = "D4_AUTOTEST_DELETE";
    private static final String FOLDER_PATH_DELETE = "/" + FOLDER_NAME_DELETE;

    private static final String FOLDER_NAME_RESTORE = "D4_AUTOTEST_RESTORE";
    private static final String FOLDER_PATH_RESTORE = "/" + FOLDER_NAME_RESTORE;

    @Test
    @DisplayName("Создание папки на Яндекс.Диске")
    void shouldCreateFolder() {
        yandexDiskRequests.createFolder(FOLDER_PATH)
                .then()
                .statusCode(HTTP_CREATED)
                .body("href", notNullValue())
                .body("method", notNullValue());

        addFolderToCleanUp(FOLDER_PATH);

        Response response = yandexDiskRequests.getFolderInfo(FOLDER_PATH)
                .then()
                .statusCode(HTTP_OK)
                .body("type", notNullValue())
                .body("name", notNullValue())
                .extract().response();

        String type = response.jsonPath().getString("type");
        String name = response.jsonPath().getString("name");

        assertThat(type)
                .as("В ответе поле type должен совпадать с значением dir")
                .isEqualTo("dir");

        assertThat(name)
                .as("В ответе поле name должен совпадать с названием папки которые мы создавали")
                .isEqualTo(FOLDER_NAME);

    }

    @Test
    @DisplayName("Удаление папки на Яндекс.Диске")
    void shouldDeleteFolderToTrash() {

        yandexDiskRequests.createFolder(FOLDER_PATH_DELETE)
                .then()
                .statusCode(HTTP_CREATED)
                .body("href", notNullValue())
                .body("method", notNullValue());
        addFolderToCleanUp(FOLDER_PATH_DELETE);

        yandexDiskRequests.deleteFolder(FOLDER_PATH_DELETE)
                .then()
                .statusCode(HTTP_NO_CONTENT);

        yandexDiskRequests.getFolderInfo(FOLDER_PATH_DELETE)
                .then()
                .statusCode(HTTP_NOT_FOUND);

        Response response = yandexDiskRequests.getTrashFolderInfo("/")
                .then()
                .statusCode(HTTP_OK)
                .body("_embedded.items", notNullValue())
                .extract().response();

        List<YandexTrashResourceModel> yandexTrashResourceModels = response.jsonPath()
                .getList("_embedded.items", YandexTrashResourceModel.class);

        YandexTrashResourceModel deletedFolder = yandexTrashResourceModels.stream()
                .filter(resource -> FOLDER_NAME_DELETE.equals(resource.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Удалённая папка не найдена в корзине"));

        assertThat(deletedFolder.getType())
                .as("В ответе поле type должен совпадать с значением dir")
                .isEqualTo("dir");

        assertThat(deletedFolder.getName())
                .as("В ответе поле name должен совпадать с названием папки которую мы удалили")
                .isEqualTo(FOLDER_NAME_DELETE);

        assertThat(deletedFolder.getOriginPath())
                .as("Поле origin_path должно содержать исходный путь удалённой папки")
                .contains(FOLDER_PATH_DELETE);
    }

    @Test
    @DisplayName("Восстановление папки из корзины на Яндекс.Диске")
    void shouldRestoreFolderFromTrash(){
        yandexDiskRequests.createFolder(FOLDER_PATH_RESTORE)
                .then()
                .statusCode(HTTP_CREATED)
                .body("href", notNullValue())
                .body("method", notNullValue());

        addFolderToCleanUp(FOLDER_PATH_RESTORE);

        yandexDiskRequests.deleteFolder(FOLDER_PATH_RESTORE)
                .then()
                .statusCode(HTTP_NO_CONTENT);

        Response trashResponse = yandexDiskRequests.getTrashFolderInfo("/")
                .then()
                .statusCode(HTTP_OK)
                .body("_embedded.items", notNullValue())
                .extract().response();

        List<YandexTrashResourceModel> trashResources  = trashResponse.jsonPath()
                .getList("_embedded.items", YandexTrashResourceModel.class);

        YandexTrashResourceModel deletedFolder = trashResources.stream()
                .filter(resource -> FOLDER_NAME_RESTORE.equals(resource.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Папка для восстановления не найдена в корзине"));

        yandexDiskRequests.restoreTrashFolder(deletedFolder.getPath())
                .then()
                .statusCode(HTTP_CREATED)
                .body("href", notNullValue())
                .body("method", notNullValue());

        Response response = yandexDiskRequests.getFolderInfo(FOLDER_PATH_RESTORE)
                .then()
                .statusCode(HTTP_OK)
                .body("type", notNullValue())
                .body("name", notNullValue())
                .extract().response();

        String type = response.jsonPath().getString("type");
        String name = response.jsonPath().getString("name");

        assertThat(type)
                .as("В ответе поле type должен совпадать с значением dir")
                .isEqualTo("dir");

        assertThat(name)
                .as("В ответе поле name должен совпадать с названием папки которые мы восстановили")
                .isEqualTo(FOLDER_NAME_RESTORE);
    }
}
