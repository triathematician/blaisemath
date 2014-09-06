/*
 * SpringLayoutTest.java
 * Created on Jun 3, 2013
 */

package com.googlecode.blaisemath.graph.testui;

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

import com.googlecode.blaisemath.graph.modules.layout.StaticSpringLayout;
import com.googlecode.blaisemath.graph.GAInstrument;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.modules.suppliers.EdgeProbabilityGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.WattsStrogatzGraphSupplier;

/**
 *
 * @author petereb1
 */
public class SpringLayoutTestMain {

    public static void main(String[] args) {
        StaticSpringLayout sl = StaticSpringLayout.getInstance();

        Graph g1 = new EdgeProbabilityGraphSupplier(true, 300, .1f).get();
        int id = GAInstrument.start("EdgePD", g1+"");
        sl.layout(g1, 10);
        GAInstrument.end(id);

        g1 = new EdgeProbabilityGraphSupplier(false, 300, .1f).get();
        id = GAInstrument.start("EdgePD", g1+"");
        sl.layout(g1, 10);
        GAInstrument.end(id);

        g1 = new WattsStrogatzGraphSupplier(true, 1000, 4, .05f).get();
        id = GAInstrument.start("EdgePD", g1+"");
        sl.layout(g1, 10);
        GAInstrument.end(id);

        GAInstrument.print(System.out);
    }

}
