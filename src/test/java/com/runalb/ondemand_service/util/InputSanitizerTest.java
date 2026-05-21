package com.runalb.ondemand_service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class InputSanitizerTest {

    @Test
    void normalizeSearchQuery_returnsNullForBlank() {
        assertNull(InputSanitizer.normalizeSearchQuery(null));
        assertNull(InputSanitizer.normalizeSearchQuery(""));
        assertNull(InputSanitizer.normalizeSearchQuery("   "));
    }

    @Test
    void normalizeSearchQuery_trimsAndCollapsesWhitespace() {
        assertEquals("hair cut", InputSanitizer.normalizeSearchQuery("  hair   cut  "));
    }
}
