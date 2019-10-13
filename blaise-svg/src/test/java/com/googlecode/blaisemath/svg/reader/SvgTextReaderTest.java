package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.svg.xml.SvgText;
import org.junit.Test;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.assertEquals;

public class SvgTextReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgTextReader().createPrimitive(null));
        AnchoredText at = new SvgTextReader().createPrimitive(SvgText.create(1, 2, "text"));
        assertEquals(1, at.x, .01f);
        assertEquals(2, at.y, .01f);
        assertEquals("text", at.getText());
    }

}