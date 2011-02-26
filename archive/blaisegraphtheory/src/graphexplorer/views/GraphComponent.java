/*
 * GraphComponent.java
 * Created Dec 27, 2010
 */

package graphexplorer.views;

import graphexplorer.controller.GraphController;
import graphexplorer.controller.GraphDecorController;
import graphexplorer.controller.GraphLayoutController;
import graphexplorer.controller.GraphStatController;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.specto.plane.graph.AbstractPlaneGraph;
import org.bm.blaise.specto.plane.graph.ImageGraph;
import org.bm.blaise.specto.plane.graph.PlaneGraph;
import visometry.plane.PlanePlotComponent;

/**
 * Displays a graph on the plane; provides interface between <code>PlaneGraph</code>
 * or <code>ImageGraph</code> (both instances of <code>AbstractPlaneGraph</code>)
 * and the controller classes: <code>GraphDecorController</code> for node decorations,
 * including varied sizes and labels; <code>GraphLayoutController</code> for node positions.
 * @author Elisha
 */
public class GraphComponent extends PlanePlotComponent
        implements PropertyChangeListener {

    /** Manages the decorations, varied node sizes, etc. on the graph */
    GraphController controller;
    /** The actual graph that will be drawn */
    AbstractPlaneGraph graph;

    /** Construct component with specified controller */
    public GraphComponent(GraphController gc) {
        setController(controller);
    }

    /** Sets the base controller */
    public final void setController(GraphController gc) {
        if (gc != controller) {
            if (controller != null)
                controller.removePropertyChangeListener(this);
            controller = gc;
            if (controller != null)
                controller.addPropertyChangeListener(this);
            initPlaneGraph();
        }
    }

    void initPlaneGraph() {
        Graph ag = controller.getViewGraph();

        // check to see if graph contains images (ad-hoc)
        boolean imageGraph = false;
        List nodes = ag.nodes();
        if (ag instanceof ValuedGraph && nodes.size() > 0) {
            ValuedGraph vg = (ValuedGraph) ag;
            Object o1 = vg.getValue(nodes.get(0));
            if (o1.getClass().equals(Object[].class)) {
                Object[] o2 = (Object[]) o1;
                if (o2.length >= 2 && Image.class.isAssignableFrom(o2[1].getClass()))
                    imageGraph = true;
            }
        }
        if (imageGraph)
            add(graph = new ImageGraph(ag));
        else 
            add(graph = new PlaneGraph(ag));

        graph.highlightNodes(controller.getHighlightNodes());
        graph.setPositionMap(controller.getNodePositions());
        updateNodeSizes(controller.getMetricValues());
        graph.updateLabels();
        graph.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                controller.setNodePositions(graph.getPositionMap());
            }
        });
        repaint();
    }

    void updateNodeSizes(List values) {
        graph.setNodeValues(values);
        if (graph instanceof PlaneGraph) {
            if (values == null) {
                ((PlaneGraph)graph).getPointStyle().setRadiusMultiplier(1.0);
            } else {
                double max = 0;
                for(int i = 0; i < values.size(); i++)
                    max = Math.max(max, ((Number) values.get(i)).doubleValue());
                ((PlaneGraph)graph).getPointStyle().setRadiusMultiplier(1.0/Math.sqrt(max));
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals(GraphDecorController.$HIGHLIGHT)) {
            graph.highlightNodes(controller.getHighlightNodes());
        } else if (prop.equals(GraphDecorController.$BASE) || prop.equals(GraphDecorController.$DECOR))
            graph.requestComputation();
        else if (prop.equals(GraphLayoutController.$POSITIONS))
            graph.setPositionMap((Map<?, Point2D.Double>) evt.getNewValue());
        else if (prop.equals(GraphStatController.$VALUES)) {
            // TODO this should be updated from the decor controller, not directly from here
            updateNodeSizes((List) evt.getNewValue());
        }
    }

}
