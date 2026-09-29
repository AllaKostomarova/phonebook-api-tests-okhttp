package dto;

/*{
  "contacts": [
    {}
  ]
}
 */

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class AllContactsDTO {
    private ArrayList<ContactDTO> arrContacts;
}
