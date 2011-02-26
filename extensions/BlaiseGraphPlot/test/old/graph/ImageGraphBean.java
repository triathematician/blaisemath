/*
 * ImageGraphBean.java
 * Created Aug 11, 2010
 */

package old.graph;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import primitive.style.Anchor;
import util.DefaultChangeBroadcaster;

/**
 * A wrapper used by a <code>ImageGraph</code> to encode the most desirable
 * settings, useful for customizing appearance in a settings panel.
 * @author Elisha Peterson
 */
public class ImageGraphBean extends DefaultChangeBroadcaster {

    ImageGraph graph;
    Node nb;
    Edge eb;
    
    public ImageGraphBean(final ImageGraph graph) {
        if (graph == null)
            throw new IllegalArgumentException("Cannot construct this bean with null graph!");
        this.graph = graph;
        changeEvent = new ChangeEvent(this);
        addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                graph.getParent().plottableStyleChanged(graph);
            }
        });
        nb = new Node();
        eb = new Edge();
    }

    /** @return instance of a node bean */
    public Node nodeBean() { return nb; }
    /** @return instance of an edge bean */
    public Edge edgeBean() { return eb; }

    /** Class encodding node visual properties */
    public class Node {
        public boolean isVisible() { return graph.isPointsVisible(); }
        public void setVisible(boolean visible) { graph.setPointsVisible(visible); fireStateChanged(); }

        public float getScale() { return graph.getImageStyle().getScale(); }
        public void setScale(float scale) { graph.getImageStyle().setScale(scale); fireStateChanged(); }
        public int getMaxDimension() { return graph.getImageStyle().getMaxWidth(); }
        public void setMaxDimension(int dim) { graph.getImageStyle().setMaxWidth(dim); graph.getImageStyle().setMaxHeight(dim); fireStateChanged(); }
        public Color getBorderColor() { return graph.getImageStyle().getBorderColor(); }
        public void setBorderColor(Color color) { graph.getImageStyle().setBorderColor(color); fireStateChanged(); }
        public Anchor getAnchor() { return graph.getImageStyle().getAnchor(); }
        public void setAnchor(Anchor anchor) { graph.getImageStyle().setAnchor(anchor); fireStateChanged(); }
    }

    /** Class encoding edge visual properties */
    public class Edge {
        public boolean isVisible() { return graph.isEdgesVisible(); }
        public void setVisible(boolean visible) { graph.setEdgesVisible(visible); fireStateChanged(); }

        public Color getColor() { return graph.getEdgeStyle().getStrokeColor(); }
        public void setColor(Color color) { graph.getEdgeStyle().setStrokeColor(color); fireStateChanged(); }
        public float getThickness() { return graph.getEdgeStyle().getThickness(); }
        public void setThickness(float value) { graph.getEdgeStyle().setThickness(value); fireStateChanged(); }

        public int getArrowSize() { return graph.getEdgeStyle().getShapeSize(); }
        public void setArrowSize(int size) { graph.getEdgeStyle().setShapeSize(size); fireStateChanged(); }
    }

}
