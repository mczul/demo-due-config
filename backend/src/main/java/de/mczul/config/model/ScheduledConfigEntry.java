package de.mczul.config.model;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_config", schema = ScheduledConfigEntry.SCHEMA_NAME)
public class ScheduledConfigEntry implements ScheduledConfig {
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
    private ZonedDateTime validFrom;

    @Column(name = "value")
    private String value;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "comment")
    private String comment;

    @Column(name = "author")
    private String author;

    @Override
    public boolean equals(Object other) {
        return isEqual(other);
    }

    @Override
    public int hashCode() {
        return calcHashCode();
    }

}
