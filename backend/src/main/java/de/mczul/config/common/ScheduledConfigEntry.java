package de.mczul.config.common;

import de.mczul.config.validation.ValidConfigKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_config", schema = ScheduledConfigEntry.SCHEMA_NAME)
public class ScheduledConfigEntry {
    public static final String SEQ_NAME = "seq_scheduled_config";
    public static final String SCHEMA_NAME = "config";

    @Id
    @SequenceGenerator(name = SEQ_NAME, schema = SCHEMA_NAME)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "{NotNull.scheduledConfig.key.message}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key.message}")
    @ValidConfigKey
    @Column(name = "key")
    private String key;

    @NotNull(message = "{NotNull.scheduledConfig.validFrom.message}")
    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "value")
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduledConfigEntry that = (ScheduledConfigEntry) o;
        if (id == null) {
            return false;
        }

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}