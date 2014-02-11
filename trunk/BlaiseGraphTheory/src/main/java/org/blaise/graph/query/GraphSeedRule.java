/*
 * GraphSeedRule.java
 * Created on Jul 3, 2012
 */
package org.blaise.graph.query;

import com.google.common.base.Function;
import java.util.Set;
import org.blaise.graph.Graph;

/**
 * A "seed rule" to select a (small) portion of a (large) graph for analysis.
 *
 * @author Elisha Peterson
 */
public interface GraphSeedRule extends Function<Graph,Set> {

    /**
     * Name of rule for display
     * @return name
     */
    String getName();

}
