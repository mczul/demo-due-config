package de.mczul.config.web;

import de.mczul.config.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(RestConstants.PATH_PREFIX_API)
public class DefaultController {

    final ScheduledConfigRepository scheduledConfigRepository;
    final ScheduledConfigService scheduledConfigService;
    final ScheduledConfigMapper scheduledConfigMapper;


    @GetMapping
    public List<ScheduledConfigDto> getScheduledConfigs(
            @RequestParam(name = RestConstants.QUERY_PARAM_PAGE_INDEX, required = false, defaultValue = "0") int pageIndex,
            @RequestParam(name = RestConstants.QUERY_PARAM_PAGE_SIZE, required = false, defaultValue = "10") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("key", "validFrom"));
        Page<ScheduledConfigEntry> domainPage = scheduledConfigRepository.findAll(pageRequest);

        return scheduledConfigMapper.fromDomainList(domainPage.toList());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<ScheduledConfigDto> postScheduledConfig(@RequestBody @Valid ScheduledConfigDto dto) {
        ScheduledConfigEntry domain = scheduledConfigMapper.toDomain(dto);
        ScheduledConfigDto result = scheduledConfigMapper.fromDomain(scheduledConfigService.set(domain));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping(path = "{" + RestConstants.PATH_VARIABLE_KEY + "}")
    public ResponseEntity<ScheduledConfigDto> getConfig(
            @PathVariable(name = RestConstants.PATH_VARIABLE_KEY) String key
    ) {
        Optional<ScheduledConfigEntry> entryOptional = scheduledConfigService
                .get(key);
        if (entryOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ScheduledConfigDto result = scheduledConfigMapper.fromDomain(entryOptional.get());
        return ResponseEntity.ok(result);
    }

}
