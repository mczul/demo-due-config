package de.mczul.config.model;

import de.mczul.config.validation.NullOrNotBlank;
import de.mczul.config.validation.ValidConfigKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
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

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotBlank(message = "{NotBlank.scheduledConfig.key.message}")
    @ValidConfigKey
    String getKey();

    void setKey(String value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotNull(message = "{NotNull.scheduledConfig.validFrom.message}")
    LocalDateTime getValidFrom();

    void setValidFrom(LocalDateTime validFrom);

    /**
     * TODO: Missing
     *
     * @return
     */
    String getValue();

    void setValue(String value);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NotNull(message = "{NotNull.scheduledConfig.created.message}")
    @PastOrPresent(message = "{PastOrPresent.scheduledConfig.created.message}")
    LocalDateTime getCreated();

    void setCreated(LocalDateTime created);

    /**
     * TODO: Missing
     *
     * @return
     */
    @NullOrNotBlank(message = "{NullOrNotBlank.scheduledConfig.comment.message}")
    String getComment();

    void setComment(String value);

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
