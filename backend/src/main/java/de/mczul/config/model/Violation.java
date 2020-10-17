package de.mczul.config.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Builder
@With
public class Violation {
    private String fieldName;
    private String message;
}
