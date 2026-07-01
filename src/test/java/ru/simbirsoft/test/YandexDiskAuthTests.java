package ru.simbirsoft.test;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.simbirsoft.requests.YandexDiskRequests;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static ru.simbirsoft.config.TestConfig.yandexDiskUserDisplayName;
import static ru.simbirsoft.config.TestConfig.yandexDiskUserLogin;

public class YandexDiskAuthTests {

    private final YandexDiskRequests yandexDiskRequests = new YandexDiskRequests();

    @Test
    @DisplayName("Успешная авторизация с валидным OAuth-токеном")
    void shouldAuthorizeWithValidToken(){
        Response response = yandexDiskRequests.getAuthorizedUserDiskInfo()
                .then()
                .statusCode(HTTP_OK)
                .body("user", notNullValue())
                .extract().response();

        String userLogin = response.jsonPath().getString("user.login");
        String userDisplayName = response.jsonPath().getString("user.display_name");

        assertThat(userLogin)
                .as("В ответе поле user.login должен совпадать с значением которое вернулось из запроса с валидным OAuth-токеном", yandexDiskUserLogin())
                .isEqualTo(yandexDiskUserLogin());

        assertThat(userDisplayName)
                .as("В ответе поле user.display_name должен совпадать с значением которое вернулось из запроса с валидным OAuth-токеном", yandexDiskUserDisplayName())
                .isEqualTo(yandexDiskUserDisplayName());
    }

    @Test
    @DisplayName("Ошибка авторизации без OAuth-токена")
    void shouldNotAuthorizeWithoutToken(){
        Response response = yandexDiskRequests.getUnauthorizedUserDiskInfo()
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("error", notNullValue())
                .body("description", notNullValue())
                .body("message", notNullValue())
                .extract().response();

        String error = response.jsonPath().getString("error");
        String description = response.jsonPath().getString("description");
        String message = response.jsonPath().getString("message");

        assertThat(error)
                .as("В ответе поле error должно быть UnauthorizedError")
                .isEqualTo("UnauthorizedError");

        assertThat(description)
                .as("В ответе поле description должно быть Unauthorized")
                .isEqualTo("Unauthorized");

        assertThat(message)
                .as("В ответе поле message должно быть Не авторизован.")
                .isEqualTo("Не авторизован.");
    }
}
