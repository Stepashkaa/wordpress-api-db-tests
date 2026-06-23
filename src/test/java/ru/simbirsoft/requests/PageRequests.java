package ru.simbirsoft.requests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.simbirsoft.model.PageRequest;

import static ru.simbirsoft.endpoint.WordPressEndpoints.INDEX;
import static ru.simbirsoft.endpoint.WordPressEndpoints.PAGES_ROUTE;
import static ru.simbirsoft.endpoint.WordPressEndpoints.pageByIdRoute;
import static ru.simbirsoft.spec.WordPressApiSpec.authorizedRequest;

public class PageRequests {

    @Step("Получить список страниц")
    public Response getAll() {
        return authorizedRequest()
                .queryParam("rest_route", PAGES_ROUTE)
                .when()
                .get(INDEX);
    }

    @Step("Создать страницу")
    public Response create(PageRequest request) {
        return authorizedRequest()
                .queryParam("rest_route", PAGES_ROUTE)
                .body(request)
                .when()
                .post(INDEX);
    }

    @Step("Обновить страницу с ID: {id}")
    public Response update(int id, PageRequest request) {
        return authorizedRequest()
                .queryParam("rest_route", pageByIdRoute(id))
                .body(request)
                .when()
                .post(INDEX);
    }

    @Step("Удалить страницу с ID: {id}")
    public Response delete(int id, boolean force) {
        RequestSpecification request = authorizedRequest()
                .queryParam("rest_route", pageByIdRoute(id));

        if (force) {
            request.queryParam("force", true);
        }

        return request
                .when()
                .delete(INDEX);
    }
}