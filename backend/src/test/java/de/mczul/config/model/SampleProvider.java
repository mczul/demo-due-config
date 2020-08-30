package de.mczul.config.model;

import de.mczul.config.service.ScheduledConfigMapper;
import de.mczul.config.service.ScheduledConfigMapperImpl_;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SampleProvider {
    // TODO: Dirty hack to prevent redundant sample definitions; uses the generated delegate of the mapper that depends on Spring beans when mapping entry to DTO
    static ScheduledConfigMapper MAPPER = new ScheduledConfigMapperImpl_();

    public static Stream<ScheduledConfigDto> buildValidDtos() {
        return Stream.of(
                // Ordinary config
                ScheduledConfigDto.builder()
                        .id(23)
                        .key("WITH_ID")
                        .validFrom(ZonedDateTime.now())
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment("ABC")
                        .history(List.of(
                                ScheduledConfigPast.builder()
                                        .id(1)
                                        .key("WITH_ID")
                                        .validFrom(ZonedDateTime.now())
                                        .created(ZonedDateTime.now())
                                        .author("B")
                                        .build()
                        ))
                        .build(),
                // History is empty
                ScheduledConfigDto.builder()
                        .id(42)
                        .key("WITH_EMPTY_HISTORY")
                        .validFrom(ZonedDateTime.now())
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment("ABC")
                        .history(Collections.emptyList())
                        .build(),
                // ID & comment is null
                ScheduledConfigDto.builder()
                        .id(null)
                        .key("ID_IS_NULL")
                        .validFrom(ZonedDateTime.now())
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(null)
                        .history(Collections.emptyList())
                        .build()
        );
    }

    public static Stream<ScheduledConfigDto> buildInvalidDtos() {
        return Stream.of(
                // Key is null
                ScheduledConfigDto.builder()
                        .key(null)
                        .validFrom(ZonedDateTime.now())
                        .value("KEY_IS_NULL")
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(null)
                        .build(),
                // Key is blank
                ScheduledConfigDto.builder()
                        .key("")
                        .validFrom(ZonedDateTime.now())
                        .value("BLANK_KEY_1")
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(null)
                        .history(Collections.emptyList())
                        .build(),
                // Key contains only whitespaces
                ScheduledConfigDto.builder()
                        .key(" ")
                        .validFrom(ZonedDateTime.now())
                        .value("BLANK_KEY_2")
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(null)
                        .history(Collections.emptyList())
                        .build(),
                // ValidFrom is null
                ScheduledConfigDto.builder()
                        .key("VALID_FROM_IS_NULL")
                        .validFrom(null)
                        .value("MY_VALUE_2")
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(null)
                        .history(Collections.emptyList())
                        .build(),
                // Negative id
                ScheduledConfigDto.builder()
                        .id(-1)
                        .key("NEGATIVE_ID")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_2")
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(null)
                        .history(Collections.emptyList())
                        .build(),
                // Blank comment
                ScheduledConfigDto.builder()
                        .key("BLANK_COMMENT")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_2")
                        .created(ZonedDateTime.now())
                        .author("A")
                        .comment(" ")
                        .history(Collections.emptyList())
                        .build(),
                // Created in future
                ScheduledConfigDto.builder()
                        .key("CREATED_IN_FUTURE")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_3")
                        .created(ZonedDateTime.now().plusMinutes(1))
                        .author("A")
                        .comment(null)
                        .history(Collections.emptyList())
                        .build(),
                // Author is null
                ScheduledConfigDto.builder()
                        .key("AUTHOR_IS_NULL")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_3")
                        .created(ZonedDateTime.now().minusMinutes(1))
                        .author(null)
                        .comment(null)
                        .history(Collections.emptyList())
                        .build(),
                // History is null
                ScheduledConfigDto.builder()
                        .key("HISTORY_IS_NULL")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_3")
                        .created(ZonedDateTime.now().minusMinutes(1))
                        .author("A")
                        .comment(null)
                        .history(null)
                        .build()
        );
    }

    public static Stream<ScheduledConfigEntry> buildValidEntries() {
        // Prevent redundant samples; DTOs represent a superset of entry attributes
        return buildValidDtos().map(MAPPER::toDomain);
    }

    public static Stream<ScheduledConfigEntry> buildInvalidEntries() {
        // Prevent redundant samples; DTOs represent a superset of entry attributes
        return buildInvalidDtos().map(MAPPER::toDomain);
    }

}