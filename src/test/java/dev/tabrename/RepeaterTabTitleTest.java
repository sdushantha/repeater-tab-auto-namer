package dev.tabrename;

import burp.api.montoya.http.message.requests.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepeaterTabTitleTest {
    @Test
    void createsTitleFromMethodAndPathWithoutQuery() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.method()).thenReturn("POST");
        when(request.pathWithoutQuery()).thenReturn("/api/users/42");

        assertEquals("POST /api/users/42", RepeaterTabTitle.from(request));
    }

    @Test
    void normalizesMethodAndMissingLeadingSlash() {
        assertEquals("PATCH /api/users", RepeaterTabTitle.from(" patch ", "api/users"));
    }

    @Test
    void supportsOptionsAsteriskForm() {
        assertEquals("OPTIONS *", RepeaterTabTitle.from("OPTIONS", "*"));
    }

    @Test
    void suppliesSafeDefaultsForMissingParts() {
        assertEquals("REQUEST /", RepeaterTabTitle.from(" ", null));
    }

    @Test
    void removesControlCharactersFromTabTitles() {
        assertEquals("GET /firstsecond", RepeaterTabTitle.from("G\nET", "/first\r\nsecond"));
    }
}
