package data;

public class ApiConstants {

    // URL & endpoints
    public static final String BASEURL = "https://contactapp-telran-backend.herokuapp.com";
    public static final String REGISTR_ENDPOINT = "/v1/user/registration/usernamepassword";
    public static final String LOGIN_ENDPOINT = "/v1/user/login/usernamepassword";
    public static final String CONTACTS_ENDPOINT = "/v1/contacts";

    // Login data
    public static final String LOGIN = "aa@aa.ru";
    public static final String PASSWORD = "Test123$";

    // Test data
    public static final String TEST_PASSWORD = "Testing!";

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
    public static final String USERNAME_ERROR_RU = "должно иметь формат адреса электронной почты";
    public static final String USERNAME_ERROR_EN = "must be a well-formed email address";
    public static final String PASSWORD_ERROR = " At least 8 characters; Must contain at least 1 uppercase letter, 1 lowercase letter, and 1 number; Can contain special characters [@$#^&*!]";
    public static final String ERROR_BLANK_RU = "не должно быть пустым";
    public static final String ERROR_BLANK_EN = "must not be blank";
    public static final String ERROR_MESSAGE_400 = "Bad Request";
    public static final String ERROR_MESSAGE_409 = "Conflict";
    public static final String ERROR_CONFLICT_MESSAGE = "User already exists";

}
