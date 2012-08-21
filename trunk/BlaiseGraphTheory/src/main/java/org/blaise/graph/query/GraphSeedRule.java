/*
 * GraphSeedRule.java
 * Created on Jul 3, 2012
 */
package org.blaise.graph.query;

import org.blaise.graph.GraphNodeFilter;

/**
 * A "seed rule" to select a (small) portion of a (large) graph for analysis.
 *
 * @author Elisha Peterson
 */
public interface GraphSeedRule extends GraphNodeFilter {

    /**
     * Name of rule for display
     * @return name
     */
    public String getName();

}
