package de.mczul.config.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@With
public class ValidationErrorResponse {
    @Singular
    private List<Violation> violations = new ArrayList<>();
}
