package dto;

/*
 {
  "timestamp": "2026-09-20T10:36:59.434Z",
  "status": 0,
  "error": "string",
  "message": {},
  "path": "string"
 }
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessageDTO<T> {

    private String timestamp;
    private Integer status;
    private String error;
    private T message;
    private String path;
}
