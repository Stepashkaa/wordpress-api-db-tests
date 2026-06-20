package ru.simbirsoft.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.simbirsoft.model.CommentRequest;

import static ru.simbirsoft.endpoint.WordPressEndpoints.COMMENTS_ROUTE;
import static ru.simbirsoft.endpoint.WordPressEndpoints.INDEX;
import static ru.simbirsoft.endpoint.WordPressEndpoints.commentByIdRoute;
import static ru.simbirsoft.spec.WordPressApiSpec.authorizedRequest;

public class CommentClient {

    @Step("Получить список комментариев")
    public Response getAll() {
        return authorizedRequest()
                .queryParam("rest_route", COMMENTS_ROUTE)
                .when()
                .get(INDEX);
    }

    @Step("Создать комментарий")
    public Response create(CommentRequest request) {
        return authorizedRequest()
                .queryParam("rest_route", COMMENTS_ROUTE)
                .body(request)
                .when()
                .post(INDEX);
    }

    @Step("Обновить комментарий с ID: {id}")
    public Response update(int id, CommentRequest request) {
        return authorizedRequest()
                .queryParam("rest_route", commentByIdRoute(id))
                .body(request)
                .when()
                .post(INDEX);
    }

    @Step("Удалить комментарий с ID: {id}")
    public Response delete(int id, boolean force) {
        RequestSpecification request = authorizedRequest()
                .queryParam("rest_route", commentByIdRoute(id));

        if (force) {
            request.queryParam("force", true);
        }

        return request
                .when()
                .delete(INDEX);
    }
}