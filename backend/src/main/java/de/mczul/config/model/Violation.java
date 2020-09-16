package de.mczul.config.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@With
public class Violation {
    private String fieldName;
    private String message;
}
