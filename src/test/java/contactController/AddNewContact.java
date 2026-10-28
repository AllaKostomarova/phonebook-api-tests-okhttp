package contactController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import data.ApiConstants;
import dto.ContactDTO;
import dto.ContactMessageObjectDTO;
import dto.ErrorMessageDTO;
import dto.SuccessMassageDTO;
import helpers.SchemaValidator;
import okhttp3.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class AddNewContact {
    public static final Gson GSON = new Gson();
    /*
     This variable handles correctly the fields with null when converting a Java Object ro JSON
     Should use this variable to convert a Java Object to JSON
      when null is passed to the fields in the request body
     */
    public static final Gson GSON_WITH_NULLS = new GsonBuilder().serializeNulls().create();
    public static final OkHttpClient CLIENT = new OkHttpClient();
    public static final MediaType JSON = MediaType.get(ApiConstants.APPLICATION_JSON);
    public static final String URL_CONTACTS = ApiConstants.BASEURL + ApiConstants.CONTACTS_ENDPOINT;

    // Helper Methods

    /**
     * Creates a request body from the provided ContactDTO and builds a POST request with a valid token.
     * Uses sendPostRequest() method to send the request, check the status code,
     * verify that the response body is not null, and return it as a String.
     *
     * @param contactDTO the contact as a ContactDTO Object.
     * @param expectedStatusCode  the expected HTTP status code.
     * @return the response body as a Java String.
     * @throws IOException if an I/O error occurs while sending the request or reading the response.
     */
    private String sendAddContactRequestWithValidToken(ContactDTO contactDTO, int expectedStatusCode) throws IOException {
        RequestBody requestBody = RequestBody.create(GSON.toJson(contactDTO), JSON);
        Request request = new Request.Builder().url(URL_CONTACTS)
                .post(requestBody).addHeader(ApiConstants.AUTH_HEADER_NAME, ApiConstants.ACTIVE_BASE_TOKEN).build();
        return sendPostRequest(request, expectedStatusCode);
    }

    /**
     * Sends the provided request, checks the status code,
     * verifies that the response body is not null, and returns it as a String.
     *
     * @param request the request as a Request object.
     * @param expectedStatusCode the expected HTTP status code.
     * @return the response body as a Java String.
     * @throws IOException if an I/O error occurs while sending the request or reading the response.
     */
    private String sendPostRequest(Request request, int expectedStatusCode) throws IOException {
        try (Response response = CLIENT.newCall(request).execute()) {
            Assert.assertEquals(response.code(), expectedStatusCode,
                    "Unexpected status code: " + response.code());
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            return responseBody.string();
        }
    }

    /**
     * Creates a ContactDTO object with valid field values and a time-based suffix.
     * Converts the ContactDTO object to JSON and returns it as a RequestBody object.
     *
     * @return the request body as a RequestBody object.
     */
    private RequestBody createValidRequestBody (){
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name(i+"ValidRequestBody")
                .lastName(i+"ValidRequestBody")
                .email(i+"valid@requestbody.com")
                .phone(i + "2222233333")
                .address(i+"Valid Request Body")
                .description(i+ "Valid Request body").build();
        return RequestBody.create(GSON.toJson(contactDTO), JSON);
    }

    /**
     * Checks the response body of a successful POST request with status code 200.
     * Validates the SuccessMessageDTO schema.
     * Converts JSON to a SuccessMessageDTO object and checks that the message contains
     * the phrase "Contact was added!" and the ID of the newly created contact
     *
     * @param stringResBody the response body as a String.
     */
    private void checkResponseBodyOfSuccessRequest(String stringResBody) {
        // Schema SuccessMessageDto
        SchemaValidator.checkSuccessMessageDtoSchema(stringResBody);
         /*
         Message field content
         "Contact was added! ID: example-id-4014-9ee4-baa556baa8a5"
         */
        SuccessMassageDTO massageDTO = GSON.fromJson(stringResBody, SuccessMassageDTO.class);
        String messageContent = massageDTO.getMessage();
        Assert.assertTrue(messageContent.contains(ApiConstants.CONTACT_CREATED_MESSAGE),
                "The text in the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field is incorrect.");
        Assert.assertTrue(messageContent.contains(ApiConstants.CONTACT_ID_PREFIX),
                "The response message does not contain the contact ID prefix '"
                        + ApiConstants.CONTACT_ID_PREFIX + "'.");
        String[] mess = messageContent.split(ApiConstants.CONTACT_ID_PREFIX);
        Assert.assertFalse(mess[1].isBlank(), "The contact ID should not be blank.");
    }

    /**
     * Checks the response body of a POST request with status code 400 (Bad Request).
     * Validates the ErrorMessageDTO schema with the 'message' field as an object.
     * Validates the 'message' object against the expected fields.
     * Converts the JSON to an ErrorMessageDTO object and checks the text in the 'error' field.
     *
     * @param stringResBody the response body as a String.
     * @param expectedFields the array of the expected field names in the 'message' object.
     * @return the response body as an ErrorMessageDTO<ContactMessageObjectDTO> object.
     */
    private ErrorMessageDTO<ContactMessageObjectDTO>
    checkResponseBodyOfBadRequest(String stringResBody, String[] expectedFields) {
        // Schema
        SchemaValidator.checkErrorMessageDtoObjectMessageSchema(stringResBody);
        // JAVA message object
        SchemaValidator.checkMessageObjectJson(expectedFields, stringResBody);
        Type type = new TypeToken<ErrorMessageDTO<ContactMessageObjectDTO>>() {
        }.getType();
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);
        // Error Field
        Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
        return errorMessageDTO;
    }

    /**
     * Checks the response body of a POST request with status code 401 (Unauthorized).
     * Validates the ErrorMessageDTO schema with the 'message' field as a String.
     * Converts the JSON to an ErrorMessageDTO object and checks the text in the 'error' field.
     *
     * @param stringResBody the response body as a String.
     */
    private void checkResponseBodyOfUnauthorized(String stringResBody) {
        // Schema
        SchemaValidator.checkErrorMessageDtoStringMessageSchema(stringResBody);
        Type type = new TypeToken<ErrorMessageDTO<String>>() {
        }.getType();
        ErrorMessageDTO<String> errorMessageDTO = GSON.fromJson(stringResBody, type);
        // Error Field
        Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_401,
                "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
    }

    /**
     * Checks that the field value in the 'message' object matches
     * the expected error text in English or Russian.
     * @param fieldName the field name in the 'message' object.
     * @param fieldContent the field value in the 'message' object.
     */
    private void assertBlankFieldErrorMessage(String fieldName, String fieldContent){
        Assert.assertTrue(ApiConstants.BLANK_FIELD_ERROR_TEXT_EN.equals(fieldContent)
                        || ApiConstants.BLANK_FIELD_ERROR_TEXT_RU.equals(fieldContent),
                "The text in the '" + fieldName + "' field is incorrect.");
    }

    // 200
    @Test
    public void shouldReturnMessageWithContactIdWhenAllFieldsAreFilledWithValidData() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0").name("fillAll").lastName("fillAllLast")
                .email(i + "fillAll@fields.com").phone(i + "1111111111")
                .address("fill all").description("fill all fields").build();

        String stringResBody = sendAddContactRequestWithValidToken(contactDTO, 200);
        checkResponseBodyOfSuccessRequest(stringResBody);
    }

    @Test
    public void shouldReturnMessageWithContactIdWhenOnlyRequiredFieldsAreFilledWithValidData() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0").name("RequiredFields").lastName("RequiredFieldsLast")
                .email(i + "required@fields.com").phone(i + "2222222222")
                .address("Required Fields Address").description("").build();

        String stringResBody = sendAddContactRequestWithValidToken(contactDTO, 200);
        checkResponseBodyOfSuccessRequest(stringResBody);
    }

    // 400
    @Test
    public void shouldReturnBadRequestWhenContactNameIsEmpty() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0").name("").lastName("BlankName")
                .email(i + "blank@name.com").phone(i + "3333333333")
                .address("Blank Name").description("Name is blank").build();

        String stringResBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.NAME_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO =
                checkResponseBodyOfBadRequest(stringResBody, expectedFields);
        // Scenario-specific assertions: the message field content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");
        assertBlankFieldErrorMessage(ApiConstants.NAME_FIELD_NAME, messageObjectDTO.getName());
    }

    @Test
    public void shouldReturnBadRequestWhenContactLastNameIsEmpty() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("EmptyLastName")
                .lastName("")
                .email(i + "empty@lastname.com")
                .phone(i + "4444444444")
                .address("Empty LastMame")
                .description("The LastMame is empty").build();

        String stringResBody = sendAddContactRequestWithValidToken(contactDTO, 400);

        String[] expectedFields = {ApiConstants.LASTNAME_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO =
                checkResponseBodyOfBadRequest(stringResBody, expectedFields);

        // Scenario-specific assertions: the message field content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");

        assertBlankFieldErrorMessage(ApiConstants.LASTNAME_FIELD_NAME, messageObjectDTO.getLastName());

    }

    @Test
    public void shouldReturnBadRequestWhenContactEmailIsEmpty() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("EmptyEmail")
                .lastName("EmptyEmail")
                .email("")
                .phone(i + "5555555555")
                .address("Empty Email")
                .description("The Email is empty").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.EMAIL_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);

        // Scenario-specific assertions: the message fields content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");

        assertBlankFieldErrorMessage(ApiConstants.EMAIL_FIELD_NAME, messageObjectDTO.getEmail());
    }

    @Test
    public void shouldReturnBadRequestWhenContactPhoneIsEmpty() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("EmptyPhone")
                .lastName("EmptyPhone")
                .email(i+"empty@phone.com")
                .phone("")
                .address("Empty Phone")
                .description("The Phone is empty").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.PHONE_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);

        // Scenario-specific assertions: the message fields content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO,"The message Object should not be null.");

        assertBlankFieldErrorMessage(ApiConstants.PHONE_FIELD_NAME, messageObjectDTO.getPhone());

    }

    @Test
    public void shouldReturnBadRequestWhenContactAddressIsEmpty() throws IOException {
        int i = (int) ((System.currentTimeMillis() / 1000) % 3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("EmptyAddress")
                .lastName("EmptyAddress")
                .email(i+"empty@address.com")
                .phone(i + "6666666666")
                .address("")
                .description("The Address is empty").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.ADDRESS_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);

        // Scenario-specific assertions: the message fields content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");

        assertBlankFieldErrorMessage(ApiConstants.ADDRESS_FIELD_NAME, messageObjectDTO.getAddress());
    }

    @Test
    public void shouldReturnBadRequestWhenAllFieldsAreEmpty() throws IOException {
        ContactDTO contactDTO = ContactDTO.builder()
                .id("")
                .name("")
                .lastName("")
                .email("")
                .phone("")
                .address("")
                .description("").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);

        // Create String array with all fields of ContactMessageObjectDTO
        Field[] declaredFields = ContactMessageObjectDTO.class.getDeclaredFields();
        String[] expectedFields = new String[declaredFields.length];
        for (int i=0; i<declaredFields.length; i++){
            expectedFields[i]=declaredFields[i].getName();
        }
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);

        // Scenario-specific assertions: the message fields content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");

        assertBlankFieldErrorMessage(ApiConstants.NAME_FIELD_NAME, messageObjectDTO.getName());
        assertBlankFieldErrorMessage(ApiConstants.LASTNAME_FIELD_NAME, messageObjectDTO.getLastName());
        assertBlankFieldErrorMessage(ApiConstants.EMAIL_FIELD_NAME, messageObjectDTO.getEmail());
        assertBlankFieldErrorMessage(ApiConstants.PHONE_FIELD_NAME, messageObjectDTO.getPhone());
        assertBlankFieldErrorMessage(ApiConstants.ADDRESS_FIELD_NAME, messageObjectDTO.getAddress());

    }

    @Test
    public void shouldReturnBadRequestWhenContactEmailIsInvalid() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("InvalidEmail")
                .lastName("InvalidEmail")
                .email("@invalid.email"+i)
                .phone(i+"7777777777")
                .address("Email is invalid").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.EMAIL_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody,expectedFields);

        // Scenario-specific assertions: the message fields content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");

        Assert.assertTrue(ApiConstants.INVALID_EMAIL_ERROR_TEXT_RU.equals(messageObjectDTO.getEmail())
                || ApiConstants.INVALID_EMAIL_ERROR_TEXT_EN.equals(messageObjectDTO.getEmail()),
                "The text in the'" + ApiConstants.EMAIL_FIELD_NAME + "' field is incorrect.");
    }

    @Test
    public void shouldReturnBadRequestWhenContactPhoneIsInvalid() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("InvalidPhone")
                .lastName("InvalidPhone")
                .email(i+"invalid@phone.com")
                .phone(i+"767676767676767")
                .address("Phone is invalid").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.PHONE_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody,expectedFields);

        // Scenario-specific assertions: the message fields content
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");

        Assert.assertEquals(messageObjectDTO.getPhone(), ApiConstants.INVALID_PHONE_ERROR_TEXT,
                "The text in the '" + ApiConstants.PHONE_FIELD_NAME + "' field is incorrect.");
    }

    @Test
    public void shouldReturnBadRequestWhenContactNameFieldIsMissing() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .lastName("NameFieldIsMissing")
                .email(i+"namefield@missing.com")
                .phone(i+"8888888888")
                .address("Name field is missing").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.NAME_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");
        assertBlankFieldErrorMessage(ApiConstants.NAME_FIELD_NAME, messageObjectDTO.getName());
    }

    @Test
    public void shouldReturnBadRequestWhenContactLastNameFieldIsMissing() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("LastNameFieldIsMissing")
                .email(i+"lastnamefield@missing.com")
                .phone(i+"9999999999")
                .address("LastName field is missing").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.LASTNAME_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");
        assertBlankFieldErrorMessage(ApiConstants.LASTNAME_FIELD_NAME, messageObjectDTO.getLastName());
    }

    @Test
    public void shouldReturnBadRequestWhenContactEmailFieldIsMissing() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("EmailFieldIsMissing")
                .lastName("EmailFieldIsMissing")
                .phone(i+"9999999999")
                .address("email field is missing").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.EMAIL_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");
        assertBlankFieldErrorMessage(ApiConstants.EMAIL_FIELD_NAME, messageObjectDTO.getEmail());
    }

    @Test
    public void shouldReturnBadRequestWhenContactPhoneFieldIsMissing() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("PhoneFieldIsMissing")
                .lastName("PhoneFieldIsMissing")
                .email(i+"phonefield@missing.com")
                .address("Phone field is missing").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.PHONE_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");
        assertBlankFieldErrorMessage(ApiConstants.PHONE_FIELD_NAME, messageObjectDTO.getPhone());
    }

    @Test
    public void shouldReturnBadRequestWhenContactAddressFieldIsMissing() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("AddressFieldIsMissing")
                .lastName("AddressFieldIsMissing")
                .email(i+"addressfield@missing.com")
                .phone(i+"1111122222")
                .build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 400);
        String[] expectedFields = {ApiConstants.ADDRESS_FIELD_NAME};
        ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = checkResponseBodyOfBadRequest(responseBody, expectedFields);
        ContactMessageObjectDTO messageObjectDTO = errorMessageDTO.getMessage();
        Assert.assertNotNull(messageObjectDTO, "The message Object should not be null.");
        assertBlankFieldErrorMessage(ApiConstants.ADDRESS_FIELD_NAME, messageObjectDTO.getAddress());
    }

    @Test
    public void shouldReturnBadRequestWhenRequestBodyIsMissing() throws IOException {
        RequestBody emptyBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(URL_CONTACTS)
                .post(emptyBody)
                .addHeader(ApiConstants.AUTH_HEADER_NAME, ApiConstants.ACTIVE_BASE_TOKEN).build();
        try (Response response = CLIENT.newCall(request).execute()){
            Assert.assertEquals(response.code(), 400, "Unexpected status code: " + response.code());
            ResponseBody responseBody = response.body();
            Assert.assertNotNull(responseBody, "The response body should not be null.");
            String stringResBody = responseBody.string();
            Type type = new TypeToken<ErrorMessageDTO<ContactMessageObjectDTO>>(){}.getType();
            ErrorMessageDTO<ContactMessageObjectDTO> errorMessageDTO = GSON.fromJson(stringResBody, type);
            Assert.assertEquals(errorMessageDTO.getError(), ApiConstants.ERROR_400,
                    "The text in the '" + ApiConstants.ERROR_FIELD_NAME + "' field is incorrect.");
        }
    }

    // 401
    @Test
    public void shouldReturnUnauthorizedWhenAddingContactWithInvalidToken() throws IOException {
        RequestBody requestBody = createValidRequestBody();
        String invalidToken = ApiConstants.ACTIVE_BASE_TOKEN.substring(1);
        Request request = new Request.Builder()
                .url(URL_CONTACTS)
                .post(requestBody)
                .addHeader(ApiConstants.AUTH_HEADER_NAME, invalidToken).build();
        String responseBody = sendPostRequest(request, 401);
        checkResponseBodyOfUnauthorized(responseBody);
    }

    @Test
    public void shouldReturnUnauthorizedWhenAddingContactWithEmptyAuthorizationHeader() throws IOException {
        RequestBody requestBody= createValidRequestBody();
        Request request = new Request.Builder()
                .url(URL_CONTACTS)
                .post(requestBody)
                .addHeader(ApiConstants.AUTH_HEADER_NAME, "").build();
        String responseBody = sendPostRequest(request, 401);
        checkResponseBodyOfUnauthorized(responseBody);

    }

    @Test
    public void shouldReturnUnauthorizedWhenAddingContactWithoutAuthorizationHeader() throws IOException {
        RequestBody requestBody = createValidRequestBody();
        Request request = new Request.Builder()
                .url(URL_CONTACTS)
                .post(requestBody)
                .build();
        String responseBody = sendPostRequest(request, 401);
        checkResponseBodyOfUnauthorized(responseBody);

    }

    // 409
    @Test
    public void shouldReturnConflictWhenAddingContactWithExistingEmail() throws IOException {
        int a = (int)((System.currentTimeMillis()/1000)%3600);
        String existedEmail = a+"existed@email.com";
        ContactDTO contactDTO1 = ContactDTO.builder()
                .id("0")
                .name(a+"FirstName")
                .lastName(a+"FirstLastName")
                .email(existedEmail)
                .phone(a+"1111111111")
                .address(a+"FirstAddress")
                .description(a+ "-The first contact").build();
        String responseBody1 = sendAddContactRequestWithValidToken(contactDTO1, 200);

        int b = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO2 = ContactDTO.builder()
                .id("0")
                .name(b+"SecondName")
                .lastName(b+"SecondName")
                .email(existedEmail)
                .phone(b+"2222222222")
                .address(b+"SecondAddress")
                .description(b+ "-The second contact").build();
        String responseBody2 = sendAddContactRequestWithValidToken(contactDTO2, 409);
    }

    @Test
    public void shouldReturnConflictWhenAddingContactWithExistingPhone() throws IOException {
        int a = (int)((System.currentTimeMillis()/1000)%3600);
        String existedPhone = a+"4545456767";
        ContactDTO contactDTO1 = ContactDTO.builder()
                .id("0")
                .name("FirstName"+a)
                .lastName("FirstLastName"+a)
                .email("first"+a+"@email.com")
                .phone(existedPhone)
                .address("FirstAddress"+a)
                .description("The first contact-" + a).build();
        String responseBody1 = sendAddContactRequestWithValidToken(contactDTO1, 200);

        int b = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO2 = ContactDTO.builder()
                .id("0")
                .name("SecondName"+b)
                .lastName("SecondName"+b)
                .email("second"+b+"@email.com")
                .phone(existedPhone)
                .address("SecondAddress"+b)
                .description("The second contact-"+b).build();
        String responseBody2 = sendAddContactRequestWithValidToken(contactDTO2, 409);
    }

    @Test
    public void shouldReturnConflictWhenAddingDuplicateContact() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);

        ContactDTO contactDTO = ContactDTO.builder()
                .id("0")
                .name("DuplicateName"+i)
                .lastName("DuplicateLastName"+i)
                .email("duplicate"+i+"@email.com")
                .phone(i+"7878787878")
                .address("DuplicateAddress"+i)
                .description("The duplicate contact-" + 1).build();
        String responseBody1 = sendAddContactRequestWithValidToken(contactDTO, 200);
        String responseBody2 = sendAddContactRequestWithValidToken(contactDTO, 409);
    }

    // 200
    @Test
    public void shouldReturnMessageWithContactIdWhenNonUniqueFieldsAreDuplicated() throws IOException {
        int a = (int)((System.currentTimeMillis()/1000)%3600);
        String existedName = a+"NonUniqueNameDuplicated";
        String existedLastName = a+"NonUniqueLastNameDuplicated";
        String existedAddress = a+"NonUniqueAddressDuplicated";
        String existedDescription = a+"Non Unique Description Duplicated";

        ContactDTO contactDTO1 = ContactDTO.builder()
                .id("0")
                .name(existedName)
                .lastName(existedLastName)
                .email(a+"uniquefield@email.com")
                .phone(a+"9988998877")
                .address(existedAddress)
                .description(existedDescription).build();
        String responseBody1 = sendAddContactRequestWithValidToken(contactDTO1, 200);
        SuccessMassageDTO massageDTO1 = GSON.fromJson(responseBody1, SuccessMassageDTO.class);
        String messageContent1 = massageDTO1.getMessage();
        Assert.assertTrue(messageContent1.contains(ApiConstants.CONTACT_ID_PREFIX),
                "The response message does not contain the contact ID prefix '"
                        + ApiConstants.CONTACT_ID_PREFIX + "'.");
        String[] mess1 = messageContent1.split(ApiConstants.CONTACT_ID_PREFIX);
        String contactId1 = mess1[1];

        int b = (int)((System.currentTimeMillis()/1000)%3600);
        ContactDTO contactDTO2 = ContactDTO.builder()
                .id("0")
                .name(existedName)
                .lastName(existedLastName)
                .email("uniquefield"+b+"@email.com")
                .phone("3456876578"+b)
                .address(existedAddress)
                .description(existedDescription).build();
        String responseBody2 = sendAddContactRequestWithValidToken(contactDTO2, 200);
        SuccessMassageDTO massageDTO2 = GSON.fromJson(responseBody2, SuccessMassageDTO.class);
        String messageContent2 = massageDTO2.getMessage();
        Assert.assertTrue(messageContent2.contains(ApiConstants.CONTACT_ID_PREFIX),
                "The response message does not contain the contact ID prefix '"
                        + ApiConstants.CONTACT_ID_PREFIX + "'.");
        String[] mess2 = messageContent2.split(ApiConstants.CONTACT_ID_PREFIX);
        String contactId2 = mess2[1];

        Assert.assertNotEquals(contactId1, contactId2);

    }

    // 200 - id
    @Test
    public void shouldGenerateNewContactIdRegardlessOfProvidedId() throws IOException {
        int i = (int)((System.currentTimeMillis()/1000)%3600);
        String providedId = "e7a93c21-6f84-4b0d-9a52-c8d317f06e4b";
        ContactDTO contactDTO = ContactDTO.builder()
                .id(providedId)
                .name("ProvidedId")
                .lastName("ProvidedId")
                .email(i+"provided@id.com")
                .phone(i+"5555599999")
                .address("Provided Id")
                .description("Id was provided").build();
        String responseBody = sendAddContactRequestWithValidToken(contactDTO, 200);
        SuccessMassageDTO massageDTO = GSON.fromJson(responseBody, SuccessMassageDTO.class);
        Assert.assertTrue(massageDTO.getMessage().contains(ApiConstants.CONTACT_ID_PREFIX),
                "The response message does not contain the contact ID prefix '"
                        + ApiConstants.CONTACT_ID_PREFIX + "'.");
        String[] mess = massageDTO.getMessage().split(ApiConstants.CONTACT_ID_PREFIX);
        String newId = mess[1];
        Assert.assertNotEquals(newId, providedId,
                "The generated contact ID should differ from the provided ID.");

    }
}
