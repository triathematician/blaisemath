package com.googlecode.blaisemath.svg.reader;

import static org.junit.Assert.fail;

class SvgReaderTest {

    static void assertSvgReadException(Runnable r) {
        try {
            r.run();
            fail("Expected a read exception");
        } catch (SvgReadException x) {
            // expected
        }
    }

}
