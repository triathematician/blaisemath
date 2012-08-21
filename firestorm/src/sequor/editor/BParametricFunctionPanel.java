/*
 * BParametricFunctionPanel.java
 * 
 * Created on Sep 10, 2007, 1:40:32 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sequor.editor;

import java.awt.Dimension;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import scio.function.FunctionValueException;
import sequor.model.ParametricModel;
import scio.coordinate.R2;
import scio.function.Function;
import sequor.model.FunctionTreeModel;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class BParametricFunctionPanel extends JPanel{
    
    private FunctionTextField fx;
    private FunctionTextField fy;
    
    public BParametricFunctionPanel(){this(new ParametricModel("t^cos(t)","t^sin(t)"));}
    public BParametricFunctionPanel(ParametricModel pm){
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        fx=new FunctionTextField(new FunctionTreeModel(pm.getTreeX()));
        fx.setMinimumSize(new Dimension(50,15)); fx.setPreferredSize(new Dimension(70,20));
        fy=new FunctionTextField(new FunctionTreeModel(pm.getTreeY()));
        fy.setMinimumSize(new Dimension(50,15)); fy.setPreferredSize(new Dimension(70,20));
        fx.setText(pm.getXString());
        fy.setText(pm.getYString());
        add(new JLabel(" x(t)= "));
        add(fx);
        add(new JLabel(" y(t)= "));
        add(fy);
    }
    
    public FunctionTextField getFX(){return fx;}
    public FunctionTextField getFY(){return fy;}
    
    public Function<Double,R2> getFunction(){
        return new Function<Double,R2>(){        
            public R2 getValue(Double x) throws FunctionValueException{
                return new R2(fx.getF().getValue(x),fy.getF().getValue(x));
            }
            @Override
            public Vector<R2> getValue(Vector<Double> x) throws FunctionValueException {
                Vector<R2> result=new Vector<R2>(x.size());
                for(Double d:x){result.add(getValue(d));}
                return result;
            }
        };
    }
}
