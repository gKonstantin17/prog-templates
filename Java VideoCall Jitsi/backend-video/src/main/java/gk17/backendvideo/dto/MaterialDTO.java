package gk17.backendvideo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {
    private Long id;
    private Long sessionId;
    private String fileName;
    private LocalDateTime uploadedAt;
}
