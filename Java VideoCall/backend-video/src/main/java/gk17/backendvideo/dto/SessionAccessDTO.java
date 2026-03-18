package gk17.backendvideo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionAccessDTO {
    private Long sessionId;
    private String status;
    private boolean canJoin;
}
