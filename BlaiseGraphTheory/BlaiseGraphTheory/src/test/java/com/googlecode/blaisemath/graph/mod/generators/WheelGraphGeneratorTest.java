/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.googlecode.blaisemath.graph.mod.generators.WheelGraphGenerator;
import com.googlecode.blaisemath.graph.mod.generators.DefaultGeneratorParameters;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class WheelGraphGeneratorTest {

    @Test
    public void testGetWheelGraphGeneratorInstance() {
        System.out.println("getWheelGraphGeneratorInstance");
        assertEquals("NODES: [0, 1, 2, 3, 4]  EDGES: 0: [1, 2, 3, 4] 1: [0, 2, 4] 2: [0, 1, 3] 3: [0, 2, 4] 4: [0, 1, 3]", 
                GraphUtils.printGraph(new WheelGraphGenerator().generate(new DefaultGeneratorParameters(false,5))));
        assertEquals("NODES: [0, 1, 2, 3, 4]  EDGES: 0: [1, 2, 3, 4] 1: [2, 4] 2: [1, 3] 3: [2, 4] 4: [1, 3]", 
                GraphUtils.printGraph(new WheelGraphGenerator().generate(new DefaultGeneratorParameters(true,5))));
    }

}
