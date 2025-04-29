package micros.plannerusers.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class UserKcDTO {
    private String id;

    private String email;
    private String userpassword;
    private String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserKcDTO user = (UserKcDTO) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
