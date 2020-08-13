package de.mczul.config.model;

import de.mczul.config.validation.ValidConfigKey;
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
    @NotNull(message = "{NotNull.scheduledConfig.key.message}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key.message}")
    @ValidConfigKey
    private String key;
    @NotNull(message = "{NotNull.scheduledConfig.validFrom.message}")
    private LocalDateTime validFrom;
    private String value;

}

