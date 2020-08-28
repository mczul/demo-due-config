package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.model.ScheduledConfigPast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ScheduledConfigMapperDecorator implements ScheduledConfigMapper {
    @Autowired
    @Qualifier("delegate")
    private ScheduledConfigMapper delegate;

    @Autowired
    private ScheduledConfigRepository scheduledConfigRepository;

    @Override
    public ScheduledConfigDto fromDomain(ScheduledConfigEntry entry) {
        ScheduledConfigDto result = delegate.fromDomain(entry);

        // Comment history
        List<ScheduledConfigEntry> entries = scheduledConfigRepository.findHistory(entry.getKey(), entry.getCreated());
        List<ScheduledConfigPast> history = entries.stream().map(delegate::toPast).collect(Collectors.toUnmodifiableList());

        return result.withHistory(history);
    }

}
