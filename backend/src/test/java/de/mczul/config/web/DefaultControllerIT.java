package de.mczul.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mczul.config.model.ConfigQueryResponse;
import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.service.ScheduledConfigMapper;
import de.mczul.config.service.ScheduledConfigRepository;
import de.mczul.config.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DefaultController tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
@AutoConfigureMockMvc
class DefaultControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ScheduledConfigMapper scheduledConfigMapper;
    @MockBean
    private ScheduledConfigRepository scheduledConfigRepository;

    static Stream<Arguments> buildGetScheduledConfigsArgs() {
        return Stream.of(
                arguments(0, 10, List.of(
                        ScheduledConfigEntry.builder().id(1).key("KEY_A").validFrom(LocalDateTime.now()).value("23").build(),
                        ScheduledConfigEntry.builder().id(2).key("KEY_A").validFrom(LocalDateTime.now().plusDays(1)).value("42").build(),
                        ScheduledConfigEntry.builder().id(3).key("KEY_B").validFrom(LocalDateTime.now()).value("4711").build()
                        )
                ),
                arguments(1, 1, List.of())
        );
    }

    @Nested
    @DisplayName("Value query tests")
    class ValueQueryTests {

        private void checkNullValueQueryResponse(String key) throws Exception {
            final MvcResult result = mockMvc
                    .perform(get(RestConstants.PATH_PREFIX_API + "/" + key))
                    .andExpect(status().isOk())
                    .andReturn();

            final byte[] content = result.getResponse().getContentAsByteArray();
            final ConfigQueryResponse response = objectMapper.readValue(content, ConfigQueryResponse.class);

            assertThat(response).as("Query response was NULL").isNotNull();
            assertThat(response.getReferenceTime()).as("The reference timestamp of the query response is invalid").isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(response.getValue()).as("The query response a value other than NULL").isNull();
        }

        @Test
        void handle_query_by_key_with_key_not_existing() throws Exception {
            final String key = "NOT_EXISTING";
            when(scheduledConfigRepository.findCurrentByKey(key)).thenReturn(Optional.empty());
            checkNullValueQueryResponse(key);
        }

        @Test
        void handle_query_by_key_with_null_value_entry() throws Exception {
            final String key = "KEY_WITH_NULL_VALUE";
            when(scheduledConfigRepository.findCurrentByKey(key)).thenReturn(
                    Optional.of(ScheduledConfigEntry.builder()
                            .key(key)
                            .validFrom(LocalDateTime.now().minusDays(1))
                            .value(null)
                            .build())
            );
            checkNullValueQueryResponse(key);
        }
    }

    @Nested
    @DisplayName("Entry tests")
    class EntryTests {

        @ParameterizedTest
        @MethodSource("de.mczul.config.web.DefaultControllerIT#buildGetScheduledConfigsArgs")
        void must_translate_query_spec_to_repository_params(int pageIndex, int pageSize, List<ScheduledConfigEntry> expectedEntries) throws Exception {
        /*
         TODO: Current test might be to close to actual implementation: find a way to test relevant aspects (as e.g.
               correct interpretation of intended query and type mapping) without mirroring the implementation details
        */
            final List<ScheduledConfigDto> expectedDtos = scheduledConfigMapper.fromDomainList(expectedEntries);
            when(scheduledConfigRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(expectedEntries));

            final MvcResult result = mockMvc
                    .perform(
                            get(RestConstants.PATH_PREFIX_API)
                                    .param(RestConstants.QUERY_PARAM_PAGE_INDEX, String.valueOf(pageIndex))
                                    .param(RestConstants.QUERY_PARAM_PAGE_SIZE, String.valueOf(pageSize))
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            verify(scheduledConfigRepository).findAll(PageRequest.of(pageIndex, pageSize, Sort.by("key", "validFrom")));
            final byte[] content = result.getResponse().getContentAsByteArray();
            final ScheduledConfigDto[] dtoArray = objectMapper.readValue(content, ScheduledConfigDto[].class);

            assertThat(dtoArray).as("More records returned than page size suggests").hasSizeLessThanOrEqualTo(pageSize);
            assertThat(dtoArray).as("Returned records did not match expected list").containsExactlyInAnyOrder(expectedDtos.toArray(ScheduledConfigDto[]::new));
        }

        @Test
        void must_save_valid_samples() throws Exception {
            final ScheduledConfigDto sample = ScheduledConfigDto.builder()
                    .key("FOO")
                    .validFrom(LocalDateTime.now().minusMinutes(1))
                    .value("BAR")
                    .build();
            final ScheduledConfigEntry expectedEntry = scheduledConfigMapper.toDomain(sample.withId(42));

            when(scheduledConfigRepository.save(any(ScheduledConfigEntry.class))).thenReturn(expectedEntry);
            final byte[] content = objectMapper.writeValueAsBytes(sample);
            final MvcResult result = mockMvc.perform(
                    post(RestConstants.PATH_PREFIX_API)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(content)
            )
                    .andExpect(status().isCreated())
                    .andReturn();

            final String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).as("Saving valid sample must return some content").isNotBlank();

            final ScheduledConfigDto actual = objectMapper.readValue(responseContent, ScheduledConfigDto.class);
            assertAll(
                    () -> assertThat(actual.getId()).as("Entry must have a non null id after it has been saved").isNotNull(),
                    () -> assertThat(actual.getKey()).as("Entry key must be unmodified after save operation").isEqualTo(sample.getKey()),
                    () -> assertThat(actual.getValidFrom()).as("Entry valid from timestamp must be unmodified after save operation").isEqualTo(sample.getValidFrom()),
                    () -> assertThat(actual.getValue()).as("Entry value must be unmodified after save operation").isEqualTo(sample.getValue())
            );
        }

        @Test
        void must_handle_invalid_samples_properly() throws Exception {
            final ScheduledConfigDto sample = ScheduledConfigDto.builder()
                    .key("")
                    .validFrom(LocalDateTime.now())
                    .value(null)
                    .build();

            final byte[] content = objectMapper.writeValueAsBytes(sample);
            final MvcResult result = mockMvc.perform(
                    post(RestConstants.PATH_PREFIX_API)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(content)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("Content-Type", MediaType.TEXT_PLAIN_VALUE))
                    .andReturn();

            assertThat(result.getResponse().getContentAsString()).as("No content returned after posting invalid config dto").isNotBlank();
        }

    }

}
