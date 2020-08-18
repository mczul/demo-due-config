package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigEntry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@DisplayName("ScheduledConfigService unit tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ScheduledConfigServiceTest {
    @Mock
    private ScheduledConfigRepository scheduledConfigRepository;
    @InjectMocks
    private ScheduledConfigService underTest;

    @Test
    void set_must_convert_keys_to_lowercase() {
        final var key = "MY_UPPERCASE_KEY";
        final var sample = ScheduledConfigEntry.builder()
                .key(key)
                .build();
        final var argCaptor = ArgumentCaptor.forClass(ScheduledConfigEntry.class);

        underTest.set(sample);
        verify(scheduledConfigRepository).save(argCaptor.capture());
        assertThat(argCaptor.getValue().getKey()).as("Service does not convert keys to lowercase when saving.").isLowerCase();
    }

    @Disabled("Not yet implemented")
    @Test
    void cleanup_must_only_delete_entries_with_newer_valid_from_timestamp() {
        // TODO: Implementation is missing
    }
}