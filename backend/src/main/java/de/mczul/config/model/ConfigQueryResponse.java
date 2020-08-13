package de.mczul.config.model;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class ConfigQueryResponse {
    private LocalDateTime referenceTime;
    private String key;
    private String value;
}
