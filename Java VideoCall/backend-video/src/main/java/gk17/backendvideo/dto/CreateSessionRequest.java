package gk17.backendvideo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> patientIds;
}
