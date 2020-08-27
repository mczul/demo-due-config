package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

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
        List<String> commentHistory = scheduledConfigRepository.loadCommentsByKey(entry.getKey());
        return result.withCommentHistory(commentHistory);
    }

}
