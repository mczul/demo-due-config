package de.mczul.configuration.common;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledConfigDto {

    private Integer id;
    @NotNull(message = "{NotNull.scheduledConfig.key}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key}")
    private String key;
    @NotNull(message = "{NotNull.scheduledConfig.validFrom}")
    private LocalDateTime validFrom;
    private String value;

}

