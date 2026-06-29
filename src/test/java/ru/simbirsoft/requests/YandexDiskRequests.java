package ru.simbirsoft.requests;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static ru.simbirsoft.client.YandexDiskClient.authorizedYandexRequest;
import static ru.simbirsoft.client.YandexDiskClient.unauthorizedYandexRequest;
import static ru.simbirsoft.endpoint.YandexDiskEndpoints.DISK_ROUTE;

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
}
