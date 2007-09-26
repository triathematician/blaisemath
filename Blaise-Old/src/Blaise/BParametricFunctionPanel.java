/*
 * BParametricFunctionPanel.java
 * 
 * Created on Sep 10, 2007, 1:40:32 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Blaise;

import Interface.BModel;
import Model.ParametricModel;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class BParametricFunctionPanel extends JPanel{
    
    private BFunctionEditField fx;
    private BFunctionEditField fy;
    
    public BParametricFunctionPanel(){
        fx=new BFunctionEditField("x(t)=","t");
        fy=new BFunctionEditField("y(t)=","t");
        fx.setText("t^cos(t)");
        fy.setText("t^sin(t)");
        add(new JLabel(fx.getLabel()));
        add(fx);
        add(new JLabel(fy.getLabel()));
        add(fy);
        setMinimumSize(new Dimension(20,20));
        setPreferredSize(new Dimension(50,25));
        setMaximumSize(new Dimension(50,25));
    }
    public BParametricFunctionPanel(ParametricModel pm){
        fx=new BFunctionEditField(pm.getParserX());
        fy=new BFunctionEditField(pm.getParserY());
        fx.setText(pm.getStringX());
        fy.setText(pm.getStringY());
        add(new JLabel("x(t)="));
        add(fx);
        add(new JLabel("y(t)="));
        add(fy);
        setMinimumSize(new Dimension(20,20));
        setPreferredSize(new Dimension(50,25));
        setMaximumSize(new Dimension(50,25));
    }
    
    public BFunctionEditField getFX(){return fx;}
    public BFunctionEditField getFY(){return fy;}
    
    public BPlotParametric2D getPlotFunction(BPlot2D owner){return new BPlotParametric2D(owner,fx.getParser(),fy.getParser());}
}
