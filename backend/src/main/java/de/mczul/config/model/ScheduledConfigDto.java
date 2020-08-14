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
public class ScheduledConfigDto implements ScheduledConfig {

    private Integer id;
    private String key;
    private LocalDateTime validFrom;
    private String value;

    @Override
    public boolean equals(Object other) {
        return isEqual(other);
    }

    @Override
    public int hashCode() {
        return calcHashCode();
    }

}

