package gk17.backendvideo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinTokenResponse {
    private String url;
    private String token;
    private String roomName;
}
