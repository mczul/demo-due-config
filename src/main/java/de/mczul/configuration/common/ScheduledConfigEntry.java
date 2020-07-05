package de.mczul.configuration.common;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
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

    @NotNull(message = "{NotNull.scheduledConfig.key}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key}")
    @Column(name = "key")
    private String key;

    @NotNull(message = "{NotNull.scheduledConfig.validFrom}")
    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "value")
    private String value;
}