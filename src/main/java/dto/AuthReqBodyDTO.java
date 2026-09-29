package dto;

/*{
  "username": "string",
  "password": "EUEDPB>zm%HUxI*\\ib b.+)JC]fG"
  }
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthReqBodyDTO {
    private String username;
    private String password;
}
