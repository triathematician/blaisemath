/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.graphics;

import org.blaise.style.StyleUtilsSVG;
import java.awt.Color;
import org.blaise.style.Styles;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elisha
 */
public class StyleUtilsSVGTest {

    /**
     * Test of convertStyleStyleUtilsSVG method, of class StyleUtilsSVG.
     */
    @Test
    public void testToSVG() throws Exception {
        System.out.println("toSVG");
        assertEquals("fill:#ff0000; stroke:#00ff00; stroke-width:1.0", StyleUtilsSVG.convertStyleToSVG(Styles.fillStroke(Color.red, Color.green)));
    }

    /**
     * Test of convertKeyStyleUtilsSVG method, of class StyleUtilsSVG.
     */
    @Test
    public void testToSVGKey() {
        System.out.println("toSVGKey");
        assertEquals("font-size", StyleUtilsSVG.convertKeyToSVG("fontSize"));
    }
}