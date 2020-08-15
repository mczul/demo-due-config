package de.mczul.config.model;

import de.mczul.config.validation.ValidConfigKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

public interface ScheduledConfig {

    /**
     * TODO: Missing implemenation
     * @return
     */
    Integer getId();

    void setId(Integer id);

    /**
     * TODO: Missing implemenation
     * @return
     */
    @NotNull(message = "{NotNull.scheduledConfig.key.message}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key.message}")
    @ValidConfigKey
    String getKey();

    void setKey(String value);

    /**
     * TODO: Missing implemenation
     * @return
     */
    @NotNull(message = "{NotNull.scheduledConfig.validFrom.message}")
    LocalDateTime getValidFrom();

    void setValidFrom(LocalDateTime validFrom);

    /**
     * TODO: Missing implemenation
     * @return
     */
    String getValue();

    void setValue(String value);

    /**
     * TODO: Missing implemenation
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
     * TODO: Missing implemenation
     * @return
     */
    default int calcHashCode() {
        return Objects.hashCode(getId());
    }

}
