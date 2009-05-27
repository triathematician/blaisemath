/**
 * FLabel.java
 * Created on Jun 11, 2008
 */

package sequor.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.VisualControl;
import sequor.model.ColorModel;
import sequor.style.LineStyle;

/**
 * Implements a label, with ability to (i) display an icon to the left or right of the label,
 * and (ii) display a line beneath the label. Similar to JLabel.
 * 
 * @author Elisha Peterson
 */
public class FLabel extends VisualControl {
    /** String to be displayed. */
    Object label;    
    /** Stroke if required. */
    Stroke stroke;    
    /** Whether to display color icon. */
    boolean showColor = true;
    /** Whether to display stroke line. */
    boolean showStroke = false;
    /** Whether to display text. */
    boolean showText = true;

    /** Dimensions of text. */
    Dimension textSize;
    
    /** Constructors. */
    
    /** Default constructor. */
    public FLabel() {
        this("TESTing", Color.RED, LineStyle.STROKES[LineStyle.VERY_THICK]);
        super.setDraggable(true);
    }
    
    /** Constructs with given label, color, and stroke. */
    public FLabel(Object label, Color color, Stroke stroke) {
        this.label = label;
        super.setForeground(color);
        super.setBackground(null);
        this.stroke = stroke;
        showText = label!=null;
        showColor = color!=null;
        showStroke = stroke!=null;
    }

    /** Constructs with given label, color, and stroke. */
    public FLabel(Object label, final ColorModel colorModel, Stroke stroke) {
        this.label = label;
        setForeground(colorModel.getValue());
        colorModel.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setForeground(colorModel.getValue());
            }
        });
        super.setBackground(null);
        this.stroke = stroke;
        showText = label!=null;
        showColor = colorModel!=null;
        showStroke = stroke!=null;
    }
    
    // BEAN PATTERNS

    public String getText() {
        return label.toString();
    }

    public void setText(Object label) {
        this.label = label;
    }    
    
    
    // LAYOUT
    
    /** Computes dimensions of text. */
    public void computeTextSize(FontMetrics fm){
        textSize = new Dimension(fm.stringWidth(getText()), fm.getHeight());
        setSize(getBounds().getSize());
    }
 
    int PADDING = 2;
        
    /** Paints the component. */
    @Override
    public void paintComponent(Graphics2D g, float opacity){
        computeTextSize(g.getFontMetrics());
        //super.paintComponent(g, opacity);
        Rectangle bounds = getBounds();
        Point strokeOffset = new Point(showColor ? bounds.height : PADDING, showText ? textSize.height : bounds.height/2);
        Point textOffset = new Point(strokeOffset.x, textSize.height - PADDING - 1);
        
        g.setColor(getForeground());
        if (showColor) {
            g.fill(new Rectangle2D.Double(getX()+2*PADDING, getY()+2*PADDING,
                    bounds.height - 4*PADDING, bounds.height - 4*PADDING));
        }
        if (showStroke) {
            g.setStroke(stroke);
            g.draw(new Line2D.Double(getX()+strokeOffset.x, getY()+strokeOffset.y,
                    getX()+bounds.width-PADDING, getY()+strokeOffset.y));
        }
        if (showText) {
            g.drawString(getText(), getX()+textOffset.x, getY()+textOffset.y);
        }
    }

    /** Returns bounding box of the component. */
    @Override
    public Rectangle getBounds() {
        if(textSize == null) { textSize = new Dimension(50,10); }
        int boxHeight = textSize.height + (showStroke && showText ? PADDING + 2 : 0);
        return new Rectangle(getX(), getY(), 
                (showColor ? boxHeight : PADDING) + (showText ? textSize.width : 3*textSize.height) + PADDING,
                boxHeight
                );
    }
}
