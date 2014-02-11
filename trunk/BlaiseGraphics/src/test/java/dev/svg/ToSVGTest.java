/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.svg;

import java.awt.Color;
import org.blaise.style.Styles;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class ToSVGTest {

    /**
     * Test of toSVG method, of class ToSVG.
     */
    @Test
    public void testToSVG() throws Exception {
        System.out.println("toSVG");
        assertEquals("fill:#ff0000; stroke:#00ff00; stroke-width:1.0", ToSVG.toSVG(Styles.fillStroke(Color.red, Color.green)));
    }

    /**
     * Test of toSVGKey method, of class ToSVG.
     */
    @Test
    public void testToSVGKey() {
        System.out.println("toSVGKey");
        assertEquals("font-size", ToSVG.toSVGKey("fontSize"));
    }
}