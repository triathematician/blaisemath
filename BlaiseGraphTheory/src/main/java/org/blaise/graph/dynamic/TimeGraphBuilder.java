/**
 * TimeGraphBuilder.java
 * Created Aug 18, 2012
 */
package org.blaise.graph.dynamic;

/**
 * <p>
 *  A generic object that returns a graph. The creation of the graph might depend
 *  on various parameters that have been applied to the builder.
 * </p>
 * @author elisha
 */
public interface TimeGraphBuilder<V> {

    /**
     * Instantiate and return a graph using the builder's parameters.
     * @return graph
     */
    TimeGraph<V> createTimeGraph();
    
    
    /**
     * Builds graphs with requisite number of nodes
     */
    public static abstract class Support<V> implements TimeGraphBuilder<V> {
        protected boolean directed = false;
        protected int nodes = 1;

        public Support() {
        }
        
        public Support(boolean directed, int nodes) {
            if (nodes < 0) {
                throw new IllegalArgumentException("Graphs must have a non-negative number of nodes: " + nodes);
            }
            this.directed = directed;
            this.nodes = nodes;
        }        

        public boolean isDirected() {
            return directed;
        }

        public void setDirected(boolean directed) {
            this.directed = directed;
        }

        public int getNodes() {
            return nodes;
        }

        public void setNodes(int nodes) {
            this.nodes = nodes;
        }
    } // INNER CLASS TimeGraphBuilder.Support
    
}
