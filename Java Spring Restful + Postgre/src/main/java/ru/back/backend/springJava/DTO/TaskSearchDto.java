package ru.back.backend.springJava.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class TaskSearchDto {
    private String title;
    private Integer completed;
    private Long priorityId;
    private Long categoryId;
    private String email;

    // entity - содержит только одну дату
    // здесь для запроса указывается промежуток времени
    private Date dateFrom;
    private Date dateTo;

    // постраничность
    private Integer pageNumber;
    private Integer pageSize;

    // сортировка
    private String sortColumn;
    private String sortDirection;
}
