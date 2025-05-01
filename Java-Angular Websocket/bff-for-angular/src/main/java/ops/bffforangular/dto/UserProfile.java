package ops.bffforangular.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {
    private String preferredUsername;
    private String email;
    private String id;
}
