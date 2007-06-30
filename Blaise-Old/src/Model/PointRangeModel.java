package Model;
import Interface.BModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import Euclidean.PPoint;

/**
 * <b>PointRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 21, 2007, 2:42 PM</i><br><br>
 *
 * This class is intended as a model for working with Cartesian coordinates
 *   within a GUI. It is intended for this class to be used to store the actual
 *   data, and for the GUI modifiers to reference this class in order to change.
 */
public class PointRangeModel extends BModel implements ChangeListener{
    public DoubleRangeModel xModel,yModel;
    
    public PointRangeModel(){
        initializeModels();
        setTo(0,0);
        setBounds(-1,-1,1,1);
    }
    public PointRangeModel(PPoint point,double lx,double ly,double rx,double ry){
        initializeModels();
        setBounds(lx,ly,rx,ry);
        setTo(point);
    }
    public PointRangeModel(PPoint point,double range){this(point,-range,-range,range,range);}
    
    private void initializeModels(){
        xModel=new DoubleRangeModel();
        xModel.addChangeListener(this);
        yModel=new DoubleRangeModel();
        yModel.addChangeListener(this);
    }
    
    public void setXModel(DoubleRangeModel xm){xModel=xm;}
    public void setYModel(DoubleRangeModel ym){yModel=ym;}
    public PPoint getPoint(){return new PPoint(xModel.getValue(),yModel.getValue());}
    public double getXRange(){return xModel.getRange();}
    public double getYRange(){return yModel.getRange();}
    
    public void setBounds(double lx,double ly,double rx,double uy){
        xModel.setRangeProperties(xModel.getValue(),lx,rx);
        yModel.setRangeProperties(yModel.getValue(),ly,uy);
    }
    
    // set methods
    public void setTo(double x0,double y0){xModel.setValue(x0);yModel.setValue(y0);}
    public void setTo(PPoint point){xModel.setValue(point.x);yModel.setValue(point.y);}
    
    // string methods
    public String getString(){return "("+xModel.getString()+","+yModel.getString()+")";}
    public String getLongString(){return "("+xModel.getLongString()+","+yModel.getLongString()+")";}
    public void setValue(String s){
        // take in (alpha,beta) and set the point using this...
    }
    
    public void stateChanged(ChangeEvent e){fireStateChanged();}
}
