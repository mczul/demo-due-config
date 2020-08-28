package de.mczul.config.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * TODO: Need refactoring in order to reflect different requirements for in- and outbound communication
 */

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledConfigDto implements ScheduledConfig {

    private Integer id;
    private String key;
    private ZonedDateTime validFrom;
    private String value;
    private ZonedDateTime created;
    private String comment;
    private String author;
    private List<ScheduledConfigPast> history;

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object other) {
        return isEqual(other);
    }

    @Override
    public int hashCode() {
        return calcHashCode();
    }

}

