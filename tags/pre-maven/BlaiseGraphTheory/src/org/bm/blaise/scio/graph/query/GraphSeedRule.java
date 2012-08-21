/*
 * GraphSeedRule.java
 * Created on Jul 3, 2012
 */
package org.bm.blaise.scio.graph.query;

/**
 * A "seed rule" to select a (small) portion of a (large) graph for analysis.
 *
 * @author Elisha Peterson
 */
public interface GraphSeedRule extends GraphQuery {

    /**
     * Name of rule for display
     * @return name
     */
    public String getName();

}
