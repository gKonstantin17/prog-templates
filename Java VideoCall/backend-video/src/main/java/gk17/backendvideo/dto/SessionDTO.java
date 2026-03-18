package gk17.backendvideo.dto;

import gk17.backendvideo.model.Session;
import gk17.backendvideo.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    private Long id;
    private String title;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long logopedId;
    private String logopedName;
    private List<Long> patientIds;
    private boolean canJoin;

    public Session toSession() {
        Session session = new Session();
        session.setId(this.id);
        session.setTitle(this.title);
        session.setRoomName(this.roomName);
        session.setStartTime(this.startTime);
        session.setEndTime(this.endTime);
        session.setStatus(SessionStatus.valueOf(this.status));
        return session;
    }
}
