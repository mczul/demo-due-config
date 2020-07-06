package de.mczul.configuration.common;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ScheduledConfigMapper {

    ScheduledConfigDto fromDomain(ScheduledConfigEntry domain);

    ScheduledConfigEntry toDomain(ScheduledConfigDto dto);

    List<ScheduledConfigDto> fromDomainList(List<ScheduledConfigEntry> domainList);

    List<ScheduledConfigEntry> toDomainList(List<ScheduledConfigDto> dto);

}
