package com.googlecode.blaisemath.graph.generate;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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

import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CycleGraphGeneratorTest {

    @Test
    public void testGetEmptyGraphInstance() {
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1, 3] 1: [0, 2] 2: [1, 3] 3: [0, 2]",
                GraphUtils.printGraph(new CycleGraphGenerator().apply(new DefaultGeneratorParameters(false,4))));
        assertEquals("NODES: [0, 1, 2, 3]  EDGES: 0: [1] 1: [2] 2: [3] 3: [0]",
                GraphUtils.printGraph(new CycleGraphGenerator().apply(new DefaultGeneratorParameters(true,4))));
    }

}
