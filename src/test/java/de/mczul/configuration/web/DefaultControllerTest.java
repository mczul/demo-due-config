package de.mczul.configuration.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mczul.configuration.common.ScheduledConfigEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DefaultControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> buildQueryParams() {
        return Stream.of(
                arguments(0, 10),
                arguments(0, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("buildQueryParams")
    void testGet(int pageIndex, int pageSize) throws Exception {
        MvcResult result = mockMvc
                .perform(
                        get("/api/default/v1")
                                .param(RestConstants.REQUEST_PARAM_PAGE_INDEX, String.valueOf(pageIndex))
                                .param(RestConstants.REQUEST_PARAM_PAGE_SIZE, String.valueOf(pageSize))
                )
                .andExpect(status().isOk())
                .andReturn();

        byte[] content = result.getResponse().getContentAsByteArray();
        ScheduledConfigEntry[] dtoArray = objectMapper.readValue(content, ScheduledConfigEntry[].class);
        assertThat(dtoArray).hasSizeLessThanOrEqualTo(pageSize);
    }

}