package ru.simbirsoft.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.simbirsoft.config.TestConfig;

import static io.restassured.RestAssured.given;

public final class YandexDiskClient {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "OAuth ";

    private YandexDiskClient() {
    }

    public static RequestSpecification authorizedYandexRequest(){
        return baseYandexRequest().header(AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + TestConfig.yandexDiskToken());
    }

    public static RequestSpecification unauthorizedYandexRequest(){
        return baseYandexRequest();
    }

    public static RequestSpecification baseYandexRequest(){
        return given().
                baseUri(TestConfig.yandexDiskBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured());
    }
}
