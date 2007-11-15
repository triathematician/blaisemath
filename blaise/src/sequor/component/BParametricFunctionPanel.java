/*
 * BParametricFunctionPanel.java
 * 
 * Created on Sep 10, 2007, 1:40:32 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sequor.component;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import scio.function.Function;
import sequor.model.ParametricModel;
import specto.PlotPanel;
import scio.coordinate.R2;
import specto.dynamicplottable.Parametric2D;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class BParametricFunctionPanel extends JPanel{
    
    private FunctionTreeTextField fx;
    private FunctionTreeTextField fy;
    
    public BParametricFunctionPanel(){
        fx=new FunctionTreeTextField("x(t)=","t");
        fy=new FunctionTreeTextField("y(t)=","t");
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
        fx=new FunctionTreeTextField(pm.getTreeX());
        fy=new FunctionTreeTextField(pm.getTreeY());
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
    
    public FunctionTreeTextField getFX(){return fx;}
    public FunctionTreeTextField getFY(){return fy;}
    
    public Function<Double,R2> getFunction(PlotPanel owner){
        return new Function<Double,R2>(){
            public R2 getValue(Double x){return new R2(fx.getF().getValue(x),fy.getF().getValue(x));}
            public R2 minValue(){return null;}
            public R2 maxValue(){return null;}  
        };
    }
}
