package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    @Test
    void whenParcingDateIsOk() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime time = parser.parse("2023-06-01T17:27:13+03:00");
        String rsl = time.toString();
        assertThat(rsl).isEqualTo("2023-06-01T17:27:13");
    }
}