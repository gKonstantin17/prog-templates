package gk17.backendvideo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionParticipantId implements Serializable {
    private Long session;
    private Long patient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionParticipantId that = (SessionParticipantId) o;
        return Objects.equals(session, that.session) &&
               Objects.equals(patient, that.patient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, patient);
    }
}
