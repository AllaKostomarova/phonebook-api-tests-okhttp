package dto;

/*
 {
  "message": "string"
}
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessMassageDTO {
    private String message;
}
