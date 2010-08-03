/*
 * PlaneGraphBean.java
 * Created Jul 19, 2010
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import primitive.style.AbstractPointStyle.PointShape;
import primitive.style.StringStyle;
import util.DefaultChangeBroadcaster;

/**
 * A wrapper used by a <code>PlaneGraph</code> to encode the most desirable
 * settings, useful for customizing appearance in a settings panel.
 * @author Elisha Peterson
 */
public class PlaneGraphBean extends DefaultChangeBroadcaster {

    PlaneGraph graph;
    Node nb;
    Edge eb;
    Label lb;
    
    public PlaneGraphBean(final PlaneGraph graph) {
        this.graph = graph;
        changeEvent = new ChangeEvent(this);
        addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                graph.getParent().plottableStyleChanged(graph);
            }
        });
        nb = new Node();
        eb = new Edge();
        lb = new Label();
    }

    /** @return instance of a node bean */
    public Node nodeBean() { return nb; }
    /** @return instance of an edge bean */
    public Edge edgeBean() { return eb; }
    /** @return instance of a label bean */
    public Label labelBean() { return lb; }

    /** Class encodding node visual properties */
    public class Node {
        public boolean isVisible() { return graph.isPointsVisible(); }
        public void setVisible(boolean visible) { graph.setPointsVisible(visible); fireStateChanged(); }

        public Point2D.Double[] getPositions() { return graph.getPoint(); }
        public Point2D.Double getPositions(int i) { return graph.getPoint(i); }
        public void setPositions(Point2D.Double[] loc) { graph.setPoint(loc); fireStateChanged(); }
        public void setPositions(int i, Point2D.Double newValue) { graph.setPoint(i, newValue); fireStateChanged(); }

        public PointShape getShape() { return graph.getPointStyle().getShape(); }
        public void setShape(PointShape shape) { graph.getPointStyle().setShape(shape); fireStateChanged(); }
        public double getSize() { return graph.getPointStyle().getMaxRadius(); }
        public void setSize(double size) { graph.getPointStyle().setMaxRadius(size); fireStateChanged(); }
        public Color getFill() { return graph.getPointStyle().getFillColor(); }
        public void setFill(Color color) { graph.getPointStyle().setFillColor(color); fireStateChanged(); }
        public Color getStroke() { return graph.getPointStyle().getStrokeColor(); }
        public void setStroke(Color color) { graph.getPointStyle().setStrokeColor(color); fireStateChanged(); }
    }

    /** Class encoding node label properties */
    public class Label {
        public boolean isVisible() { return graph.getPointStyle().isLabelVisible(); }
        public void setVisible(boolean visible) { graph.getPointStyle().setLabelVisible(visible); fireStateChanged(); }

        public Font getFont() { return graph.getPointStyle().getLabelFont(); }
        public void setFont(Font font) { graph.getPointStyle().setLabelFont(font); fireStateChanged(); }
        public Color getColor() { return graph.getPointStyle().getLabelColor(); }
        public void setColor(Color color) { graph.getPointStyle().setLabelColor(color); fireStateChanged(); }
        public StringStyle.Anchor getAnchor() { return graph.getPointStyle().getLabelAnchor(); }
        public void setAnchor(StringStyle.Anchor anchor) { graph.getPointStyle().setLabelAnchor(anchor); fireStateChanged(); }
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
