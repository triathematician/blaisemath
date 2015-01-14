/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.googlecode.blaisemath.graph.modules.metrics.GraphMetrics;
import java.util.Arrays;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class GraphMetricsTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- GraphMetricsTest --");
    }

    @Test
    public void testDistribution() {
        System.out.println("distribution");
        Map result1 = GraphMetrics.distribution(Arrays.asList(0,3,45,56,5,1,2,4,45,5,21,3,3,2,1,1,2,34,4,4,3,2,2));
        assertEquals("{0=1, 1=3, 2=5, 3=4, 4=3, 5=2, 21=1, 34=1, 45=2, 56=1}", result1.toString());
    }

}
