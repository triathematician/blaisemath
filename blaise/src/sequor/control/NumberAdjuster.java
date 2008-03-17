/**
 * NumberAdjuster.java
 * Created on Mar 12, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.text.NumberFormat;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.VisualControl;
import sequor.model.BoundedRangeModel;
import sequor.model.ComboBoxRangeModel;
import sequor.model.IntegerRangeModel;
import specto.style.LineStyle;

/**
 * Represents a visual line with a dot (or bar with a dot) which can be used to control the value of a double within a range of values.
 * If displayed as a line, the visual line represents the range of values which are possible. If displayed as a bar, the control acts somewhat
 * like a joystick. The farther the dot is perturbed, the greater the change in the value. Changes are based on the stepsize of the underlying
 * data model.
 * 
 * @author Elisha Peterson
 */
public class NumberAdjuster extends VisualControl {
    
    // PROPERTIES
    
    /** Underlying data model */
    BoundedRangeModel model;
    
    // STYLE 
    
    /** Whether the control has a "joystick style" feel */
    boolean joystick=false;
    /** Controls the visual display of the control */
    public ComboBoxRangeModel style;
    public static final int STYLE_LINE=0;
    public static final int STYLE_BOX=1;
    public static final int STYLE_ROUND_BOX=2;
    public static final int STYLE_DOTS=3;
    public static final int STYLE_GRADIENT=4;
    public static final int STYLE_TRIANGLE=5;
    public static final int STYLE_BOWTIE=6;
    public static final int STYLE_NOB=7;
    public static final int STYLE_JOYSTICK=8;
    final static String[] styleStrings={"Line","Box","Round Box","Dots","Gradient","Triangle","Bowtie","Nob","Joystick"};
    public String[] getStyleStrings(){return styleStrings;}
    
    // orientation
    int orientation=HORIZONTAL;
    public static final int HORIZONTAL=0;
    public static final int VERTICAL=1;
    
    // CONSTRUCTORS

    public NumberAdjuster(Point2D.Double position,BoundedRangeModel model){this(position.x,position.y,model);}
    public NumberAdjuster(double x,double y,BoundedRangeModel model){
        super(x,y,100,20);
        setModel(model);
        model.addChangeListener(this);
        setStyle(STYLE_ROUND_BOX);
    }    
    public NumberAdjuster(double x,double y,double length,double girth,int orientation,int style,BoundedRangeModel model){
        super(x,y);
        setModel(model);
        model.addChangeListener(this);
        setProperties(length,girth,orientation,style);
    }    
    
    // BEAN PATTERNS
    
    public void setModel(BoundedRangeModel model){
        if(model!=null && !model.equals(this.model)){
            if(this.model!=null){this.model.removeChangeListener(this);}
            this.model=model;
            model.addChangeListener(this);
            initLocation();
        }
    }
    public void setStyle(int style){
        if(this.style==null){this.style=new ComboBoxRangeModel(styleStrings,style,0,8);
        }else{this.style.setValue(style);}
        if(style==STYLE_JOYSTICK && !joystick){
            joystick=true;
        }else if(joystick){
            joystick=false;
        }
        initLocation();
    }
    public void setOrientation(int orientation){this.orientation=orientation;initLocation();}
    public void setLength(double length){setSize(length,getGirth());}
    public void setGirth(double girth){setSize(getLength(),girth);}
    public double getLength(){return(orientation==HORIZONTAL)?getWidth():getHeight();}
    public double getGirth(){return(orientation==HORIZONTAL)?getHeight():getWidth();}
    @Override
    public void setSize(double length,double girth){
        if(orientation==HORIZONTAL){super.setSize(length,girth);
        }else{super.setSize(girth,length);}        
    }
    public void setProperties(double length,double girth,int orientation,int style){
        this.orientation=orientation;
        setSize(length,girth);
        setStyle(style);
    }
    
    public ComboBoxRangeModel getOrientationModel(){
        String[] strings={"Horizontal","Vertical"};
        final ComboBoxRangeModel result=new ComboBoxRangeModel(strings,0,0,1);
        result.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setOrientation(result.getValue());
                fireStateChanged();
            }
        });
        return result;
    }
    public IntegerRangeModel getLengthModel(){
        final IntegerRangeModel result=new IntegerRangeModel(100,10,1000,1);
        result.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setLength(result.getValue());
                fireStateChanged();
            }
        });
        return result;
    }
    public IntegerRangeModel getGirthModel(){
        final IntegerRangeModel result=new IntegerRangeModel(20,1,1000,1);        
        result.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setGirth(result.getValue());
                fireStateChanged();
            }
        });
        return result;
    }
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setStroke(LineStyle.THIN_STROKE);
        Point2D.Double position=new Point2D.Double(boundingBox.x+boundingBox.width/2,boundingBox.y+boundingBox.height/2);        
        Point2D.Double end1=getEndpoint1();
        Point2D.Double end2=getEndpoint2();
        Point2D.Double upperLeft=getPercentPoint(0,0);
        double hDistance;
        double vDistance;
        float length=(float) getLength();
        float girth=(float) getGirth();
        if(orientation==HORIZONTAL){hDistance=length;vDistance=girth;
        }else{hDistance=girth;vDistance=length;}
        switch(style.getValue()){
            case STYLE_LINE:
                g.draw(new Line2D.Double(end1,end2));
                break;
            case STYLE_BOX:   
                g.draw(new Rectangle2D.Double(upperLeft.x,upperLeft.y,hDistance,vDistance));
                break;
            case STYLE_ROUND_BOX:
                g.draw(new RoundRectangle2D.Double(upperLeft.x,upperLeft.y,hDistance,vDistance,girth,girth));
                break;
            case STYLE_DOTS:
                float n=(float) Math.floor(length/girth);
                if(n>10){n=10;}
                Point2D.Double center;
                double dotWidth;
                for(float i=0.5f/n;i<1;i+=0.7/n){
                    center=getPercentPoint(i,0.5);
                    dotWidth=(.25+.75*i)*girth;
                    g.draw(new Ellipse2D.Double(center.x-dotWidth/2.0,center.y-dotWidth/2.0,dotWidth,dotWidth));
                }
                break;
            case STYLE_GRADIENT:
                float n2=(float) Math.floor(length/girth);
                if(n2>10){n2=10;}
                Point2D.Double corner;
                double dotWidth2;
                for(float i=0.25f/n2;i<1;i+=1/n2){
                    corner=getPercentPoint(i,0.0);
                    dotWidth2=(1+i/n2)*girth/2.0;
                    g.setColor(Color.getHSBColor(0.3f,0.0f,1.0f-i));                    
                    g.fill(new Rectangle2D.Double(corner.x,corner.y,
                            (orientation==HORIZONTAL)?dotWidth2:girth,
                            (orientation==HORIZONTAL)?girth:dotWidth2));
                }
                break;
            case STYLE_TRIANGLE:
                Path2D.Double path=new Path2D.Double();
                path.moveTo(end1.x,end1.y);
                Point2D.Double nextPoint=getPercentPoint(0.5,0.0);
                path.lineTo(nextPoint.x,nextPoint.y);
                path.lineTo(end2.x,end2.y);
                nextPoint=getPercentPoint(0.5,1.0);
                path.lineTo(nextPoint.x,nextPoint.y);
                path.lineTo(end1.x,end1.y);
                g.draw(path);
                break;
            case STYLE_BOWTIE:
                Path2D.Double path2=new Path2D.Double();
                path2.moveTo(upperLeft.x,upperLeft.y);
                path2.lineTo(position.x,position.y);
                Point2D.Double nextPoint2=getPercentPoint(1.0,0.0);
                path2.lineTo(nextPoint2.x,nextPoint2.y);
                nextPoint2=getPercentPoint(1.0,1.0);
                path2.lineTo(nextPoint2.x,nextPoint2.y);
                path2.lineTo(position.x,position.y);
                nextPoint2=getPercentPoint(0.0,1.0);
                path2.lineTo(nextPoint2.x,nextPoint2.y);
                path2.lineTo(upperLeft.x,upperLeft.y);
                g.draw(path2);
                break;
            case STYLE_NOB:
            case STYLE_JOYSTICK:
                paintJoystick(g);
                break;                
        }
        g.setColor(Color.RED);
        Shape dot=new Ellipse2D.Double(dotPosition.x-girth/2,dotPosition.y-girth/2,girth,girth);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.3f));
        g.fill(dot);        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.draw(dot);
        g.drawString(NumberFormat.getInstance().format(model.getValue()),(float)dotPosition.x+girth/2,(float)dotPosition.y-girth/2);
    }
    
    public void paintJoystick(Graphics2D g){   
        Point2D.Double position=new Point2D.Double(boundingBox.x+boundingBox.width/2,boundingBox.y+boundingBox.height/2);    
        double length=getLength();
        double girth=getGirth();
        switch(orientation){
            case HORIZONTAL:
                g.setColor(Color.getHSBColor(0.3f,0.0f,0.5f));
                g.fill(new Rectangle2D.Double(position.x-length/2,position.y-girth/2,length,girth));
                g.setColor(Color.getHSBColor(0.3f,0.0f,0.7f));
                g.fill(new Rectangle2D.Double(position.x-length/3,position.y-girth/2,2*length/3,girth));
                g.setColor(Color.getHSBColor(0.3f,0.0f,0.9f));
                g.fill(new Rectangle2D.Double(position.x-length/6,position.y-girth/2,length/3,girth));
                break;
            case VERTICAL:
                g.setColor(Color.getHSBColor(0.3f,0.0f,0.5f));
                g.fill(new Rectangle2D.Double(position.x-girth/2,position.y-length/2,girth,length));
                g.setColor(Color.getHSBColor(0.3f,0.0f,0.7f));
                g.fill(new Rectangle2D.Double(position.x-girth/2,position.y-length/3,girth,2*length/3));
                g.setColor(Color.getHSBColor(0.3f,0.0f,0.9f));
                g.fill(new Rectangle2D.Double(position.x-girth/2,position.y-length/6,girth,length/3));
                break;
        }
    }

    
    // EVENT HANDLING

    @Override
    public void stateChanged(ChangeEvent e) {
        initLocation();
        super.stateChanged(e);
    }
    
    
    /** Location of the dot of the control. */
    Point2D.Double dotPosition;    
    /** Denotes the percentage of length of current dot position. This is determined when the "closestOnLine" method is called. */
    double percentLength;
    
    /** Sets location based on the data model. */
    public void initLocation(){
        Point2D.Double position=new Point2D.Double(boundingBox.x+boundingBox.width/2,boundingBox.y+boundingBox.height/2);    
        double length=getLength();
        double girth=getGirth();
        if(dotPosition==null){dotPosition=new Point2D.Double();}
        if(joystick){
            percentLength=0.5;
        }else{
            percentLength=model.getValuePercent();
        }
        if(orientation==HORIZONTAL){
            dotPosition.setLocation(position.x+(percentLength-.5)*(length-girth),position.y);
        }else{
            dotPosition.setLocation(position.x,position.y-(percentLength-.5)*(length-girth));
        }
        fireStateChanged();
    }
    
    /** Sets the location of the dot, and adjusts the underlying data model. */
    public void setDotLocation(Point2D.Double location){
        dotPosition.setLocation(location);
        // adjust the data model
        if(joystick){         
            // increment value based on distance from center
            if(percentLength>.9){
                model.increment(false,100);
            }else if(percentLength>.7){
                model.increment(false,10);
            }else if(percentLength>.51){
                model.increment(false);
            }else if(percentLength>.49){                    
            }else if(percentLength>.3){
                model.increment(false,-1);
            }else if(percentLength>.1){
                model.increment(false,-10);
            }else{
                model.increment(false,-100);
            }   
        }else{
            model.setValuePercent(percentLength);
        }
    }
    
    /** Returns first endpoint of control line. */
    public Point2D.Double getEndpoint1(){return(orientation==HORIZONTAL)?getControlW():getControlN();}
    /** Returns second endpoint of control line. */
    public Point2D.Double getEndpoint2(){return(orientation==HORIZONTAL)?getControlE():getControlS();}
    /** Returns point at specified percentage between endpoints. */
    public Point2D.Double getPercentPoint(double percentLength,double percentGirth){
        return(orientation==HORIZONTAL)?
            new Point2D.Double(getX()+percentLength*getLength(),getY()+percentGirth*getGirth()):
            new Point2D.Double(getX()+percentGirth*getGirth(),getY()+percentLength*getLength());
    }    
    
    /** Returns the point on the control which is closest to the specified point. */
    public Point2D.Double closestOnLine(Point2D.Double point){
        Point2D.Double end1=getEndpoint1();
        Point2D.Double end2=getEndpoint2();
        percentLength=((end1.x-point.x)*(end1.x-end2.x)+(end1.y-point.y)*(end1.y-end2.y))/end1.distanceSq(end2);
        percentLength=(percentLength<0)?0:((percentLength>1)?1:percentLength);
        percentLength=model.closestPercent(percentLength);
        return new Point2D.Double(end1.x+percentLength*(end2.x-end1.x),end1.y+percentLength*(end2.y-end1.y));
    }
        
    @Override
    public void mouseDragged(MouseEvent e) {setDotLocation(closestOnLine(new Point2D.Double(e.getX(),e.getY())));}
    
    @Override
    public void mouseReleased(MouseEvent e) {
        Point2D.Double position=new Point2D.Double(boundingBox.x+boundingBox.width/2,boundingBox.y+boundingBox.height/2);    
        if(joystick){
            dotPosition.setLocation(position);
            fireStateChanged();            
            adjusting=false;
        }else{
            super.mouseReleased(e);
        }
    }
}
