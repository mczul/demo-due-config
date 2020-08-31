package de.mczul.config.web;

import de.mczul.config.AppConstants;
import de.mczul.config.model.ConfigQueryResponse;
import de.mczul.config.model.SampleProvider;
import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.service.ScheduledConfigMapper;
import de.mczul.config.service.ScheduledConfigRepository;
import de.mczul.config.service.ScheduledConfigService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("DefaultController unit tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
public class DefaultControllerTest {
    @Mock
    private ScheduledConfigMapper scheduledConfigMapper;
    @Mock
    private ScheduledConfigRepository scheduledConfigRepository;
    @Mock
    private ScheduledConfigService scheduledConfigService;

    @InjectMocks
    private DefaultController underTest;

    @BeforeAll
    static void beforeAll() {
        assertThat(SampleProvider.buildValidDtos().count()).as("Not enough valid DTO samples provided").isGreaterThan(1);
        assertThat(SampleProvider.buildInvalidDtos().count()).as("Not enough invalid DTO samples provided").isGreaterThan(1);
    }

    @Nested
    @DisplayName("Query tests")
    class QueryTests {
        @Test
        void query_by_key_with_existing_key() {
            final ScheduledConfigEntry expectedEntry = ScheduledConfigEntry.builder()
                    .id(42)
                    .key("MY_KEY")
                    .validFrom(ZonedDateTime.now().minusHours(1))
                    .value("42")
                    .created(ZonedDateTime.now().minusHours(12))
                    .build();

            // Prepare service response
            when(scheduledConfigService.get(expectedEntry.getKey())).thenReturn(Optional.of(expectedEntry));

            // Execute call
            ResponseEntity<ConfigQueryResponse> responseEntity = underTest.queryByKey(expectedEntry.getKey());

            // Verify usage of service
            verify(scheduledConfigService, times(1)).get(expectedEntry.getKey());

            // ResponseEntity
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

            // ConfigQueryResponse
            ConfigQueryResponse response = responseEntity.getBody();
            assertThat(response).isNotNull();
            assertThat(response.getKey()).isEqualTo(expectedEntry.getKey().toLowerCase(AppConstants.DEFAULT_LOCALE));
            assertThat(response.getValue()).isEqualTo(expectedEntry.getValue());
            assertThat(response.getReferenceTime()).isBetween(ZonedDateTime.now().minusSeconds(1), ZonedDateTime.now());
        }

        @Test
        void query_by_key_with_not_existing_key() {
            final String key = "DOES_NOT_EXIST";

            // Prepare service response
            when(scheduledConfigService.get(any())).thenReturn(Optional.empty());

            // Execute call
            ResponseEntity<ConfigQueryResponse> responseEntity = underTest.queryByKey(key);

            // Verify usage of service
            verify(scheduledConfigService, times(1)).get(key);

            // ResponseEntity
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

            // ConfigQueryResponse
            ConfigQueryResponse response = responseEntity.getBody();
            assertThat(response).isNotNull();
            assertThat(response.getKey()).isEqualTo(key.toLowerCase(AppConstants.DEFAULT_LOCALE));
            assertThat(response.getReferenceTime()).isBetween(ZonedDateTime.now().minusSeconds(1), ZonedDateTime.now());
            assertThat(response.getValue()).isNull();
        }
    }

    @Nested
    @DisplayName("Entry tests")
    class EntryTests {
        @Test
        void pass_paging_information_to_data_layer() {
            final int pageIndex = 12;
            final int pageSize = 34;
            final var argCaptor = ArgumentCaptor.forClass(Pageable.class);

            when(scheduledConfigRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Lists.emptyList()));
            underTest.getScheduledConfigs(pageIndex, pageSize);
            verify(scheduledConfigRepository).findAll(argCaptor.capture());

            assertThat(argCaptor.getValue().getPageNumber())
                    .as("Wrong page number passed to the repository.")
                    .isEqualTo(pageIndex);
            assertThat(argCaptor.getValue().getPageSize())
                    .as("Wrong page size passed to the repository.")
                    .isEqualTo(pageSize);
        }

        @Test
        void get_scheduled_configs_with_empty_database() {
            when(scheduledConfigRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
            ResponseEntity<List<ScheduledConfigDto>> responseEntity = underTest.getScheduledConfigs(12, 34);

            verify(scheduledConfigRepository, times(1)).findAll(any(Pageable.class));

            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(responseEntity.getBody()).isEmpty();
        }

        @Test
        void get_scheduled_configs_with_multiple_records() {
            var samples = SampleProvider.buildValidEntries().collect(Collectors.toUnmodifiableList());
            when(scheduledConfigRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(samples));
            when(scheduledConfigMapper.toDto(any())).thenAnswer((invocation) -> SampleProvider.convertToDto(invocation.getArgument(0), new Random().nextInt(10)));

            ResponseEntity<List<ScheduledConfigDto>> responseEntity = underTest.getScheduledConfigs(0, 100);

            verify(scheduledConfigRepository, times(1)).findAll(any(Pageable.class));
            verify(scheduledConfigMapper, times(samples.size())).toDto(any());

            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(responseEntity.getBody()).isNotEmpty();
        }

        @ParameterizedTest
        @MethodSource("de.mczul.config.model.SampleProvider#buildValidDtos")
        void post_scheduled_config_with_valid_sample(ScheduledConfigDto sample) {
            final int expectedId = new Random().nextInt();
            // Clear ID attribute
            sample = sample.withId(null);
            var entry = SampleProvider.convertToDomain(sample);

            when(scheduledConfigMapper.toDomain(sample)).thenReturn(entry);
            when(scheduledConfigService.set(entry)).thenReturn(entry.withId(expectedId));
            when(scheduledConfigMapper.toDto(entry.withId(expectedId))).thenReturn(sample.withId(expectedId));

            ResponseEntity<ScheduledConfigDto> response = underTest.postScheduledConfig(sample);

            verify(scheduledConfigMapper, times(1)).toDomain(any(ScheduledConfigDto.class));
            verify(scheduledConfigService, times(1)).set(any(ScheduledConfigEntry.class));
            verify(scheduledConfigMapper, times(1)).toDto(any(ScheduledConfigEntry.class));

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(expectedId);
        }
    }
}
