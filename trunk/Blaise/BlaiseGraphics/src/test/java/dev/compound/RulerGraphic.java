/*
 * RulerGraphic.java
 * Created Sep 21, 2011
 */
package dev.compound;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.blaise.graphics.BasicShapeGraphic;
import org.blaise.graphics.BasicTextGraphic;
import org.blaise.graphics.GraphicComposite;
import org.blaise.style.PointStyleBasic;
import org.blaise.style.Markers;
import org.blaise.style.ShapeStyle;
import org.blaise.style.StyleHints;

/**
 * This primitive is designed for objects such as a plot's axes, which display a line
 * together with several "tick marks" that label the line. The elements of the primitive
 * include the line's location (as two points), the locations of each tick mark (as doubles = distances from start),
 * labels associated with each tick mark (as an array, may be null), and a global line label (may be null).
 * 
 * @author elisha
 */
public final class RulerGraphic extends TwoPointGraphicSupport {
    
    /** Entry with the line */
    private BasicShapeGraphic lineEntry;
    
    /** Entry with the ticks */
    private BasicShapeGraphic ticksEntry;
    /** Entry with the tick labels */
    private GraphicComposite labelEntry;
    
    /** Tick mark positions */
    private float[] ticks;
    /** Tick labels */
    private String[] labels;
    

    /** Pixel length of tick mark (to the left of the line). */
    int ruleLeft = 7;
    /** Pixel length of tick mark (to the right of the line). */
    int ruleRight = -7;
    /** Label position, as a percentage of the tick position (0 is right, 1 is leftmost) */
    float labelPos = 0;

    /** Reverses the order of left & right, in effect drawing everything on the opposite side of the line from where it would be otherwise. */
    boolean mirrored = false;
    
    /** Whether ticks are shown on the plot. */
    private boolean rulesVisible = true;
    /** Whether tick labels are shown on the plot. */
    private boolean labelsVisible = true;

    
    //
    // CONSTRUCTORS
    //

    /** Construct arrow between specified points */
    public RulerGraphic(Point2D start, Point2D end) {
        super(start, end);
    }
    
    @Override
    public void initGraphics() {
        // ensure line is added before points
        addGraphic(lineEntry = new BasicShapeGraphic(new GeneralPath(), true));        
        super.initGraphics();
        addGraphic(ticksEntry = new BasicShapeGraphic(null, true));
        addGraphic(labelEntry = new GraphicComposite());
        
        PointStyleBasic style = new PointStyleBasic()
                .marker(Markers.CIRCLE)
                .stroke(null)
                .markerRadius(2)
                .fill(Color.black);
        start.setStyle(style);
        end.setStyle(style);
        start.setStyleHint(StyleHints.HIDDEN_HINT, true);
        end.setStyleHint(StyleHints.HIDDEN_HINT, true);
    }

    /** Updates the angles of the tick marks */
    @Override
    protected synchronized void pointsUpdated() {
        Point2D.Double dl = new Point2D.Double(getEndY()-getStartY(), getStartX()-getEndX());
        double magn = Math.sqrt(dl.x*dl.x+dl.y*dl.y);
        dl.x /= magn; dl.y /= magn;
        boolean vert = dl.y >= dl.x;
        if (vert) { dl.x = -dl.x; dl.y = -dl.y; }
        
        double angle = Math.atan2(getEndY()-getStartY(), getEndX()-getStartX());
        start.setAngle(angle+Math.PI);
        end.setAngle(angle);
        
        double pLabel = (1-labelPos)*ruleRight + labelPos*ruleLeft;
        
        labelEntry.clearGraphics();

        GeneralPath tickPath = new GeneralPath();
        if (rulesVisible && ticks != null && ruleLeft != ruleRight) {
            for (int i = 0; i < ticks.length; i++) {
                float f = ticks[i];
                if (f < .02 || f > .98) 
                    continue;
                double x = (1-f)*getStartX() + f*getEndX();
                double y = (1-f)*getStartY() + f*getEndY();
                tickPath.moveTo((float)(x+ruleRight*dl.x), (float)(y+ruleRight*dl.y));
                tickPath.lineTo((float)(x+ruleLeft*dl.x), (float)(y+ruleLeft*dl.y));
                
                if (labelsVisible) {
                    String s = labels == null ? null : labels.length > i ? labels[i] : null;
                    if (s != null)
                        labelEntry.addGraphic(new BasicTextGraphic(
                            new Point2D.Double(x + pLabel*dl.x, y + pLabel*dl.y),
                            s));
                }
            }
        }
        ticksEntry.setPrimitive(tickPath);
        
        lineEntry.setPrimitive(new Line2D.Double(start.getPoint(), end.getPoint()));
        fireGraphicChanged();
    }
    
    //
    // PROPERTIES
    //    

    public ShapeStyle getLineStyle() { return lineEntry.getStyle(); }
    public void setLineStyle(ShapeStyle r) { lineEntry.setStyle(r); }
    
    public float[] getTickPositions() { return ticks; }
    public void setTickPositions(float[] pos) { this.ticks = pos; pointsUpdated(); }
    
    public String[] getTickLabels() { return labels; }
    public void setTickLabels(String[] lab) { this.labels = lab; pointsUpdated(); }


    /** @return tick extension to the left (in pixels) */
    public int getRuleLeft() { return ruleLeft; }
    /** Set tick extension to the left (in pixels) */
    public void setRuleLeft(int size) { ruleLeft = size; pointsUpdated(); }

    /** @return tick extension to the right (in pixels) */
    public int getRuleRight() { return ruleRight; }
    /** Set tick extension to the right (in pixels, usually negative) */
    public void setRuleRight(int size) { ruleRight = size; pointsUpdated(); }

    /** @return visibility of rules */
    public boolean isRulesVisible() { return rulesVisible; }
    /** Sets visibility of rules */
    public void setRulesVisible(boolean val) { rulesVisible = val; pointsUpdated(); }

    /** @return visibility of labels */
    public boolean isLabelsVisible() { return labelsVisible; }
    /** Sets visibility of labels */
    public void setLabelsVisible(boolean val) { labelsVisible = val; pointsUpdated(); }

    /** @return label position (as a percentage) */
    public float getLabelPosition() { return labelPos; }
    /** Sets label position (as a percentage) */
    public void setLabelPosition(float d) { labelPos = d; pointsUpdated(); }

    /** @return mirroring status */
    public boolean isMirrored() { return mirrored; }
    /** Sets mirroring status */
    public void setMirrored(boolean val) { mirrored = val; pointsUpdated(); }
    
    
}
