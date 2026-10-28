package contactController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.ApiConstants;
import dto.AllContactsDTO;
import dto.ErrorMessageDTO;
import helpers.SchemaValidator;
import okhttp3.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;

public class GetAllContacts {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();
    private static final String GET_CONTACTS = ApiConstants.BASEURL+ApiConstants.CONTACTS_ENDPOINT;

    /**
     * The positive test checks that the contacts array is returned in the response body
     * when the request contains a valid token in the Authorization header.
     *
     * Expected results:
     * - Status code: 200 (#1)
     * - The response body matches the ContactsDto schema (#2)
     * - The 'contacts' field is not empty (#3)
     */
    @Test
    public void shouldReturnContactArrayWhenAuthorizedUserHasContacts() throws IOException {
        Request request = new Request.Builder().url(GET_CONTACTS).get()
                .addHeader(ApiConstants.AUTH_HEADER_NAME, ApiConstants.ACTIVE_BASE_TOKEN).build();
        try (Response response = CLIENT.newCall(request).execute()){
            // #1
            Assert.assertEquals(response.code(), 200,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();

            // #2
            SchemaValidator.checkContactsResponseStructure(stringResBody);

            // #3
            AllContactsDTO contactsArrayDTO = GSON.fromJson(stringResBody, AllContactsDTO.class);

            Assert.assertFalse(contactsArrayDTO.getContacts().isEmpty(),
                    "The contacts array should not be empty.");
        }
    }

    /**
     * The positive test verifies that the 'contacts' field in the response body
     * contains an empty array for a user without contacts.
     *
     * Expected results:
     * - Status code: 200 (#1)
     * - The response body matches the ContactsDto schema (#2)
     * - The 'contacts' field contains an empty array (#3)
     */
    @Test
    public  void shouldReturnEmptyContactsArrayWhenAuthorizedUserHasNoContacts() throws IOException {
        Request request = new Request.Builder().url(GET_CONTACTS)
                .get().addHeader(ApiConstants.AUTH_HEADER_NAME, ApiConstants.ACTIVE_TOKEN_EMPTY_ARRAY).build();
        try (Response response = CLIENT.newCall(request).execute()){
            // #1
            Assert.assertEquals(response.code(), 200,
                    "Unexpected status code: " + response.code());
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            // #2
            SchemaValidator.checkContactsResponseStructure(stringResBody);
            // #3
            AllContactsDTO contactsArrayDTO = GSON.fromJson(stringResBody, AllContactsDTO.class);
            Assert.assertTrue(contactsArrayDTO.getContacts().isEmpty(),
                    "The contacts array should be empty in the user: " + ApiConstants.ANOTHER_REGISTERED_LOGIN);
        }
    }

    /**
     * The negative test checks that the system correctly handles
     * the GET request "Get all contacts" with an invalid token.
     *
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     */
    @Test
    public void shouldReturnUnauthorizedWhenGetContactsWithInvalidToken() throws IOException {
        Request request = new Request.Builder().url(GET_CONTACTS).get()
                .addHeader(ApiConstants.AUTH_HEADER_NAME, ApiConstants.INVALID_TOKEN).build();
        try (Response response = CLIENT.newCall(request).execute()){
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();

            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);

            Type type = new TypeToken<ErrorMessageDTO<String>>(){}.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
        }
    }
    /**
     * The negative test checks that the system correctly handles
     * the GET request "Get all contacts" when the Authorization header is empty.
     *
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     */
    @Test
    public void shouldReturnUnauthorizedWhenGetContactsWithEmptyAuthorizationHeader() throws IOException {
        Request request= new Request.Builder().url(GET_CONTACTS).get()
                .addHeader(ApiConstants.AUTH_HEADER_NAME, "").build();
        try (Response response = CLIENT.newCall(request).execute()){
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringRsBody = responseBody.string();

            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringRsBody);

            Type type = new TypeToken<ErrorMessageDTO<String>>(){}.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringRsBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");

        }

    }

    /**
     * The negative test checks that the system correctly handles
     * the GET request "Get all contacts" without the Authorization header.
     *
     * Expected results:
     * - Status code: 401 (#1)
     * - The response body matches the ErrorMessageDto (Message - String) schema (#2)
     * - The 'error' field exists and equals "Unauthorized" (#3)
     */
    @Test
    public void shouldReturnUnauthorizedWhenGetContactsWithoutAuthorizationHeader() throws IOException {
        Request request = new Request.Builder().url(GET_CONTACTS).get().build();
        try (Response response = CLIENT.newCall(request).execute()){
            // #1
            Assert.assertEquals(response.code(), 401,
                    "Unexpected status code: " + response.code());

            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();

            // #2
            SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);

            Type type = new TypeToken<ErrorMessageDTO<String>>(){}.getType();
            ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);

            // #3
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
        }
    }

}
