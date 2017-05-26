package com.googlecode.blaisemath.palette;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.blaisemath.json.AwtJson;
import java.awt.Color;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class GradientPaletteTest {
    
    @Test
    public void testValue() {
        GradientPalette gp = new GradientPalette();
        gp.addStop(GradientPalette.stop(Color.white, 0f));
        gp.addStop(GradientPalette.stop(Color.black, 1f));
        
        assertEquals(new Color(255, 255, 255), gp.color(0f));
        assertEquals(new Color(127, 127, 127), gp.color(.5f));
        assertEquals(new Color(0, 0, 0), gp.color(1f));
        
        GradientPalette gp2 = new GradientPalette();
        gp2.addStop(GradientPalette.stop(Color.yellow, 0.5f));
        assertEquals(Color.yellow, gp2.color(0f));
        assertEquals(Color.yellow, gp2.color(.2f));
        assertEquals(Color.yellow, gp2.color(.5f));
        assertEquals(Color.yellow, gp2.color(1f));
        
        GradientPalette gp3 = new GradientPalette();
        assertEquals(null, gp3.color(0f));
        
        try {
            assertEquals(Color.yellow, gp.color(2f));
            fail("Exception expected");
        } catch (IllegalArgumentException x) {
            // expected
        }
    }

    @Test
    public void testSerialize() throws JsonProcessingException, IOException {
        GradientPalette gp = new GradientPalette();
        gp.setName("Simple Gradient");
        gp.addStop(GradientPalette.stop(Color.white, 0f));
        gp.addStop(GradientPalette.stop(Color.black, 1f));
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(AwtJson.module());
        assertEquals("{\"name\":\"Simple Gradient\",\"stops\":[{\"color\":\"#ffffff\",\"stop\":0.0},{\"color\":\"#000000\",\"stop\":1.0}]}", mapper.writeValueAsString(gp));
        mapper.writeValue(System.out, gp);
    }

    @Test
    public void testDeserialize() throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(AwtJson.module());
        GradientPalette pal = mapper.readValue("{\"name\":\"Simple Gradient\",\"stops\":[{\"color\":\"#000000\",\"stop\":1.0},{\"color\":\"#ffffff\",\"stop\":0.0}]}", GradientPalette.class);
        
        assertEquals("{\"name\":\"Simple Gradient\",\"stops\":[{\"color\":\"#ffffff\",\"stop\":0.0},{\"color\":\"#000000\",\"stop\":1.0}]}", mapper.writeValueAsString(pal));
        
    }
    
}
