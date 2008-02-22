package sequor.model;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import scio.coordinate.R2;

/**
 * <b>PointRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 21, 2007, 2:42 PM</i><br><br>
 *
 * This class is intended as a model for working with Cartesian coordinates
 *   within a GUI. It is intended for this class to be used to store the actual
 *   data, and for the GUI modifiers to reference this class in order to change.
 */
public class PointRangeModel extends FiresChangeEvents implements ChangeListener{
    public DoubleRangeModel xModel,yModel;
    
    public PointRangeModel(){
        initializeModels();
        setTo(0,0);
        setBoundsMax();
    }
    public PointRangeModel(DoubleRangeModel xModel,DoubleRangeModel yModel){
        this.xModel=(DoubleRangeModel) xModel.clone();
        this.yModel=(DoubleRangeModel) yModel.clone();
    }
    public PointRangeModel(double x,double y){
        initializeModels();
        setTo(x,y);
        setBoundsMax();
    }
    public PointRangeModel(R2 point){
        initializeModels();
        setTo(point);
        setBoundsMax();
    }
    public PointRangeModel(R2 point,double lx,double ly,double rx,double ry){
        initializeModels();
        setBounds(lx,ly,rx,ry);
        setTo(point);
    }
    public PointRangeModel(R2 point,double range){this(point,-range,-range,range,range);}
    
    private void initializeModels(){
        xModel=new DoubleRangeModel();
        xModel.addChangeListener(this);
        yModel=new DoubleRangeModel();
        yModel.addChangeListener(this);
    }
    
    public void setXModel(DoubleRangeModel xm){xModel=xm;}
    public void setYModel(DoubleRangeModel ym){yModel=ym;}
    public R2 getPoint(){return new R2(xModel.getValue(),yModel.getValue());}
    public R2 getValue(){return getPoint();}
    public double getX(){return xModel.getValue();}
    public double getY(){return yModel.getValue();}
    public double getXRange(){return xModel.getRange();}
    public double getYRange(){return yModel.getRange();}
    
    public void setBounds(double lx,double ly,double rx,double uy){
        xModel.setRangeProperties(xModel.getValue(),lx,rx);
        yModel.setRangeProperties(yModel.getValue(),ly,uy);
    }
    public void setBoundsMax(){
        setBounds(-Double.MAX_VALUE,-Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
    }
    
    // set methods
    public void setTo(double x0,double y0){xModel.setValue(x0);yModel.setValue(y0);}
    public void setTo(R2 point){xModel.setValue(point.x);yModel.setValue(point.y);}
    
    // string methods
    @Override
    public String toString(){return "("+xModel.toString()+","+yModel.toString()+")";}
    @Override
    public String toLongString(){return "("+xModel.toLongString()+","+yModel.toLongString()+")";}
    @Override
    public void setValue(String s){
        // take in (alpha,beta) and set the point using this...
    }
    
    @Override
    public void stateChanged(ChangeEvent e){fireStateChanged();}
    @Override
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,getValue());}

    @Override
    public FiresChangeEvents clone() {return new PointRangeModel(xModel,yModel);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent){
        xModel.setValue(((PointRangeModel)parent).getX());
        yModel.setValue(((PointRangeModel)parent).getY());
    }
}
