package gk17.backendvideo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logoped_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User logoped;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "room_name", unique = true, nullable = false, length = 255)
    private String roomName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.SCHEDULED;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SessionParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SessionMaterial> materials = new ArrayList<>();

    public void addParticipant(SessionParticipant participant) {
        participants.add(participant);
        participant.setSession(this);
    }

    public void removeParticipant(SessionParticipant participant) {
        participants.remove(participant);
        participant.setSession(null);
    }

    public void addMaterial(SessionMaterial material) {
        materials.add(material);
        material.setSession(this);
    }

    public void removeMaterial(SessionMaterial material) {
        materials.remove(material);
        material.setSession(null);
    }
}
