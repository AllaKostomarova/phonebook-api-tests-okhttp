package helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.ApiConstants;
import dto.AuthReqBodyDTO;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class SchemaValidator {
    /**
     * Checks that the response body
     * matches the TokenDto schema defined in the API documentation.
     * <p>
     * Expected results:
     * - There is only one field (#1)
     * - The field name is 'token' (#2)
     * - The field type is String (#3)
     * <p>
     * TokenDto{
     * token	string
     * }
     *
     * @param responseBody the response body as a Java String
     */
    public static void checkTokenDtoSchema(String responseBody) {
            /*
            Convert the response body String to JsonObject:
            1) Parse the String using JsonParser.parseString()
            2) Convert JsonElement to JsonObject using the .getAsJsonObject() method
             */
        JsonObject jsonResBody = JsonParser.parseString(responseBody).getAsJsonObject();
        // #1
        Assert.assertEquals(jsonResBody.size(), 1,
                "The response body should contain only one field.");
        // #2
        Assert.assertTrue(jsonResBody.has(ApiConstants.TOKEN_FIELD_NAME),
                "The response body should contain the '" + ApiConstants.TOKEN_FIELD_NAME + "' field.");
        // #3
        JsonElement tokenField = jsonResBody.get(ApiConstants.TOKEN_FIELD_NAME);
        Assert.assertTrue(tokenField.isJsonPrimitive(),
                "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should be a JSON primitive.");
        Assert.assertTrue(tokenField.getAsJsonPrimitive().isString(),
                "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should be a String.");
    }

    /**
     * Checks the common part of the ErrorMessageDto schema.
     * <p>
     * Validates the fields that are common to both error response variants:
     * - the response contains exactly 5 fields; (#1)
     * - the required fields are present; (#2)
     * - the 'timestamp' field is a String with a valid date-time format; (#3)
     * - the 'status' field is an int32; (#4)
     * - the 'error' field is a String; (#5)
     * - the 'path' field is a String. (#6)
     * <p>
     * The type of the 'message' field is not validated by this method,
     * because it can be either a String or an object depending on the response.
     *
     * @param responseBody the response body as a Java String
     * @return the parsed response body as a JsonObject
     */
    private static JsonObject checkCommonPartOfErrorMessageDtoSchema(String responseBody) {
        String timestampName = ApiConstants.TIMESTAMP_FIELD_NAME;
        String statusName = ApiConstants.STATUS_FIELD_NAME;
        String errorName = ApiConstants.ERROR_FIELD_NAME;
        String messageName = ApiConstants.MESSAGE_FIELD_NAME;
        String pathName = ApiConstants.PATH_FIELD_NAME;

        JsonObject jsonResBody = JsonParser.parseString(responseBody).getAsJsonObject();
        // #1
        Assert.assertEquals(jsonResBody.size(), ApiConstants.ERROR_MESSAGE_SIZE,
                "The response body should contain " + ApiConstants.ERROR_MESSAGE_SIZE + " fields.");

        // Check field names and types

        // #2 'timestamp' field name
        Assert.assertTrue(jsonResBody.has(timestampName),
                "The ErrorMessageDto should contain the '" + timestampName + "' field.");
        // #3 'timestamp' field type
        JsonElement timestampField = jsonResBody.get(timestampName);
        Assert.assertTrue(timestampField.isJsonPrimitive(),
                "The '" + timestampName + "' field should be a JSON primitive.");
        Assert.assertTrue(timestampField.getAsJsonPrimitive().isString(),
                "The '" + timestampName + "' field should be a String.");
        // Check that the 'timestamp' has a valid date-time format
        // Convert the timestamp to a Java String using the .getAsString() method
        String dateTime = timestampField.getAsString();
        // Parse the timestamp String to LocalDateTime using the LocalDateTime.parse() method
        try {
            LocalDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            // If the timestamp does not have a valid date-time format, fail the test
            Assert.fail("The '" + timestampName + "' field should have a valid date-time format.");
        }

        // #2 'status' field name
        Assert.assertTrue(jsonResBody.has(statusName),
                "The ErrorMessageDto should contain the '" + statusName + "' field.");
        // #4 'status' field type
        JsonElement statusField = jsonResBody.get(statusName);
        Assert.assertTrue(statusField.isJsonPrimitive(),
                "The '" + statusName + "' field should be a JSON primitive.");
        Assert.assertTrue(statusField.getAsJsonPrimitive().isNumber(),
                "The '" + statusName + "' field should be a number.");
        // Check that the status is an int32.
        // Convert the status number to String using the .getAsString() method
        String status = statusField.getAsString();
        // Parse the status String to int using the Integer.parseInt(String s) method
        try {
            Integer.parseInt(status);
        } catch (NumberFormatException e) {
            // If the status is not an int32, fail the test
            Assert.fail("The '" + statusName + "' field should be an int32.");
        }

        // #2 'error' field name
        Assert.assertTrue(jsonResBody.has(errorName),
                "The ErrorMessageDto should contain the '" + errorName + "' field.");
        // #5 'error' field type
        JsonElement errorField = jsonResBody.get(errorName);
        Assert.assertTrue(errorField.isJsonPrimitive(),
                "The '" + errorName + "' field should be a JSON primitive.");
        Assert.assertTrue(errorField.getAsJsonPrimitive().isString(),
                "The '" + errorName + "' field should be a String.");

        // #2 'message' field name
        Assert.assertTrue(jsonResBody.has(messageName),
                "The ErrorMessageDto should contain the '" + messageName + "' field.");

        // #2 'path' field name
        Assert.assertTrue(jsonResBody.has(pathName),
                "The ErrorMessageDto should contain the '" + pathName + "' field.");
        // #6 'path' field type
        JsonElement pathField = jsonResBody.get(pathName);
        Assert.assertTrue(pathField.isJsonPrimitive(),
                "The '" + pathName + "' field should be a JSON primitive.");
        Assert.assertTrue(pathField.getAsJsonPrimitive().isString(),
                "The '" + pathName + "' field should be a String.");
        return jsonResBody;
    }

    /**
     * Checks that the response body matches the ErrorMessageDto schema
     * with the 'message' field of type String.
     * <p>
     * First, the common part of the ErrorMessageDto schema is checked
     * using the checkCommonPartOfErrorMessageDtoSchema() method.
     * Then, the 'message' field type is checked.
     * <p>
     * Expected results for the 'message' field:
     * - The field is a JSON primitive. (#1)
     * - The field type is String. (#2)
     * <p>
     * ErrorMessageDto {
     * timestamp    string($date-time)
     * status       integer($int32)
     * error        string
     * message      string
     * path         string
     * }
     * <p>
     * Note:
     * This schema variant is returned by the API for 401 (Unauthorized)
     * and 409 (Conflict) responses, as well as for some 400 (Bad Request) responses.
     * According to the API documentation, the 'message' field should be an object.
     *
     * @param responseBody the response body as a Java String
     */
    public static void checkErrorMessageDtoStringMessageSchema(String responseBody) {
        JsonObject jsonResBody = checkCommonPartOfErrorMessageDtoSchema(responseBody);

        // #1 Check that the 'message' field is a JSON primitive
        JsonElement messageField = jsonResBody.get(ApiConstants.MESSAGE_FIELD_NAME);
        Assert.assertTrue(messageField.isJsonPrimitive(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a JSON primitive.");
        // #2 Check that the 'message' field is a String
        Assert.assertTrue(messageField.getAsJsonPrimitive().isString(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a String.");

    }

    /**
     * Checks that the response body matches the ErrorMessageDto schema
     * with the 'message' field of type Object.
     * <p>
     * First, the common part of the ErrorMessageDto schema is checked
     * using the checkCommonPartOfErrorMessageDtoSchema() method.
     * Then, the 'message' field type is checked.
     * <p>
     * Expected result for the 'message' field:
     * - The field is a JSON object. (#1)
     * <p>
     * ErrorMessageDto {
     * timestamp    string($date-time)
     * status       integer($int32)
     * error        string
     * message      object
     * path         string
     * }
     * <p>
     * This schema variant corresponds to the ErrorMessageDto schema
     * defined in the API documentation.
     *
     * @param responseBody the response body as a Java String
     */
    public static void checkErrorMessageDtoObjectMessageSchema(String responseBody){
        JsonObject jsonResBody = checkCommonPartOfErrorMessageDtoSchema(responseBody);
        JsonElement messageField = jsonResBody.get(ApiConstants.MESSAGE_FIELD_NAME);
        // #1 Check that the 'message' field is a JSON object
        Assert.assertTrue(messageField.isJsonObject(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a JSON object.");
    }


}
