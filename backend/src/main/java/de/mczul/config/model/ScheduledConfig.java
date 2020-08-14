package de.mczul.config.model;

import de.mczul.config.validation.ValidConfigKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

public interface ScheduledConfig {

    Integer getId();

    void setId(Integer id);

    @NotNull(message = "{NotNull.scheduledConfig.key.message}")
    @NotBlank(message = "{NotBlank.scheduledConfig.key.message}")
    @ValidConfigKey
    String getKey();

    void setKey(String value);

    @NotNull(message = "{NotNull.scheduledConfig.validFrom.message}")
    LocalDateTime getValidFrom();

    void setValidFrom(LocalDateTime validFrom);

    String getValue();

    void setValue(String value);

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

    default int calcHashCode() {
        return Objects.hashCode(getId());
    }

}
