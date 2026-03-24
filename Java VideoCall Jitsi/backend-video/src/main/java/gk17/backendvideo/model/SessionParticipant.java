package gk17.backendvideo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "session_participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SessionParticipantId.class)
public class SessionParticipant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Session session;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User patient;
}
