package de.mczul.config.web;

import de.mczul.config.AppConstants;
import de.mczul.config.model.ConfigQueryResponse;
import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.service.ScheduledConfigMapper;
import de.mczul.config.service.ScheduledConfigRepository;
import de.mczul.config.service.ScheduledConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(RestConstants.PATH_PREFIX_API)
public class DefaultController {
    final ScheduledConfigRepository scheduledConfigRepository;
    final ScheduledConfigService scheduledConfigService;
    final ScheduledConfigMapper scheduledConfigMapper;

    @GetMapping
    public ResponseEntity<List<ScheduledConfigDto>> getScheduledConfigs(
            @RequestParam(name = RestConstants.QUERY_PARAM_PAGE_INDEX, required = false, defaultValue = "0") int pageIndex,
            @RequestParam(name = RestConstants.QUERY_PARAM_PAGE_SIZE, required = false, defaultValue = "10") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("key", "validFrom"));
        Page<ScheduledConfigEntry> domainPage = scheduledConfigRepository.findAll(pageRequest);

        List<ScheduledConfigDto> dtos = domainPage.stream().map(scheduledConfigMapper::toDto).collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<ScheduledConfigDto> postScheduledConfig(@RequestBody @Valid ScheduledConfigDto dto) {
        ScheduledConfigEntry submittedEntry = scheduledConfigMapper.toEntry(dto);
        ScheduledConfigEntry savedEntry = scheduledConfigService.set(submittedEntry);
        ScheduledConfigDto result = scheduledConfigMapper.toDto(savedEntry);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping(path = "{" + RestConstants.PATH_VARIABLE_KEY + "}")
    public ResponseEntity<ConfigQueryResponse> queryByKey(
            @NotBlank(message = "{NotBlank.scheduledConfig.key.message}") @PathVariable(name = RestConstants.PATH_VARIABLE_KEY) String key
    ) {
        Optional<ScheduledConfigEntry> entryOptional = scheduledConfigService.get(key);
        var queryResponse = ConfigQueryResponse.builder()
                .key(key.toLowerCase(AppConstants.DEFAULT_LOCALE))
                .referenceTime(ZonedDateTime.now())
                .build();
        if (entryOptional.isEmpty()) {
            queryResponse.setValue(null);
        } else {
            queryResponse.setValue(entryOptional.get().getValue());
        }

        return ResponseEntity.ok(queryResponse);
    }

}
