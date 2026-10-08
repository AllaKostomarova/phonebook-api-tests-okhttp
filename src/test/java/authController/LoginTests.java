package authController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.ApiConstants;
import dto.AuthReqBodyDTO;
import dto.AuthResponseDTO;
import dto.ErrorMessageDTO;
import helpers.SchemaValidator;
import okhttp3.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;

public class LoginTests {
    private static final Gson GSON = new Gson();
    private static final MediaType JSON = MediaType.get(ApiConstants.APPLICATION_JSON);
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final String LOGIN_URL = ApiConstants.BASEURL + ApiConstants.LOGIN_ENDPOINT;

    /**
     * The positive test checks that login with a valid registered username and password is successful.
     * <p>
     * Expected results:
     * - Status code: 200 (#1)
     * - The response body matches the token response schema (#2)
     * - The token is not null (#3)
     * - The token is not blank (#4)
     */
    @Test
    public void shouldReturnTokenWhenLoginWithValidData() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(ApiConstants.REGISTERED_LOGIN)
                .password(ApiConstants.REGISTERED_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 200,
                    "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkTokenDtoSchema(stringResBody);

            AuthResponseDTO responseDTO = GSON.fromJson(stringResBody, AuthResponseDTO.class);
            Assert.assertNotNull(responseDTO, "The AuthResponseDTO object should not be null.");
            String token = responseDTO.getToken();

            // #3
            Assert.assertNotNull(token,
                    "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should not be null.");
            // #4
            Assert.assertFalse(token.isBlank(), "The token should not be blank.");

        }
    }

    /**
     * The positive test checks that the repeated login requests with the same data are successful.
     * <p>
     * Expected results for each request:
     * - Status code: 200; (#1)
     * - The token is not null (#2)
     * - The token is not blank (#3)
     */
    @Test
    public void shouldReturnTokenWhenLoginRepeatedlyWithSameCredentials() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(ApiConstants.REGISTERED_LOGIN)
                .password(ApiConstants.REGISTERED_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        for (int i = 1; i < 3; i++) {
            try (Response response = CLIENT.newCall(request).execute()) {
                // #1
                Assert.assertEquals(response.code(), 200,
                        "Unexpected status code: " + response.code() + " for request number: " + i);
                ResponseBody responseBody = response.body();
                Assert.assertNotNull(responseBody, "The response body should not be null in request number: " + i);
                AuthResponseDTO responseDTO = GSON.fromJson(responseBody.string(), AuthResponseDTO.class);
                Assert.assertNotNull(responseDTO, "The AuthResponseDTO object should not be null.");
                String token = responseDTO.getToken();

                // #2
                Assert.assertNotNull(token,
                        "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should not be null in request number: " + i);
                // #3
                Assert.assertFalse(token.isBlank(),
                        "The token should not be blank in request number: " + i);
            }
        }
    }

    /**
     * The negative test checks that login with the registered username
     * and a new password is rejected.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'Error' field exists and equals "Unauthorized" (#3)
     * - The 'Message' field exists and equals "Login or Password incorrect" (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithRegisteredUsernameAndNewPassword() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(ApiConstants.REGISTERED_LOGIN)
                .password(ApiConstants.TEST_PART_PASSWORD + i).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody,
                    "The response body should not be null.");
            String stringResBody = responseBody.string();

            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);

            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        }
    }

    /**
     * The negative test checks that login with the registered username
     * and another registered user's password is rejected.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'Error' field exists and equals "Unauthorized" (#3)
     * - The 'Message' field exists and equals "Login or Password incorrect" (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithRegisteredUsernameAndAnotherPassword() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(ApiConstants.REGISTERED_LOGIN)
                .password(ApiConstants.ANOTHER_REGISTERED_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody,
                    "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        }
    }


    /**
     * The negative test checks that an unregistered user cannot log in.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'Error' field exists and equals "Unauthorized" (#3)
     * - The 'Message' field exists and equals "Login or Password incorrect" (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithUnregisteredUser() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder()
                .username(i + "unregistered@user.com")
                .password(i + ApiConstants.TEST_PART_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");

        }
    }

    /**
     * The negative test checks that login with the blank or empty username is rejected.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     * - The 'message' field exists and equals the expected error message (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithBlankUsername() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username("")
                .password(ApiConstants.REGISTERED_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        }
    }

    /**
     * The negative test checks that login with empty or blank password is rejected.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     * - The 'message' field exists und equals the expected message (#4)
     */

    @Test
    public void shouldReturnUnauthorizedWhenLoginWithBlankPassword() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(ApiConstants.REGISTERED_LOGIN)
                .password("").build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        }

    }

    /**
     * The negative test checks that login with empty or blank username and password is rejected.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     * - The 'message' field exists and equals the expected message (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithBlankUsernameAndPassword() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password("").username("").build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody,
                    "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        }

    }

    /**
     * The negative test checks that the system correctly handles invalid data in the 'username' field and
     * rejects login attempts with an invalid username format.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     * - The 'message' field exists and equals the expected message (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithInvalidUsernameFormat() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username("@" + ApiConstants.REGISTERED_LOGIN)
                .password(ApiConstants.REGISTERED_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL)
                .post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        }

    }

    /**
     * The negative test checks that the system correctly handles invalid data in the 'password' field and
     * rejects login attempts with an invalid password format.
     * <p>
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     * - The 'message' field exists and equals the expected message (#4)
     */
    @Test
    public void shouldReturnUnauthorizedWhenLoginWithInvalidPasswordFormat() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(ApiConstants.REGISTERED_LOGIN)
                .password(ApiConstants.TEST_PART_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(LOGIN_URL)
                .post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertEquals(errorMessageDTO.getMessage(), ApiConstants.AUTH_ERROR_TEXT,
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");

        }

    }
}
