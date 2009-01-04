package sequor.model;
import java.util.Vector;
import scio.coordinate.R3;
import sequor.FiresChangeEvents;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import javax.xml.bind.annotation.XmlRootElement;
import scio.coordinate.R2;
import scio.random.Random2D;
import scio.random.RandomGenerator;

/**
 * <b>PointRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 21, 2007, 2:42 PM</i><br><br>
 *
 * This class is intended as a model for working with Cartesian coordinates
 *   within a GUI. It is intended for this class to be used to store the actual
 *   data, and for the GUI modifiers to reference this class in order to change.
 */
@XmlRootElement(name="pointRangeModel")
public class PointRangeModel extends FiresChangeEvents implements ChangeListener, RandomGenerator<R2> {
    public DoubleRangeModel xModel,yModel;
    
    public PointRangeModel(){this(0,0);}
    public PointRangeModel(DoubleRangeModel xModel,DoubleRangeModel yModel){
        this.xModel=xModel;
        this.yModel=yModel;
    }
    public PointRangeModel(double x,double y){
        initializeModels();
        setBoundsMax();
        setTo(x,y);
    }
    public PointRangeModel(R2 point){
        initializeModels();
        setBoundsMax();
        setTo(point);
    }
    public PointRangeModel(R2 desiredMin, R2 desiredMax) {this(desiredMin,desiredMin.x,desiredMin.y,desiredMax.x,desiredMax.y);}
    public PointRangeModel(R2 point,double lx,double ly,double rx,double ry){
        initializeModels();
        setBounds(lx,ly,rx,ry);
        setTo(point);
    }
    public PointRangeModel(R2 point,double range){this(point,-range,-range,range,range);}
    public PointRangeModel(double uMin, double uMax, double vMin, double vMax) {
        this(new DoubleRangeModel(uMin, uMax), new DoubleRangeModel(vMin, vMax) );
    }



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
    public R2 getMinimum(){return new R2(xModel.getMinimum(),yModel.getMinimum());}
    public R2 getMaximum(){return new R2(xModel.getMaximum(),yModel.getMaximum());}
    public R2 getCenter(){return new R2((xModel.getMinimum()+xModel.getMaximum())/2.0,(yModel.getMinimum()+yModel.getMaximum())/2.0);}
    public R2 getRValue() {return Random2D.rectangle(xModel.getMinimum(),yModel.getMinimum(),xModel.getMaximum(),yModel.getMaximum());}

    public double getX(){return xModel.getValue();}
    public void setX(double x){xModel.setValue(x);}
    
    public double getY(){return yModel.getValue();}
    public void setY(double y){yModel.setValue(y);}
    
    public double getMinX(){return xModel.getMinimum();}
    public double getMinY(){return yModel.getMinimum();}
    public double getMaxX(){return xModel.getMaximum();}
    public double getMaxY(){return yModel.getMaximum();}
    public double getXRange(){return xModel.getRange();}
    public double getYRange(){return yModel.getRange();}
    public Vector<R2> getValueRange(boolean inclusive, double shiftX, double shiftY) {
        Vector<R2> result = new Vector<R2>();
        for(double x : xModel.getValueRange(inclusive, shiftX)) {
            for (double y : yModel.getValueRange(inclusive, shiftY)) {
                result.add(new R2(x,y));
            }
        }
        return result;
    }
    
    public void setBounds(double lx,double ly,double rx,double uy){
        xModel.setRangeProperties(xModel.getValue(),lx,rx);
        yModel.setRangeProperties(yModel.getValue(),ly,uy);
    }
    public void setBoundsMax(){
        setBounds(-Double.MAX_VALUE,-Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
    }
    
    // set methods
    public void setTo(double x0,double y0){xModel.setValue(x0);yModel.setValue(y0);}
    public void setTo(R2 point){setTo(point.x,point.y);}
    public void update(){setTo(xModel.value, yModel.value);}

    
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

    public boolean contains(R2 point) {
        return xModel.contains(point.x) && yModel.contains(point.y);
    }
}
