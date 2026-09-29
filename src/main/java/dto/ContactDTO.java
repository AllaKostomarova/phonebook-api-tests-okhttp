package dto;

/*
 "contact":
    {
      "id": "string",
      "name": "string",
      "lastName": "string",
      "email": "string",
      "phone": "688465879920997",
      "address": "string",
      "description": "string"
    }
 */

import lombok.*;

@Data
@Builder
public class ContactDTO {
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String description;

}
