package de.mczul.configuration.web;

import de.mczul.configuration.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/default/v1")
public class DefaultController {

    final ScheduledConfigRepository scheduledConfigRepository;
    final ScheduledConfigService scheduledConfigService;
    final ScheduledConfigMapper scheduledConfigMapper;


    @GetMapping
    public List<ScheduledConfigDto> getScheduledConfig() {
        List<ScheduledConfigEntry> domainList = scheduledConfigRepository.findAll();

        return scheduledConfigMapper.fromDomainList(domainList);
    }

    @PostMapping
    @ResponseBody
    public ScheduledConfigDto postScheduledConfig(@RequestBody @Valid ScheduledConfigDto dto) {
        ScheduledConfigEntry domain = scheduledConfigMapper.toDomain(dto);
        return scheduledConfigMapper.fromDomain(scheduledConfigService.set(domain));
    }

}
