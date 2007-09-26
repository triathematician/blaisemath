/*
 * BPlotParametric2D.java
 * 
 * Created on Sep 10, 2007, 1:49:41 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Blaise;

import Euclidean.PPoint;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class BPlotParametric2D extends BPlotPath2D implements ActionListener {
    
    /** Parser corresponding to the function */
    private BParser px;
    private BParser py;
    /** Owning plot */
    private BPlot2D owner;
    double minT=0;
    double maxT=15;
    double stepT=.1;
    
// Constructors
    
    public BPlotParametric2D(){this(new BPlot2D());}
    public BPlotParametric2D(BPlot2D o){this(o,new BParser(),new BParser());}
    public BPlotParametric2D(BPlot2D o,BParser px,BParser py){
        super();
        setOwner(o);
        this.px=px;this.py=py;
        px.addActionListener(this);
        py.addActionListener(this);
        setColor(Color.BLUE);
        setStroke(new BasicStroke(1.5f));
        recomputeCurve();
        o.repaint();
    }
    
// Bean patterns
    
    public void setOwner(BPlot2D o){owner=o;owner.addActionListener(this);}
    public void setParserX(BParser px){this.px=px;}
    public void setParserY(BParser py){this.py=py;}
    public BParser getParserX(){return px;}
    public BParser getParserY(){return py;}
    //public BFunctionEditField getFunctionField(){
    //    BFunctionEditField result=new BFunctionEditField();
    //    result.setParser(parser);
    //    return result;
    //}
    
// Recomputes the curve... requires an underlying BPlot2D object
    
    /** Gets point at a certain time. */
    public PPoint getValue(double t){return new PPoint(px.getValue(t),py.getValue(t));}
    
    /** Computes the path corresponding to a function graph. */
    private void recomputeCurve(){
        clear();
        for(double t=minT;t<maxT;t+=stepT){add(getValue(t));}
    }
    
    /** Responds to change events. */
    public void stateChanged(ChangeEvent e){}
    /** Responds to action events (including updates of the underlying function). */
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand()!="bounds"){System.out.println("action command: "+e.getActionCommand());}
        if(e.getActionCommand()=="error"){}
        else if(e.getActionCommand()=="change"){recomputeCurve();owner.repaint();}
        //else if(e.getSource()==owner){recomputeCurve();owner.repaint();}
    }
}
