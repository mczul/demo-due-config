package de.mczul.config.model;

import de.mczul.config.validation.NullOrNotBlank;
import de.mczul.config.validation.ValidConfigKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.ZonedDateTime;
import java.util.Objects;

public interface ScheduledConfig {

    /**
     * TODO: Missing
     *
     * @return
     */
    @Positive(message = "{Positive.scheduledConfig.id.message}")
    Integer getId();

    void setId(Integer id);

    ScheduledConfig withId(Integer value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotBlank(message = "{NotBlank.scheduledConfig.key.message}")
    @ValidConfigKey
    String getKey();

    void setKey(String value);

    ScheduledConfig withKey(String value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotNull(message = "{NotNull.scheduledConfig.validFrom.message}")
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
    @NotNull(message = "{NotNull.scheduledConfig.created.message}")
    @PastOrPresent(message = "{PastOrPresent.scheduledConfig.created.message}")
    ZonedDateTime getCreated();

    void setCreated(ZonedDateTime created);

    ScheduledConfig withCreated(ZonedDateTime value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NullOrNotBlank(message = "{NullOrNotBlank.scheduledConfig.comment.message}")
    String getComment();

    void setComment(String value);

    ScheduledConfig withComment(String value);

    @NotBlank(message = "{NotBlank.scheduledConfig.author.message}")
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
