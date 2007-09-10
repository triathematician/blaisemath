/*
 * BParametricFunctionPanel.java
 * 
 * Created on Sep 10, 2007, 1:40:32 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Blaise;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class BParametricFunctionPanel extends JPanel{
    
    public BFunctionEditField fx=new BFunctionEditField("x(t)=","t");
    public BFunctionEditField fy=new BFunctionEditField("y(t)=","t");
    
    public BParametricFunctionPanel(){
        fx.setText("t^3");
        fy.setText("t^2");
        add(new JLabel(fx.getLabel()));
        add(fx);
        add(new JLabel(fy.getLabel()));
        add(fy);
    }
    
    public BPlotParametric2D getPlotFunction(BPlot2D owner){return new BPlotParametric2D(owner,fx.getParser(),fy.getParser());}
}
