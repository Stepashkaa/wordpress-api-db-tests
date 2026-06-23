package ru.simbirsoft.requests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.simbirsoft.model.PostRequest;

import static ru.simbirsoft.endpoint.WordPressEndpoints.*;
import static ru.simbirsoft.spec.WordPressApiSpec.authorizedRequest;

public class PostRequests {
    @Step("Получить список записей")
    public Response getAll(){
        return authorizedRequest()
                .queryParam("rest_route", POSTS_ROUTE)
                .when()
                .get(INDEX);
    }

    @Step("Создать запись")
    public Response create(PostRequest request){
        return authorizedRequest()
                .queryParam("rest_route", POSTS_ROUTE)
                .body(request)
                .when()
                .post(INDEX);
    }

    @Step("Обновить запись с ID: {id}")
    public Response update(int id, PostRequest request) {
        return authorizedRequest()
                .queryParam("rest_route", postByIdRoute(id))
                .body(request)
                .when()
                .post(INDEX);
    }

    @Step("Удалить запись с ID: {id}")
    public Response delete(int id, boolean force) {
        RequestSpecification request = authorizedRequest()
                .queryParam("rest_route", postByIdRoute(id));

        if (force) {
            request.queryParam("force", true);
        }

        return request
                .when()
                .delete(INDEX);
    }
}
