package ru.back.backend.springJava.DTO;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link ru.back.backend.springproj.entity.Category}
 */
@Value
public class CategoryDto implements Serializable {
    Long id;
    String title;
    Long completedCount;
    Long uncompletedCount;
    Long userId;
}