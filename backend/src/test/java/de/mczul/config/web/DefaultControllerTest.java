package de.mczul.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mczul.config.AppConstants;
import de.mczul.config.common.ScheduledConfigDto;
import de.mczul.config.common.ScheduledConfigEntry;
import de.mczul.config.common.ScheduledConfigMapper;
import de.mczul.config.common.ScheduledConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles({AppConstants.PROFILES_TEST})
class DefaultControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ScheduledConfigMapper scheduledConfigMapper;
    @MockBean
    private ScheduledConfigRepository scheduledConfigRepository;

    static Stream<Arguments> buildGetQueryArgs() {
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

    @Autowired
    Environment env;

    @ParameterizedTest
    @MethodSource("buildGetQueryArgs")
    void testGet(int pageIndex, int pageSize, List<ScheduledConfigEntry> expectedEntries) throws Exception {
        LOG.info(Arrays.deepToString(env.getActiveProfiles()));
        List<ScheduledConfigDto> expectedDtos = scheduledConfigMapper.fromDomainList(expectedEntries);
        when(scheduledConfigRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(expectedEntries));

        MvcResult result = mockMvc
                .perform(
                        get(RestConstants.PATH_PREFIX_API)
                                .param(RestConstants.QUERY_PARAM_PAGE_INDEX, String.valueOf(pageIndex))
                                .param(RestConstants.QUERY_PARAM_PAGE_SIZE, String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andReturn();

        verify(scheduledConfigRepository).findAll(PageRequest.of(pageIndex, pageSize, Sort.by("key", "validFrom")));
        byte[] content = result.getResponse().getContentAsByteArray();
        ScheduledConfigDto[] dtoArray = objectMapper.readValue(content, ScheduledConfigDto[].class);

        assertThat(dtoArray).hasSizeLessThanOrEqualTo(pageSize);
        assertThat(dtoArray).containsExactlyInAnyOrder(expectedDtos.toArray(ScheduledConfigDto[]::new));
    }

}