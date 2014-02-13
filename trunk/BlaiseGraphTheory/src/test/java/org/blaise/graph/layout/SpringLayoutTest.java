/*
 * SpringLayoutTest.java
 * Created on Jun 3, 2013
 */

package org.blaise.graph.layout;

import org.blaise.graph.GAInstrument;
import org.blaise.graph.Graph;
import org.blaise.graph.modules.EdgeProbabilityGraphSupplier;
import org.blaise.graph.modules.WattsStrogatzGraphSupplier;

/**
 *
 * @author petereb1
 */
public class SpringLayoutTest {

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
