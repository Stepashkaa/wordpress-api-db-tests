package ru.simbirsoft.requests;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static ru.simbirsoft.client.YandexDiskClient.authorizedYandexRequest;
import static ru.simbirsoft.client.YandexDiskClient.unauthorizedYandexRequest;
import static ru.simbirsoft.endpoint.YandexDiskEndpoints.DISK_ROUTE;
import static ru.simbirsoft.endpoint.YandexDiskEndpoints.TRASH_RESTORE_RESOURCES_ROUTE;
import static ru.simbirsoft.endpoint.YandexDiskEndpoints.RESOURCES_ROUTE;
import static ru.simbirsoft.endpoint.YandexDiskEndpoints.TRASH_RESOURCES_ROUTE;

public class YandexDiskRequests {

    @Step("Получить информацию с OAuth-токеном")
    public Response getAuthorizedUserDiskInfo(){
        return authorizedYandexRequest()
                .when()
                .get(DISK_ROUTE);
    }

    @Step("Получить информацию без OAuth-токена")
    public Response getUnauthorizedUserDiskInfo(){
        return unauthorizedYandexRequest()
                .when()
                .get(DISK_ROUTE);
    }

    @Step("Создать папку")
    public Response createFolder(String path){
        return authorizedYandexRequest()
                .queryParam("path", path)
                .when()
                .put(RESOURCES_ROUTE);
    }

    @Step("Получение информации о существующей папке")
    public Response getFolderInfo(String path){
        return authorizedYandexRequest()
                .queryParam("path", path)
                .when()
                .get(RESOURCES_ROUTE);
    }

    @Step("Удаление существующей папки")
    public Response deleteFolder(String path){
        return authorizedYandexRequest()
                .queryParam("path", path)
                .when()
                .delete(RESOURCES_ROUTE);
    }

    @Step("Получение папки из корзины")
    public Response getTrashFolderInfo(String path){
        return authorizedYandexRequest()
                .queryParam("path", path)
                .queryParam("limit", 100)
                .when()
                .get(TRASH_RESOURCES_ROUTE);
    }

    @Step("Очистка корзины от тестовых папок")
    public Response deleteTrashFolder(String path){
        return authorizedYandexRequest()
                .queryParam("path", path)
                .when()
                .delete(TRASH_RESOURCES_ROUTE);
    }

    @Step("Восстановление папки из корзины")
    public Response restoreTrashFolder(String path){
        return authorizedYandexRequest()
                .queryParam("path", path)
                .when()
                .put(TRASH_RESTORE_RESOURCES_ROUTE);
    }
}
