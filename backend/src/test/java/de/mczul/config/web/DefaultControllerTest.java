package de.mczul.config.web;

import de.mczul.config.model.ConfigQueryResponse;
import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.service.ScheduledConfigRepository;
import de.mczul.config.service.ScheduledConfigService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@DisplayName("DefaultController unit tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
public class DefaultControllerTest {
    @Mock
    private ScheduledConfigRepository scheduledConfigRepository;
    @Mock
    private ScheduledConfigService scheduledConfigService;

    @InjectMocks
    private DefaultController underTest;

    static Stream<ScheduledConfigDto> buildValidDto() {
        return Stream.empty();
    }

    static Stream<ScheduledConfigDto> buildInvalidDto() {
        return Stream.empty();
    }

    @BeforeAll
    static void beforeAll() {
        assertThat(buildValidDto().count()).as("Not enough valid DTO samples provided").isGreaterThan(1);
        assertThat(buildInvalidDto().count()).as("Not enough invalid DTO samples provided").isGreaterThan(1);
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
            assertThat(response.getKey()).isEqualTo(expectedEntry.getKey().toLowerCase());
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
            assertThat(response.getKey()).isEqualTo(key.toLowerCase());
            assertThat(response.getReferenceTime()).isBetween(ZonedDateTime.now().minusSeconds(1), ZonedDateTime.now());
            assertThat(response.getValue()).isNull();
        }
    }

    @Nested
    @DisplayName("Entry tests")
    class EntryTests {
        @Test
        void get_scheduled_configs_with_empty_database() {
            fail("Not yet implemented!");
        }

        @Test
        void get_scheduled_configs_with_multiple_records() {
            fail("Not yet implemented!");
        }

        @ParameterizedTest
        @MethodSource("de.mczul.config.web.DefaultControllerTest#buildValidDto")
        void post_scheduled_config_with_valid_sample() {
            fail("Not yet implemented!");
        }

        @ParameterizedTest
        @MethodSource("de.mczul.config.web.DefaultControllerTest#buildInvalidDto")
        void post_scheduled_config_with_invalid_sample() {
            fail("Not yet implemented!");
        }
    }
}
