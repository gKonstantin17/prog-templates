package angms.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyEvent {
    private LocalDateTime date;
    private String descr;
}
