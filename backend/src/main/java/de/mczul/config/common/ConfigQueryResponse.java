package de.mczul.config.common;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class ConfigQueryResponse {
    private LocalDateTime referenceTime;
    private String value;

}
