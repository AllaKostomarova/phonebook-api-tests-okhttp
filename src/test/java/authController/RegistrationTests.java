package authController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.ApiConstants;
import dto.AuthReqBodyDTO;
import dto.AuthResponseDTO;
import dto.ErrorMessageDTO;
import dto.MessageFieldDTO;
import helpers.SchemaValidator;
import okhttp3.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;

public class RegistrationTests {

    /* Create the Gson object (class variable):
    - converts a Java object to JSON;
    - converts JSON to a Java object
     */
    private static final Gson GSON = new Gson();

    /* Create the OkHttpClient object:
    - sends requests;
    - acts as the [Send] button in Postman
     */
    private static final OkHttpClient CLIENT = new OkHttpClient();

    /* Create the MediaType object:
    - specifies the type of the request body data;
    - tells OkHttp that the request body contains JSON data.
     */
    private static final MediaType JSON = MediaType.get(ApiConstants.APPLICATION_JSON);

    private static final String REG_URL = ApiConstants.BASEURL + ApiConstants.REGISTR_ENDPOINT;


    /**
     * The positive test "shouldReturnTokenWhenRegisteringNewUserWithValidData()" checks that the Phonebook API
     * returns a token when registering a new user with a valid unique email and a valid password.
     * Expected results:
     * - Status code: 200. (#1)
     * - Token is not null. (#2)
     * - Token is not blank. (#3)
     * - Response body matches the TokenDto schema. (#4)
     */
    @Test
    public void shouldReturnTokenWhenRegisteringNewUserWithValidData() throws IOException {

        //Create a number based on the current time to generate different user data for registration.
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);

        //Create the Java object for the request body
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder()
                .password(i + ApiConstants.TEST_PART_PASSWORD)
                .username(i + "new.testuser@register.com")
                .build();
        //System.out.println(i+"new.testuser@regisrer.com");

        /*
        Create the request body:
        - the variable type is RequestBody;
        - RequestBody class calls the method "create".
        The method arguments:
        1) GSON converts the Java object reqBodyDTO to JSON using the method "toJson" (Java --> JSON);
        2) JSON specifies the MediaType of the request body.
         */
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);

        /*
        Create the request using the Request.Builder pattern.
        - Request.Builder is a builder class provided by OkHttp;
        - the variable type is Request.
        The request contains:
        - full url (base url + endpoint);
        - HTTP method POST;
        - JSON request body
         */
        Request request = new Request.Builder()
                .url(REG_URL).post(requestBody).build();

        /*
        Send the request and get the response using CLIENT (OkHttpClient object):
        CLIENT calls:
        - the method .newCall(request) to create a Call object;
        - the method .execute() to send the request and get the response.
        The result is a Response object

        Use try-with-resources:
        - Response uses resources that must be closed after use;
        - try automatically calls response.close() when the try block finishes;
        - Response is closed even if an exception occurs or an Assert fails.
         */
        try (Response response = CLIENT.newCall(request).execute()) {

        /*
        #1 Check the status code 2xx (200-299) using the boolean method .isSuccessful():
        must select: .assertTrue() or .assertFalse()
         */
            Assert.assertTrue(response.isSuccessful(), "Actual code: " + response.code());

        /*
        #1 Check that the status code == 200 using the int method .code()
        must select: .assertEquals()
         */
            Assert.assertEquals(response.code(), 200, "Unexpected status code.");

        /*
        Convert the response body from JSON to an AuthResponseDTO Java object.
        - .body() gets the response body as the ResponseBody object;
        - the ResponseBody object must be converted to the type String;
        - .string() reads the response body and returns it as a String;
        - GSON.fromJson() converts the JSON string to an AuthResponseDTO object;
        - AuthResponseDTO.class specifies the type of the Java object to create
         */
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            AuthResponseDTO resBodyDTO = GSON.fromJson(stringResBody, AuthResponseDTO.class);

            // Get the token
            String token = resBodyDTO.getToken();
            //System.out.println(token);
            /*
            These checks partially duplicate the schema validation intentionally:
            - the schema validation checks the response structure,
            - while these assertions verify the token value as part of the functional test.
             */
            // #2 Check that the "token" field is not null
            Assert.assertNotNull(token, "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should not be null.");

            // #3 Check that the "token" field is not blank
            Assert.assertFalse(token.isBlank(), "Value for the token is missing.");

            // #4 Check the TokenDto schema
            SchemaValidator.checkTokenDtoSchema(stringResBody);
        }
    }

    /**
     * The positive test "shouldReturnTokenWhenRegisteringNewUserWithExistingPassword" checks that the Phonebook API
     * returns a token when registering a new user with a valid unique email and an existing password.
     * Expected results:
     * - Status code: 200. (#1)
     * - Token is not null. (#2)
     * - Token is not blank. (#3)
     * - Response body matches the TokenDto schema. (#4)
     */

    @Test
    public void shouldReturnTokenWhenRegisteringNewUserWithExistingPassword() throws IOException {
        // Create a random number
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        // Create a request body as a Java object
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder()
                .username(i + "existing@password.com")
                .password(ApiConstants.REGISTERED_PASSWORD)
                .build();
        // Create a RequestBody with JSON using RequestBody.create() and GSON
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        // Create a Request using Request.Builder()
        Request request = new Request.Builder()
                .url(REG_URL).post(requestBody).build();
        /*
        Send the request and get a response.
        Use try-with-resources
         */
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1 Check the status code 200
            Assert.assertEquals(response.code(), 200, "Unexpected status code.");
            // Convert the response body from JSON to a Java object
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            AuthResponseDTO responseDTO = GSON.fromJson(stringResBody, AuthResponseDTO.class);
            // #2 Check that the token field in Java object is not null
            Assert.assertNotNull(responseDTO.getToken(),
                    "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should not be null.");
            // #3 Check that the token is not blank
            Assert.assertFalse(responseDTO.getToken().isBlank(), "Value for the token is missing.");
            // #4 Check the TokenDto schema
            SchemaValidator.checkTokenDtoSchema(stringResBody);
        }
    }

    /**
     * The negative test "shouldReturnBadRequestWhenRegisteringWithInvalidEmail" checks that the Phonebook API
     * returns the error "Bad Request" when registering a new user with an invalid email and a valid password.
     * Expected results:
     * - Status code: 400. (#1)
     * - The "error" field equals "Bad Request". (#2)
     * - The "password" field in the "message" object is null. (#3)
     * - The "username" field in the "message" object equals the expected error message (#4)
     * - Response body matches the ErrorMessageDto (Message - Object) schema. (#5)
     * <p>
     * Note:
     * The exact validation message is not defined by the API contract.
     * The current API response is used as the expected value to detect
     * unexpected changes in API behavior.
     */
    @Test
    public void shouldReturnBadRequestWhenRegisteringWithInvalidEmail() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder()
                .username(i + "invalid.email")
                .password(i + ApiConstants.TEST_PART_PASSWORD)
                .build();
        // For debugging
        // String requestJson = GSON.toJson(reqBodyDTO);
        // System.out.println("REQUEST: " + requestJson);
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1 Check the status code 400
            Assert.assertEquals(response.code(), 400, "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // For debugging
            // System.out.println("RESPONSE: " + responseBody);

            /*
            Specify the type of the 'message' field in ErrorMessageDTO:
            Type type = new TypeToken<ErrorMessageDTO<String>>() {}.getType();
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>(){}.getType();
             */
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>() {
            }.getType();
            //Specify the generic type when declaring the ErrorMessageDTO variable
            ErrorMessageDTO<MessageFieldDTO> errorMessageDTO =
                    GSON.fromJson(stringResBody, type);
            // #2 Check that the "error" field equals "Bad Request"
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #3 Check that the "password" field is null
            MessageFieldDTO messageFieldDTO = errorMessageDTO.getMessage();
            // Check that the "message" object is not null
            Assert.assertNotNull(messageFieldDTO,
                    "The message object should not be null.");
            String password = messageFieldDTO.getPassword();
            Assert.assertNull(password, "The '" + ApiConstants.PASSWORD_FIELD_NAME + "' field should be null.");
            // #4 Check that the "username" field equals the expected error message.
            String username = messageFieldDTO.getUsername();
            Assert.assertTrue(ApiConstants.USERNAME_ERROR_TEXT_RU.equals(username)
                            || ApiConstants.USERNAME_ERROR_TEXT_EN.equals(username),
                    "The text in the '" + ApiConstants.USERNAME_FIELD_NAME + "' field is incorrect.");
            // #5 Check that the response body matches the ErrorMessageDto schema
            SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);
        }

    }

    /**
     * The negative test "shouldReturnBadRequestWhenRegisteringWithInvalidPassword" checks that the Phonebook API
     * returns the error "Bad Request" when registering a new user with a valid unique email and an invalid password.
     * Expected results:
     * - Status code: 400. (#1)
     * - The "error" field equals "Bad Request". (#2)
     * - The "password" field in the "message" object equals the expected error message. (#3)
     * - The "username" field in the "message" object is null (#4)
     * - Response body matches the ErrorMessageDto (Message - Object) schema. (#5)
     * <p>
     * Note:
     * The exact validation message is not defined by the API contract.
     * The current API response is used as the expected value to detect
     * unexpected changes in API behavior.
     */
    @Test
    public void shouldReturnBadRequestWhenRegisteringWithInvalidPassword() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password(ApiConstants.TEST_PART_PASSWORD)
                .username(i + "invalid@password.com").build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 400, "Unexpected status code.");
            // The type of generic
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>() {
            }.getType();
            ErrorMessageDTO<MessageFieldDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);
            //System.out.println(errorMessageDTO.toString());
            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #3
            MessageFieldDTO messageFieldDTO = errorMessageDTO.getMessage();
            // Check that the "message" object is not null
            Assert.assertNotNull(messageFieldDTO,
                    "The message object should not be null.");
            Assert.assertEquals(messageFieldDTO.getPassword(), ApiConstants.PASSWORD_ERROR_TEXT,
                    "The text in the '" + ApiConstants.PASSWORD_FIELD_NAME + "' field is incorrect.");
            // #4
            Assert.assertNull(messageFieldDTO.getUsername(),
                    "The '" + ApiConstants.USERNAME_FIELD_NAME + "' field should be null.");
            // #5
            SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);
        }
    }


    /**
     * The negative test "shouldReturnBadRequestWhenRegisteringWithInvalidPasswordAndUsername" checks that the Phonebook API
     * returns the error "Bad Request" when registering a new user with an invalid email and an invalid password.
     * Expected results:
     * - Status code: 400. (#1)
     * - The "error" field equals "Bad Request". (#2)
     * - The "password" field in the "message" object equals the expected error message. (#3)
     * - The "username" field in the "message" object equals the expected error message (#4)
     * - Response body matches the ErrorMessageDto (Message - Object) schema. (#5)
     *
     * <p>
     * Note:
     * The exact validation message is not defined by the API contract.
     * The current API response is used as the expected value to detect
     * unexpected changes in API behavior.
     */
    @Test
    public void shouldReturnBadRequestWhenRegisteringWithInvalidPasswordAndUsername() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().username(i + "invalid@@alldata.ru")
                .password(ApiConstants.TEST_PART_PASSWORD).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 400, "Unexpected status code");
            //The generic type
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>() {
            }.getType();
            ErrorMessageDTO<MessageFieldDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);
            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            // #3
            MessageFieldDTO messageFieldDTO = errorMessageDTO.getMessage();
            // Check that the "message" object is not null
            Assert.assertNotNull(messageFieldDTO,
                    "The message object should not be null.");
            Assert.assertEquals(messageFieldDTO.getPassword(), ApiConstants.PASSWORD_ERROR_TEXT,
                    "The text in the '" + ApiConstants.PASSWORD_FIELD_NAME + "' field is incorrect.");
            // #4
            String usernameField = messageFieldDTO.getUsername();
            Assert.assertTrue(usernameField.equals(ApiConstants.USERNAME_ERROR_TEXT_RU)
                            || usernameField.equals(ApiConstants.USERNAME_ERROR_TEXT_EN),
                    "The text in the '" + ApiConstants.USERNAME_FIELD_NAME + "' field is incorrect.");
            // #5
            SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);
        }
    }

    /**
     * The negative test "shouldReturnBadRequestWhenRegisteringWithBlankUsername" checks that the Phonebook API
     * returns the error "Bad Request" when registering a new user with an empty or blank email and a valid password.
     * Expected results:
     * - Status code: 400. (#1)
     * - The "error" field equals "Bad Request". (#2)
     * - The "password" field in the "message" object is null. (#3)
     * - The "username" field in the "message" object equals the expected error message (#4)
     * - Response body matches the ErrorMessageDto (Message - Object) schema. (#5)
     * <p>
     * Note:
     * The exact validation message is not defined by the API contract.
     * The current API response is used as the expected value to detect
     * unexpected changes in API behavior.
     */
    @Test
    public void shouldReturnBadRequestWhenRegisteringWithBlankUsername() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password(i + ApiConstants.TEST_PART_PASSWORD)
                .username("").build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 400, "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>() {
            }.getType();
            ErrorMessageDTO<MessageFieldDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);
            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            MessageFieldDTO messageFieldDTO = errorMessageDTO.getMessage();
            // Check that the "message" object is not null
            Assert.assertNotNull(messageFieldDTO,
                    "The message object should not be null.");
            // #3
            Assert.assertNull(messageFieldDTO.getPassword(),
                    "The '" + ApiConstants.PASSWORD_FIELD_NAME + "' field should be null.");
            // #4
            String usernameField = messageFieldDTO.getUsername();
            Assert.assertTrue(ApiConstants.BLANK_FIELD_ERROR_TEXT_RU.equals(usernameField)
                            || ApiConstants.BLANK_FIELD_ERROR_TEXT_EN.equals(usernameField),
                    "The text in the '" + ApiConstants.USERNAME_FIELD_NAME + "' field is incorrect.");
            // #5
            SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);

        }
    }

    /**
     * The negative test "shouldReturnBadRequestWhenRegisteringWithBlankPassword" checks that the Phonebook API
     * returns the error "Bad Request" when registering a new user with a valid email and an empty or blank password.
     * Expected results:
     * - Status code: 400. (#1)
     * - The "error" field equals "Bad Request". (#2)
     * - The "username" field in the "message" object is null. (#3)
     * - The "password" field in the "message" object equals the expected error message (#4)
     * - Response body matches the ErrorMessageDto (Message - Object) schema. (#5)
     * <p>
     * Note:
     * The exact validation message is not defined by the API contract.
     * The current API response is used as the expected value to detect
     * unexpected changes in API behavior.
     */
    @Test
    public void shouldReturnBadRequestWhenRegisteringWithBlankPassword() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password("").username(i + "blank@password.com").build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 400, "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>() {
            }.getType();
            ErrorMessageDTO<MessageFieldDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);
            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            MessageFieldDTO messageFieldDTO = errorMessageDTO.getMessage();
            // Check that the "message" object is not null
            Assert.assertNotNull(messageFieldDTO,
                    "The message object should not be null.");
            // #3
            Assert.assertNull(messageFieldDTO.getUsername(),
                    "The '" + ApiConstants.USERNAME_FIELD_NAME + "' field should be null.");
            // #4
            String passwordField = messageFieldDTO.getPassword();
            Assert.assertTrue(ApiConstants.BLANK_FIELD_ERROR_TEXT_RU.equals(passwordField)
                            || ApiConstants.BLANK_FIELD_ERROR_TEXT_EN.equals(passwordField),
                    "The text in the '" + ApiConstants.PASSWORD_FIELD_NAME + "' field is incorrect.");
            // #5
            SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);

        }
    }
    /**
     * The negative test "shouldReturnBadRequestWhenRegisteringWithBlankEmailAndPassword" checks that the Phonebook API
     * returns the error "Bad Request" when registering a new user with an empty or blank email and an empty or blank password.
     * Expected results:
     * - Status code: 400. (#1)
     * - The "error" field equals "Bad Request". (#2)
     * - The "username" field in the "message" object equals the expected error message (#3)
     * - The "password" field in the "message" object equals the expected error message (#4)
     * - Response body matches the ErrorMessageDto (Message - Object) schema. (#5)
     * <p>
     * Note:
     * The exact validation message is not defined by the API contract.
     * The current API response is used as the expected value to detect
     * unexpected changes in API behavior.
     */
    @Test
    public void shouldReturnBadRequestWhenRegisteringWithBlankEmailAndPassword() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password("").username("").build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()){
            // #1
            Assert.assertEquals(response.code(), 400, "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");

            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<MessageFieldDTO>>(){}.getType();
            ErrorMessageDTO<MessageFieldDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");

            MessageFieldDTO messageFieldDTO = errorMessageDTO.getMessage();
            Assert.assertNotNull(messageFieldDTO, "The message object should not be null");

            // #3
            String usernameField = messageFieldDTO.getUsername();
            Assert.assertTrue(ApiConstants.BLANK_FIELD_ERROR_TEXT_RU.equals(usernameField)
                            || ApiConstants.BLANK_FIELD_ERROR_TEXT_EN.equals(usernameField),
                    "The text in the '" + ApiConstants.USERNAME_FIELD_NAME + "' field is incorrect.");
            // #4
            String passwordField = messageFieldDTO.getPassword();
            Assert.assertTrue(ApiConstants.BLANK_FIELD_ERROR_TEXT_EN.equals(passwordField)
                    || ApiConstants.BLANK_FIELD_ERROR_TEXT_RU.equals(passwordField),
                    "The text in the '" + ApiConstants.PASSWORD_FIELD_NAME + "' field is incorrect.");
            // #5
            SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);
        }
    }

    /**
     * The negative test "shouldReturnConflictWhenRegisteringWithExistingEmail()" checks that the Phonebook API
     * returns the error "Conflict" when registering a new user with an existing email and a valid new password.
     * <p>
     * Expected results:
     * - Status code: 409. (#1)
     * - The "error" field equals "Conflict". (#2)
     * - The "message" field equals "User already exists". (#3)
     * - Response body matches the ErrorMessageDto (Message - String) schema. (#4)
     * <p>
     * Note:
     * According to the API schema, the "message" field is defined as an object.
     * However, the actual API response contains the "message" field as a String.
     * This response format is treated as part of the actual API behavior.
     */
    @Test
    public void shouldReturnConflictWhenRegisteringWithExistingEmail() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password(i + ApiConstants.TEST_PART_PASSWORD)
                .username(ApiConstants.REGISTERED_LOGIN).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 409, "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);
            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_409,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            String messageField = errorMessageDTO.getMessage();
            // #3
            Assert.assertTrue(ApiConstants.CONFLICT_ERROR_TEXT.equals(messageField),
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
            // #4
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);

        }
    }

    /**
     * The negative test "shouldReturnConflictWhenRegisteringWithExistingEmailAndPassword()" checks that the Phonebook API
     * returns the error "Conflict" when registering a new user with an existing email and password combination.
     * <p>
     * Expected results:
     * - Status code: 409. (#1)
     * - The "error" field equals "Conflict". (#2)
     * - The "message" field equals "User already exists". (#3)
     * - Response body matches the ErrorMessageDto (Message - String) schema. (#4)
     * <p>
     * Note:
     * According to the API schema, the "message" field is defined as an object.
     * However, the actual API response contains the "message" field as a String.
     * This response format is treated as part of the actual API behavior.
     */
    @Test
    public void shouldReturnConflictWhenRegisteringWithExistingEmailAndPassword() throws IOException {
        AuthReqBodyDTO reqBodyDTO = AuthReqBodyDTO.builder().password(ApiConstants.REGISTERED_PASSWORD)
                .username(ApiConstants.REGISTERED_LOGIN).build();
        RequestBody requestBody = RequestBody.create(GSON.toJson(reqBodyDTO), JSON);
        Request request = new Request.Builder().url(REG_URL).post(requestBody).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            // #1
            Assert.assertEquals(response.code(), 409, "Unexpected status code.");
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<String>>() {
            }.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);
            // #2
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_409,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
            String messageField = errorMessageDTO.getMessage();
            // #3
            Assert.assertTrue(ApiConstants.CONFLICT_ERROR_TEXT.equals(messageField),
                    "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
            // #4
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
        }
    }





}
