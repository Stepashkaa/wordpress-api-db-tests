package ru.simbirsoft.spec;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.simbirsoft.config.TestConfig;

import static io.restassured.RestAssured.given;

public final class WordPressApiSpec {
    private WordPressApiSpec() {
    }

    public static RequestSpecification authorizedRequest(){
        return given().
                baseUri(TestConfig.wordpressBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic(TestConfig.wordpressUsername(), TestConfig.wordpressPassword());

    }
}
