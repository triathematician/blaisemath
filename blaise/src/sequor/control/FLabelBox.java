/**
 * FLabelBox.java
 * Created on Jun 11, 2008
 */

package sequor.control;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import sequor.VisualControl;
import sequor.VisualControlGroup;

/**
 * Contains several labels with ability to display inside a single box.
 * @author Elisha Peterson
 */
public class FLabelBox extends VisualControlGroup {
    
    // CONSTRUCTORS
    
    /** Default constructor. */
    public FLabelBox(){
        this(0,0,50,50);
        add(new FLabel());
        add(new FLabel());
    }

    /** Constructs to given width and height. */
    public FLabelBox(int x, int y, int wid, int ht) {
        super(x, y, wid, ht);
        spacing=2;
        padding=2;
        paintBorder=false;
    }
        
    
    // BEAN PATTERNS
    
    /** Returns a specific element, cast to a VisualButton */
    public FLabel getElement(int i){return (FLabel)elements.get(i);} 

    
    // ADD/REMOVE METHODS

    /** Only accept additions of labels. */
    @Override
    public void add(VisualControl vc) {
        if(!(vc instanceof FLabel)){return;}
        super.add(vc);
        if (fm!=null) {
            ((FLabel)vc).computeTextSize(fm);
        }
        performLayout();
    }
    
    public void clear() {
        super.elements.clear();
    }
    

    // LAYOUT METHODS
    
    FontMetrics fm = null;
    
    /** Returns width of largest element. */
    public int getMaxElementWidth(){
        if (fm==null){return 0;}
        int maxWidth = 0;
        for(VisualControl vc : elements) {
            ((FLabel)vc).computeTextSize(fm);
            maxWidth = Math.max(maxWidth, vc.getWidth());
        }
        return maxWidth;
    }
    
    /** Sets the positions of all the buttons. */
    public void performLayout(){
        if (elements == null || elements.size() == 0) { return; }
        int elementHeight = elements.firstElement().getHeight();
        setSize(getMaxElementWidth()+2*padding, elementHeight*elements.size()+3*padding);
        for(int i=0;i<elements.size();i++){
            elements.get(i).setLocation(
                    getX()+padding,
                    getY()+padding+i*(spacing+elementHeight));
        }       
    }
    
    /** Paint method. */
    @Override
    public void paintComponent(Graphics2D g) {
        fm = g.getFontMetrics();
        performLayout();
        super.paintComponent(g);
    }
    
    /** Paint method. */
    @Override
    public void paintComponent(Graphics2D g, float opacity) {
        fm = g.getFontMetrics();
        performLayout();
        super.paintComponent(g, opacity);
    }
}
