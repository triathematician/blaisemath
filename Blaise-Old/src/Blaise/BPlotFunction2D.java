package Blaise;

import Euclidean.PPoint;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;

/**
 * <b>BPlotFunction2D.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 20, 2007, 10:38 AM</i><br><br>
 *
 * Stores a function and its corresponding path. Updates path as required.<br><br>
 *
 * <b>DESIRED FEATURES LIST:</b><br>
 *    1. Allow right-click editing of the function
 *    2. Option to display the function string on the plot
 *    3. Write function parser interface
 *    4. Implement function "tracing"
 *    5. Expand to permit parametric functions
 *    6. Generate reasonable bounds for a range of x-values (or y-values)
 *    7. Right-click display derivative function
 *    8. Right-click display integral function
 */
public class BPlotFunction2D extends BPlotPath2D {
    
// Fields    
    
    /** Parser corresponding to the function */
    private BParser parser;
    /** Owning plot */
    private BPlot2D owner;
    
// Constructors
    
    /** Constructor: creates a new instance of BPlotFunction2D */
    public BPlotFunction2D(){
        super();
        owner=new BPlot2D();
        owner.addActionListener(this);
        parser=new BParser();
        parser.addActionListener(this);
        parser.setExpressionString("0");
        setColor(Color.RED);
        setStroke(new BasicStroke(1.5f));
    }
    /** Constructor: initializes for a given plot window */
    public BPlotFunction2D(BPlot2D o){
        super();
        setOwner(o);
        parser=new BParser();
        parser.addActionListener(this);
        parser.setExpressionString("0");
        setColor(Color.RED);
        setStroke(new BasicStroke(1.5f));
    }
    
// Bean patterns
    
    public void setParser(BParser p){parser=p;}
    public void setOwner(BPlot2D o){owner=o;owner.addActionListener(this);}
    public BParser getParser(){return parser;}
    public BFunctionEditField getFunctionField(){
        BFunctionEditField result=new BFunctionEditField();
        result.setParser(parser);
        return result;
    }
    
// Recomputes the curve... requires an underlying BPlot2D object
    
    /** Computes the path corresponding to a function graph. */
    private void recomputeCurve(){
        double minX=owner.getLeftX();
        double maxX=owner.getRightX();
        double minY=owner.getLowY();
        double maxY=owner.getUpY();
        int numSamples=owner.getWidth();
        clear();
        double stepX=(maxX-minX)/numSamples;
        for(double x=minX;x<=maxX;x+=stepX){
            double y=parser.getY(x);
            if(y>maxY){y=maxY+1;}
            if(y<minY){y=minY-1;}
            add(new PPoint(x,parser.getY(x)));
        }
    }
    
    /** Responds to change events. */
    public void stateChanged(ChangeEvent e){}
    /** Responds to action events (including updates of the underlying function). */
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==parser){
            if(e.getActionCommand()=="error"){}
            else if(e.getActionCommand()=="change"){recomputeCurve();owner.repaint();}
        }
        if(e.getSource()==owner){recomputeCurve();owner.repaint();}
    }
}
