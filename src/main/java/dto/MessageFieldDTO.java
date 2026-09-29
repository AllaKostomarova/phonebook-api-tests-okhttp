package dto;

import lombok.Builder;
import lombok.Data;

/*
"message": {
    "password": " At least 8 characters; Must contain at least 1 uppercase letter, 1 lowercase letter, and 1 number; Can contain special characters [@$#^&*!]",
    "username": "должно иметь формат адреса электронной почты"
  }
 */
@Data
@Builder
public class MessageFieldDTO {
    private String password;
    private String username;
}
