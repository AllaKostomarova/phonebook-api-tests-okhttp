package data;

public class ApiConstants {

    // URL & endpoints
    public static final String BASEURL = "https://contactapp-telran-backend.herokuapp.com";
    public static final String REGISTR_ENDPOINT = "/v1/user/registration/usernamepassword";
    public static final String LOGIN_ENDPOINT = "/v1/user/login/usernamepassword";
    public static final String CONTACTS_ENDPOINT = "/v1/contacts";

    public static final String APPLICATION_JSON = "application/json;charset=utf-8";

    // Login data
    public static final String REGISTERED_LOGIN = "aa@aa.ru";
    public static final String REGISTERED_PASSWORD = "Test123$";

    // Test login data
    public static final String TEST_PART_PASSWORD = "Testing!";
    public static final String ANOTHER_REGISTERED_LOGIN = "dd@dd.ru";
    public static final String ANOTHER_REGISTERED_PASSWORD = "Password567!";

    // Tokens
    // For aa@aa.ru / Test123$
    public static final String ACTIVE_BASE_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoiYWFAYWEucnUiLCJpc3MiOiJSZWd1bGFpdCIsImV4cCI6MTc5MjA5OTY4MCwiaWF0IjoxNzkxNDk5NjgwfQ.ti5x3X8XxvkgImHfUm060h_W2vh1cGfWTvL3Z6Bd3IA";
    // For dd@dd.ru / Password567!
    public static final String ACTIVE_TOKEN_EMPTY_ARRAY = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoiZGRAZGQucnUiLCJpc3MiOiJSZWd1bGFpdCIsImV4cCI6MTc5MjEwMDMxMiwiaWF0IjoxNzkxNTAwMzEyfQ.zETL25cStPJInXFE8-5yMeA-TCo9JnXeh4QK695r1ww";
    public static final String INVALID_TOKEN = "eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoiZGRAZGQucnUiLCJpc3MiOiJSZWd1bGFpdCIsImV4cCI6MTc5MjEwMDMxMiwiaWF0IjoxNzkxNTAwMzEyfQ.zETL25cStPJInXFE8-5yMeA-TCo9JnXeh4QK695r1ww";

    // Headers name
    public static final String AUTH_HEADER_NAME = "Authorization";


    // Response field names
    // For ErrorMessageDto
    public static final String TOKEN_FIELD_NAME = "token";
    public static final String TIMESTAMP_FIELD_NAME = "timestamp";
    public static final String STATUS_FIELD_NAME = "status";
    public static final String ERROR_FIELD_NAME = "error";
    public static final String MESSAGE_FIELD_NAME = "message";
    public static final String PATH_FIELD_NAME = "path";
    public static final int ERROR_MESSAGE_SIZE = 5;
    // For ContactDTO
    public static final String ID_FIELD_NAME = "id";
    public static final String NAME_FIELD_NAME = "name";
    public static final String LASTNAME_FIELD_NAME = "lastName";
    public static final String EMAIL_FIELD_NAME = "email";
    public static final String PHONE_FIELD_NAME = "phone";
    public static final String ADDRESS_FIELD_NAME = "address";
    public static final String DESCRIPTION_FIELD_NAME = "description";
    public static final int CONTACT_SIZE = 7;
    // For ContactsDto
    public static final String CONTACTS_FIELD_NAME = "contacts";
    public static final String PASSWORD_FIELD_NAME = "password";
    public static final String USERNAME_FIELD_NAME = "username";


    // Expected error messages
    public static final String USERNAME_ERROR_TEXT_RU = "должно иметь формат адреса электронной почты";
    public static final String USERNAME_ERROR_TEXT_EN = "must be a well-formed email address";
    public static final String PASSWORD_ERROR_TEXT = " At least 8 characters; Must contain at least 1 uppercase letter, 1 lowercase letter, and 1 number; Can contain special characters [@$#^&*!]";
    public static final String BLANK_FIELD_ERROR_TEXT_RU = "не должно быть пустым";
    public static final String BLANK_FIELD_ERROR_TEXT_EN = "must not be blank";
    public static final String CONFLICT_ERROR_TEXT = "User already exists";
    public static final String AUTH_ERROR_TEXT = "Login or Password incorrect";
    public static final String ERROR_400 = "Bad Request";
    public static final String ERROR_409 = "Conflict";
    public static final String ERROR_401 = "Unauthorized";


}
