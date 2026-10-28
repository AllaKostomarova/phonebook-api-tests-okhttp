package dto;
/*
"message": {
        "name": "must not be blank",
        "lastName": "must not be blank",
        "email": "must not be blank" / "must be a well-formed email address",
        "phone": "must not be blank" / "Phone number must contain only digits! And length min 10, max 15!",
        "address": "must not be blank"
    }
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactMessageObjectDTO {
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String address;


}
