package de.mczul.config.common;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ScheduledConfigMapper {

    ScheduledConfigDto fromDomain(ScheduledConfigEntry domain);

    List<ScheduledConfigDto> fromDomainList(List<ScheduledConfigEntry> domainList);

    ScheduledConfigEntry toDomain(ScheduledConfigDto dto);
    List<ScheduledConfigEntry> toDomainList(List<ScheduledConfigDto> dto);

}