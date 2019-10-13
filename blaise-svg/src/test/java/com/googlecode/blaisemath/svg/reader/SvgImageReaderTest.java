package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.primitive.AnchoredImage;
import com.googlecode.blaisemath.svg.xml.SvgImage;
import org.junit.Test;

import java.net.MalformedURLException;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.*;

public class SvgImageReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgImageReader().createPrimitive(null));
        AnchoredImage image = new SvgImageReader().createPrimitive(SvgImage.create(1, 2, "file:image.png"));
        assertEquals(1, image.x, .01);
        assertEquals(2, image.y, .01);
        assertEquals("file:image.png", image.getReference());
    }

//    @Test
//    public void testLoadImage() throws MalformedURLException {
//        System.out.println("loadImage");
//        SvgImage i = new SvgImage();
//        assertTrue(i.getImage() == null);
//
//        i.href = "cherries.png";
//        assertTrue(i.getImage() == null);
//
//        i.href = "file:cherries.png";
//        assertTrue(i.getImage() == null);
//
//        i.href = "file:src/test/resources/com/googlecode/blaisemath/svg/resources/cherries.png";
//        assertTrue(i.getImage() != null);
//        assertEquals(20.0, i.getWidth());
//        assertEquals(20.0, i.getHeight());
//    }

}