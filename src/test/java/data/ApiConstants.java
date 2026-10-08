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

    // Test data
    public static final String TEST_PART_PASSWORD = "Testing!";
    public static final String ANOTHER_REGISTERED_LOGIN = "dd@dd.ru";
    public static final String ANOTHER_REGISTERED_PASSWORD = "Password567!";

    // Response field names
    public static final String TOKEN_FIELD_NAME = "token";
    public static final String TIMESTAMP_FIELD_NAME = "timestamp";
    public static final String STATUS_FIELD_NAME = "status";
    public static final String ERROR_FIELD_NAME = "error";
    public static final String MESSAGE_FIELD_NAME = "message";
    public static final String PATH_FIELD_NAME = "path";
    public static final int ERROR_MESSAGE_SIZE = 5;
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
