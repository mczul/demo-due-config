package de.mczul.config.model;

import de.mczul.config.validation.NullOrNotBlank;
import de.mczul.config.validation.ValidConfigKey;
import de.mczul.config.validation.ValidationGroups.OnCreate;
import de.mczul.config.validation.ValidationGroups.OnUpdate;

import javax.validation.constraints.*;
import java.time.ZonedDateTime;
import java.util.Objects;

public interface ScheduledConfig {

    /**
     * TODO: Missing
     *
     * @return
     */
    @Null(message = "{ScheduledConfig.id.Null.message}", groups = {OnCreate.class})
    @NotNull(message = "{ScheduledConfig.id.NotNull.message}", groups = {OnUpdate.class})
    @Positive(message = "{ScheduledConfig.id.Positive.message}")
    Integer getId();

    void setId(Integer id);

    ScheduledConfig withId(Integer value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotBlank(message = "{ScheduledConfig.key.NotBlank.message}")
    @ValidConfigKey
    String getKey();

    void setKey(String value);

    ScheduledConfig withKey(String value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotNull(message = "{ScheduledConfig.validFrom.NotNull.message}")
    ZonedDateTime getValidFrom();

    void setValidFrom(ZonedDateTime validFrom);

    ScheduledConfig withValidFrom(ZonedDateTime value);

    /**
     * TODO: Missing
     *
     * @return
     */
    String getValue();

    void setValue(String value);

    ScheduledConfig withValue(String value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotNull(message = "{ScheduledConfig.created.NotNull.message}")
    @PastOrPresent(message = "{ScheduledConfig.created.PastOrPresent.message}")
    ZonedDateTime getCreated();

    void setCreated(ZonedDateTime created);

    ScheduledConfig withCreated(ZonedDateTime value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NullOrNotBlank(message = "{ScheduledConfig.comment.NullOrNotBlank.message}")
    String getComment();

    void setComment(String value);

    ScheduledConfig withComment(String value);

    @NotBlank(message = "{ScheduledConfig.author.NotBlank.message}")
    String getAuthor();

    void setAuthor(String name);

    ScheduledConfig withAuthor(String name);

    /**
     * TODO: Missing
     *
     * @return
     */
    default boolean isEqual(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        ScheduledConfig that = (ScheduledConfig) other;
        if (getId() == null) {
            return false;
        }

        return Objects.equals(getId(), that.getId());
    }

    /**
     * TODO: Missing
     *
     * @return
     */
    default int calcHashCode() {
        return Objects.hashCode(getId());
    }

}
