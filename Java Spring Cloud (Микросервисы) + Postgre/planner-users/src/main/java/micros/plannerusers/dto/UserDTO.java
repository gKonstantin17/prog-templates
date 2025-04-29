package micros.plannerusers.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String username;

    private Integer pageNumber;
    private Integer pageSize;

    private String sortColumn;
    private String sortDirection;

}
