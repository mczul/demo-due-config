package de.mczul.configuration.common;

import lombok.*;

import javax.persistence.*;
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

    @Column(name = "key")
    private String key;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "value")
    private String value;
}