package de.mczul.config.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.ZonedDateTime;

@Data
@Builder
@With
public class ScheduledConfigPast implements ScheduledConfig {
    private Integer id;
    private String key;
    private ZonedDateTime validFrom;
    private String value;
    private ZonedDateTime created;
    private String comment;
    private String author;
}
