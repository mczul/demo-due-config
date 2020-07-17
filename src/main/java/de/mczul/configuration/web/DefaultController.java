package de.mczul.configuration.web;

import de.mczul.configuration.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(RestConstants.URL_API_ROOT)
public class DefaultController {

    final ScheduledConfigRepository scheduledConfigRepository;
    final ScheduledConfigService scheduledConfigService;
    final ScheduledConfigMapper scheduledConfigMapper;


    @GetMapping
    public List<ScheduledConfigDto> getScheduledConfigs(
            @RequestParam(name = RestConstants.REQUEST_PARAM_PAGE_INDEX, required = false, defaultValue = "0") int pageIndex,
            @RequestParam(name = RestConstants.REQUEST_PARAM_PAGE_SIZE, required = false, defaultValue = "10") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("key", "validFrom"));
        Page<ScheduledConfigEntry> domainPage = scheduledConfigRepository.findAll(pageRequest);

        return scheduledConfigMapper.fromDomainList(domainPage.toList());
    }

    @PostMapping
    @ResponseBody
    public ScheduledConfigDto postScheduledConfig(@RequestBody @Valid ScheduledConfigDto dto) {
        ScheduledConfigEntry domain = scheduledConfigMapper.toDomain(dto);
        return scheduledConfigMapper.fromDomain(scheduledConfigService.set(domain));
    }

}
