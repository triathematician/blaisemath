/*
 * SpringLayoutTest.java
 * Created on Jun 3, 2013
 */

package org.blaise.graph.layout;

import org.blaise.graph.GAInstrument;
import org.blaise.graph.Graph;
import org.blaise.graph.modules.EdgeProbabilityBuilder;
import org.blaise.graph.modules.WattsStrogatzBuilder;

/**
 *
 * @author petereb1
 */
public class SpringLayoutTest {

    public static void main(String[] args) {
        StaticSpringLayout sl = StaticSpringLayout.getInstance();

        Graph g1 = new EdgeProbabilityBuilder(true, 300, .1f).createGraph();
        int id = GAInstrument.start("EdgePD", g1+"");
        sl.layout(g1, 10);
        GAInstrument.end(id);

        g1 = new EdgeProbabilityBuilder(false, 300, .1f).createGraph();
        id = GAInstrument.start("EdgePD", g1+"");
        sl.layout(g1, 10);
        GAInstrument.end(id);

        g1 = new WattsStrogatzBuilder(true, 1000, 4, .05f).createGraph();
        id = GAInstrument.start("EdgePD", g1+"");
        sl.layout(g1, 10);
        GAInstrument.end(id);

        GAInstrument.print(System.out);
    }

}
