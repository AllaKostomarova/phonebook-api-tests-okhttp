package helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.ApiConstants;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class SchemaValidator {
    /**
     * Checks that the response body
     * matches the TokenDto schema defined in the API documentation.
     * <p>
     * Expected results:
     * - The response contains exactly one field (#1)
     * - The field name is 'token' (#2)
     * - The field type is string (#3)
     * <p>
     * TokenDto{
     * token	string
     * }
     *
     * @param responseBody the response body as a Java String
     */
    public static void checkTokenDtoSchema(String responseBody) {
        Assert.assertNotNull(responseBody,
                "Response body should not be null.");
            /*
            Convert the response body from a String to a JsonObject:
            1) Parse the String using JsonParser.parseString()
            2) Convert the JsonElement to a JsonObject using the .getAsJsonObject() method
             */
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assert.assertTrue(jsonElement.isJsonObject(),
                "Response body should be a JSON object.");
        JsonObject jsonResBody = jsonElement.getAsJsonObject();
        // #1
        Assert.assertEquals(jsonResBody.size(), 1,
                "The response body should contain exactly one field.");
        // #2
        Assert.assertTrue(jsonResBody.has(ApiConstants.TOKEN_FIELD_NAME),
                "The response body should contain the '" + ApiConstants.TOKEN_FIELD_NAME + "' field.");
        // #3
        JsonElement tokenField = jsonResBody.get(ApiConstants.TOKEN_FIELD_NAME);
        Assert.assertTrue(tokenField.isJsonPrimitive(),
                "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should be a JSON primitive.");
        Assert.assertTrue(tokenField.getAsJsonPrimitive().isString(),
                "The '" + ApiConstants.TOKEN_FIELD_NAME + "' field should be a string.");
    }

    /**
     * Checks the common part of the ErrorMessageDto schema.
     * <p>
     * Validates the fields that are common to both error response variants:
     * - the response contains exactly 5 fields; (#1)
     * - the expected fields are present; (#2)
     * - the 'timestamp' field is a string with a valid date-time format; (#3)
     * - the 'status' field is an int32; (#4)
     * - the 'error' field is a string; (#5)
     * - the 'path' field is a string. (#6)
     * <p>
     * The type of the 'message' field is not validated by this method,
     * because it can be either a string or an object depending on the response.
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

        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assert.assertTrue(jsonElement.isJsonObject(),
                "Response body should be a JSON object.");
        JsonObject jsonResBody = jsonElement.getAsJsonObject();
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
                "The '" + timestampName + "' field should be a string.");
        // Check that the 'timestamp' has a valid date-time format
        // Convert the timestamp to a Java string using the .getAsString() method
        String dateTime = timestampField.getAsString();
        // Parse the timestamp string into a LocalDateTime using the LocalDateTime.parse() method
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
        // Parse the status String into an int using the Integer.parseInt(String s) method
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
                "The '" + errorName + "' field should be a string.");

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
                "The '" + pathName + "' field should be a string.");
        return jsonResBody;
    }

    /**
     * Checks that the response body matches the ErrorMessageDto schema
     * with the 'message' field of type string.
     * <p>
     * First, the common part of the ErrorMessageDto schema is checked
     * using the checkCommonPartOfErrorMessageDtoSchema() method.
     * Then, the 'message' field type is checked.
     * <p>
     * Expected results for the 'message' field:
     * - The field is a JSON primitive. (#1)
     * - The field type is string. (#2)
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
        Assert.assertNotNull(responseBody,
                "Response body should not be null.");
        JsonObject jsonResBody = checkCommonPartOfErrorMessageDtoSchema(responseBody);

        // #1 Check that the 'message' field is a JSON primitive
        JsonElement messageField = jsonResBody.get(ApiConstants.MESSAGE_FIELD_NAME);
        Assert.assertTrue(messageField.isJsonPrimitive(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a JSON primitive.");
        // #2 Check that the 'message' field is a string
        Assert.assertTrue(messageField.getAsJsonPrimitive().isString(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a string.");

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
        Assert.assertNotNull(responseBody,
                "Response body should not be null.");
        JsonObject jsonResBody = checkCommonPartOfErrorMessageDtoSchema(responseBody);
        JsonElement messageField = jsonResBody.get(ApiConstants.MESSAGE_FIELD_NAME);
        // #1 Check that the 'message' field is a JSON object
        Assert.assertTrue(messageField.isJsonObject(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be an object.");
    }

    /**
     * Checks that the response body matches the top-level
     * ContactsDto structure defined in the API documentation.
     * <p>
     * Expected results:
     * - The response contains exactly one field (#1)
     * - The field name is 'contacts' (#2)
     * - The 'contacts' field is an JSON array (#3)
     * <p>
     *  IMPORTANT:
     *  This method does not validate the structure of individual
     *  contacts or whether the array is empty.
     *
     * ContactsDto{
     * contacts	[]
     * }
     * *
     * @param responseBody the response body as a Java String
     */
    public static void checkContactsResponseStructure(String responseBody){
        Assert.assertNotNull(responseBody,
                "Response body should not be null.");
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assert.assertTrue(jsonElement.isJsonObject(),
                "Response body should be a JSON object.");
        JsonObject jsonResBody = jsonElement.getAsJsonObject();
        // #1
        Assert.assertEquals(jsonResBody.size(), 1,
                "The response body should contain exactly one field.");
        // #2
        Assert.assertTrue(jsonResBody.has(ApiConstants.CONTACTS_FIELD_NAME),
                "The ContactsDto should contain the '" + ApiConstants.CONTACTS_FIELD_NAME + "' field.");
        // #3
        JsonElement contactsField = jsonResBody.get(ApiConstants.CONTACTS_FIELD_NAME);
        Assert.assertTrue(contactsField.isJsonArray(),
                "The '" + ApiConstants.CONTACTS_FIELD_NAME + "' field should be a JSON array.");

    }

    /**
     * Checks that the response body matches the complete
     * ContactsDto structure defined in the API documentation.
     *
     * First, validates the top-level response structure using
     * checkContactsResponseStructure().
     * Then, validates each contact using checkContactJson().
     *
     * <p>
     * Expected results:
     * - The response matches the top-level ContactsDto structure.
     * - Each element in the 'contacts' array is a JSON object.
     * - Each contact object matches the ContactDto schema.
     *
     * IMPORTANT:
     * This method validates the structure of each contact
     * but does not require the contacts array to be non-empty.
     *
     * <p>
     * ContactsDto {
     *     contacts: [ContactDto {
     *         id: string
     *         name*: string
     *         lastName*: string
     *         email: string
     *         phone: string
     *         address*: string
     *         description: string
     *     }]
     * }
     *
     * @param responseBody the response body as a Java String
     */
    public static void checkContactsResponseAndItemsStructure(String responseBody){
        checkContactsResponseStructure(responseBody);

        JsonObject jsonResBody = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray contactsArray = jsonResBody.getAsJsonArray(ApiConstants.CONTACTS_FIELD_NAME);
        for (JsonElement contact : contactsArray){
            Assert.assertTrue(contact.isJsonObject(),
                    "Each contact should be a JSON object.");
            checkContactJson(contact.getAsJsonObject());
        }
    }

    /**
     * Checks that the response body
     * matches the ContactDto schema defined in the API documentation
     * using the helper method checkContactJson().
     *
     * Expected results:
     * - The response contains exactly 7 fields
     * - All expected fields are present and have the data type
     * specified in the API documentation
     *
     * ContactDto{
     * id	string
     * name*	string
     * lastName*	string
     * email	string
     * phone	string pattern: ^\d{10,15}$
     * address*	string
     * description	string
     * }
     *
     * @param responseBody the response body as a Java String
     */
    public static void checkContactDtoSchema(String responseBody){
        Assert.assertNotNull(responseBody,
                "Response body should not be null.");
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assert.assertTrue(jsonElement.isJsonObject(),
                "Response body should be a JSON object.");
        JsonObject jsonResBody = jsonElement.getAsJsonObject();

        checkContactJson(jsonResBody);
    }

    /**
     * Checks that a contact JSON object
     * matches the ContactDto schema defined in the API documentation.
     *
     * Expected results:
     * - The response contains exactly 7 fields (#1)
     * - All expected fields are present and have the data type
     * specified in the API documentation (#2)
     *
     * ContactDto{
     * id	string
     * name*	string
     * lastName*	string
     * email	string
     * phone	string pattern: ^\d{10,15}$
     * address*	string
     * description	string
     * }
     *
     * @param jsonResBody the contact data as a JsonObject
     */

    private static void checkContactJson(JsonObject jsonResBody){
        String [] contactFieldsArray = {ApiConstants.ID_FIELD_NAME,
                ApiConstants.NAME_FIELD_NAME,
                ApiConstants.LASTNAME_FIELD_NAME,
                ApiConstants.EMAIL_FIELD_NAME,
                ApiConstants.PHONE_FIELD_NAME,
                ApiConstants.ADDRESS_FIELD_NAME,
                ApiConstants.DESCRIPTION_FIELD_NAME};
        // #1
        Assert.assertTrue(jsonResBody.size() == ApiConstants.CONTACT_SIZE,
                "The response body should contain " + ApiConstants.CONTACT_SIZE + " fields.");
        // #2
        for (String field : contactFieldsArray){
            Assert.assertTrue(jsonResBody.has(field),
                    "The ContactDto object should contain the '" + field + "' field.");
            JsonElement fieldContent = jsonResBody.get(field);
            Assert.assertTrue(fieldContent.isJsonPrimitive(),
                    "The '" + field + "' field should be a JSON primitive.");
            Assert.assertTrue(fieldContent.getAsJsonPrimitive().isString(),
                    "The '" + field + "' field should be a string.");
        }
    }

    public static void checkSuccessMessageDtoSchema(String responseBody){
        Assert.assertNotNull(responseBody, " The response body should not be null. ");
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assert.assertTrue(jsonElement.isJsonObject(),
                "The ResponseMessageDto object should be a Json object.");
        JsonObject jsonResBody = jsonElement.getAsJsonObject();
        Assert.assertEquals(jsonResBody.size(), 1,
                "The ResponseMessageDto object should contain exactly one field.");
        Assert.assertTrue(jsonResBody.has(ApiConstants.MESSAGE_FIELD_NAME),
                "The ResponseMessageDTO should contain the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field.");
        JsonElement messageField = jsonResBody.get(ApiConstants.MESSAGE_FIELD_NAME);
        Assert.assertTrue(messageField.isJsonPrimitive(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a Json primitive.");
        Assert.assertTrue(messageField.getAsJsonPrimitive().isString(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a string.");
    }

    /**
     * Validates the structure of a JSON message object against an array of expected field names.
     *
     * Expected results:
     * - The message object contains exactly the expected fields (no missing or extra fields).
     * - Each field value is a JSON string.
     *
     * IMPORTANT:
     * The expected fields array must be defined separately for each test scenario.
     * There is no universal set of expected fields; it depends on the validation
     * errors expected in the specific test case.
     *
     * @param messageFieldsArray an array of expected field names for the current test scenario
     * @param responseBody the response body as a Java String
     */

    public static void checkMessageObjectJson(String[] messageFieldsArray, String responseBody){
        Assert.assertNotNull(messageFieldsArray,
                "The message fields array should not be null.");
        Assert.assertNotNull(responseBody,
                "The response body should not be null.");
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        Assert.assertTrue(jsonElement.isJsonObject(), "The response body should be a JSON object.");
        JsonObject jsonResBody = jsonElement.getAsJsonObject();
        Assert.assertTrue(jsonResBody.has(ApiConstants.MESSAGE_FIELD_NAME),
                "The ErrorMessageDto should contain the '" + ApiConstants.MESSAGE_FIELD_NAME + "' field.");
        JsonElement messageElement = jsonResBody.get(ApiConstants.MESSAGE_FIELD_NAME);
        Assert.assertTrue(messageElement.isJsonObject(),
                "The '" + ApiConstants.MESSAGE_FIELD_NAME + "' field should be a JSON object.");
        JsonObject messageJsonObject = messageElement.getAsJsonObject();
        // Check the message object size
        Assert.assertEquals(messageJsonObject.size(), messageFieldsArray.length,
                "The message object should contain " + messageFieldsArray.length + " fields.");
        // Check field names and value types
        for (String field : messageFieldsArray){
            Assert.assertTrue(messageJsonObject.has(field),
                    "The message object should contain the '" + field + "' field.");
            JsonElement fieldContent = messageJsonObject.get(field);
            Assert.assertTrue(fieldContent.isJsonPrimitive(),
                    "The '" + field + "' field should be a JSON primitive.");
            Assert.assertTrue(fieldContent.getAsJsonPrimitive().isString(),
                    "The '" + field + "' field should be a string.");
        }
    }

}
