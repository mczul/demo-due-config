package de.mczul.config.common;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class ConfigQueryResponse {
    private LocalDateTime referenceTime;
    @NotNull(message = "{NotNull.scheduledConfig.key}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key}")
    private String key;
    private String value;

}
