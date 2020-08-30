package de.mczul.config.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import java.util.stream.Stream;

@DisplayName("ScheduledConfigEntry tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledConfigEntryTest extends AbstractScheduledConfigTest {

    @Override
    public Stream<ScheduledConfigEntry> buildValid() {
        return SampleProvider.buildValidEntries();
    }

    @Override
    public Stream<ScheduledConfigEntry> buildInvalid() {
        return SampleProvider.buildInvalidEntries();
    }

    @Override
    protected ScheduledConfig createInstance() {
        return ScheduledConfigEntry.builder().build();
    }
}
