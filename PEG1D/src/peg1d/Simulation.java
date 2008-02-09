/*
 * Simulation.java
 * 
 * Created on Oct 17, 2007, 8:28:27 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package peg1d;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import specto.PlotPanel;
import scio.coordinate.R2;
import sequor.component.Settings;
import sequor.model.ColorModel;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import specto.dynamicplottable.Point2D;
import specto.dynamicplottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 *
 * @author ae3263
 */
public class Simulation implements ChangeListener {    
    SimSettings settings;
    Vector<PointRangeModel> pStartsAt;
    Vector<PointRangeModel> eStartsAt;
    Vector<PointSet2D> pPaths;
    Vector<PointSet2D> ePaths;
    
    public Simulation(){
        settings=new SimSettings();
        pStartsAt=new Vector<PointRangeModel>(getNP());
        pPaths=new Vector<PointSet2D>(getNE());
        eStartsAt=new Vector<PointRangeModel>(getNE());
        ePaths=new Vector<PointSet2D>(getNE());
        reset();
    }
    
    /** resets the entire simulation */
    void reset(){
        initDataModels();
        randomize();    
        initListening();
        run();
    }
    
    /** Sets up data models which can then be placed on a PlotPanel. */    
    void initDataModels(){
        PointRangeModel addThis;
        pStartsAt.clear();
        pPaths.clear();
        for(int i=0;i<getNP();i++){
            addThis=new PointRangeModel();
            pStartsAt.add(addThis);
            pPaths.add(new PointSet2D(settings.colorPursuer.getValue()));
        }
        eStartsAt.clear();
        ePaths.clear();
        for(int i=0;i<getNE();i++){
            addThis=new PointRangeModel();
            eStartsAt.add(addThis);
            ePaths.add(new PointSet2D(settings.colorEvader.getValue()));
        }
    }
    
    void initListening(){
        for(PointRangeModel prm:pStartsAt){
            prm.addChangeListener(this);
        }
        for(PointRangeModel prm:eStartsAt){
            prm.addChangeListener(this);
        }
    }
    
    /** Returns plottables to add to the PlotPanel. */
    public void addToPanel(PlotPanel<Euclidean2> p){
        if(pStartsAt==null||pPaths==null||eStartsAt==null||ePaths==null){return;}
        for(PointRangeModel prm:pStartsAt){p.add(new Point2D(prm,settings.colorPursuer.getValue()));}
        for(PointRangeModel prm:eStartsAt){p.add(new Point2D(prm,settings.colorEvader.getValue()));}
        for(PointSet2D ps:pPaths){p.add(ps);}
        for(PointSet2D ps:ePaths){p.add(ps);}
    }
    
    /** Randomizes positions */
    public void randomize(){initRandom(10,10);}
    /** Places points at random along the ``starting line'' */
    void initRandom(double maxp,double maxe){
        for(int i=0;i<getNP();i++){                        
            // place somewhere between x=-max and x=+max and at y=0
            pStartsAt.get(i).setTo(Math.random()*2*maxp-maxp,0.0);
            pStartsAt.get(i).setBounds(-2*maxp,0,2*maxp,0);
        }
        for(int i=0;i<getNE();i++){                        
            // place somewhere between x=-max and x=+max and at y=0
            eStartsAt.get(i).setTo(Math.random()*2*maxp-maxp,0.0);
            eStartsAt.get(i).setBounds(-2*maxp,0,2*maxp,0);
        }
    }
    
    /** Clears all paths. */
    void clearPaths(){
        for(int i=0;i<getNP();i++){getPPath(i).clear();}
        for(int i=0;i<getNE();i++){getEPath(i).clear();}
    }
    
    /** Returns ith pursuer path. */
    Vector<R2> getPPath(int i){return pPaths.get(i).getPath();}
    
    /** Returns last current position. */
    double curPPos(int i){return getPPath(i).lastElement().x;}
    
    /** Returns ith evader path. */
    Vector<R2> getEPath(int i){return ePaths.get(i).getPath();}
    
    /** Returns evader's current position. */
    double curEPos(int i){return getEPath(i).lastElement().x;}
    
    /** Runs the simulation. */
    void runSimulation(int steps){
        clearPaths();
        for(int i=0;i<getNP();i++){getPPath(i).add(new R2(pStartsAt.get(i).getPoint()));}
        for(int i=0;i<getNE();i++){getEPath(i).add(new R2(eStartsAt.get(i).getPoint()));}
        for(int i=0;i<steps;i++){loopSimulation(i);}
        //fireStateChanged();
    }
    
    /** Runs with current number of steps. */
    public void run(){runSimulation(getNumSteps());}
    
    class Closest{
        public Closest(){index=-1;dir=0;dist=Double.POSITIVE_INFINITY;}
        public Closest(int i,double d,double dist){index=i;dir=d;this.dist=dist;}
        public int index;
        public double dir;
        public double dist;
        public Closest replaceIfCloser(int i2,double diff){
            if(Math.abs(diff)<dist){
                index=i2;
                dir=(diff<0)?getStepSize():-getStepSize();
                dist=Math.abs(diff);
            }
            return this;
        }
    }
    
    /** Returns closest evader to a given pursuer. */
    Closest toClosestEvader(int i){
        Closest cl=new Closest();
        double curPPos=curPPos(i);
        for(int j=0;j<getNE();j++){cl.replaceIfCloser(j,curPPos-curEPos(j));}  
        return cl;
    }
    
    /** Returns closest evader to a given pursuer. */
    Closest toClosestPursuer(int i){
        Closest cl=new Closest();
        double curEPos=curEPos(i);
        for(int j=0;j<getNP();j++){cl.replaceIfCloser(j,curEPos-curPPos(j));}  
        return cl;
    }
    
    /** Computes new evader positions. */
    public Vector<R2> getEvaderPositions(int curStep){
        Vector<R2> result=new Vector<R2>();
        if(1==1){
            // evading elements always head to origin for now
            for(int i=0;i<getNE();i++) {
                if(Math.abs(curEPos(i)-getGoal())<getCaptureRange()){
                    result.add(new R2(curEPos(i),curStep*getStepSize()));
                }else if(curEPos(i)<getGoal()){
                    result.add(new R2(curEPos(i)+getStepSize()*getESpeed(),curStep*getStepSize()));
                }else{
                    result.add(new R2(curEPos(i)-getStepSize()*getESpeed(),curStep*getStepSize()));
                }
            }
        }else if(0==1){
            Vector<Double> testRatio=new Vector<Double>();
            for(int i=0;i<getNE();i++){testRatio.add(toClosestPursuer(i).dist/Math.abs(curEPos(i)));}
        }
        return result;
    }
    
    /** Decide pursuer directions */
    void assignPursuerDirections(Vector<Double> evaderPosition,Vector<Double> pursuerPosition){
         // code here
    }
    
    /** Main loop for the simulation. Performs one iteration. */
    void loopSimulation(int curStep){
        // pursuing elements chase closest evader
        int closest;
        Vector<R2> newPPos=new Vector<R2>();
        for(int i=0;i<getNP();i++){
            newPPos.add(new R2(curPPos(i)+toClosestEvader(i).dir*getPSpeed(),curStep*getStepSize()));
        }
        Vector<R2> newEPos=getEvaderPositions(curStep);
        
        // if any two elements are closer than capture distance, remove to infinity.
        for(int i=0;i<getNP();i++){
            for(int j=0;j<getNE();j++){
                if(Math.abs(newPPos.get(i).x-newEPos.get(j).x)<getCaptureRange()){
                    newPPos.get(i).x=Double.POSITIVE_INFINITY;
                    newEPos.get(j).x=Double.NEGATIVE_INFINITY;
                }
            }
        }
        // add new points onto the paths
        for(int i=0;i<getNP();i++){getPPath(i).add(newPPos.get(i));}
        for(int i=0;i<getNE();i++){getEPath(i).add(newEPos.get(i));}
    }
    
    // Bean patterns
    public int getNP(){return settings.numPursuer.getValue();}
    public int getNE(){return settings.numEvader.getValue();}
    public double getPSpeed(){return settings.speedPursuer.getValue();}
    public double getESpeed(){return settings.speedEvader.getValue();}
    public double getGoal(){return settings.goalPosition.getValue();}
    public double getCaptureRange(){return settings.captureRegion.getValue();}
    public double getStepSize(){return settings.stepSize.getValue();}
    public int getNumSteps(){return settings.numSteps.getValue();}
    
    /** Contains all the initial settings for the simulation. */
    private class SimSettings extends Settings {
        /** Number of pursuers */
        private IntegerRangeModel numPursuer=new IntegerRangeModel(3,1,50);
        /** Speed of pursuers */
        private DoubleRangeModel speedPursuer=new DoubleRangeModel(1,0,10,.05);
        /** Pursuer color */
        private ColorModel colorPursuer=new ColorModel(Color.RED);
        /** Behavior (to be implemented */
        // private ComboBoxRangeModel algorithmPursuer=Behavior.getComboBoxModel();
        
        /** Number of evaders */
        private IntegerRangeModel numEvader=new IntegerRangeModel(3,1,50);
        /** Speed of evaders */
        private DoubleRangeModel speedEvader=new DoubleRangeModel(1,0,10,.05);
        /** Evader color */
        private ColorModel colorEvader=new ColorModel(Color.BLUE);
        /** Behavior (to be implemented */
        // private ComboBoxRangeModel algorithmEvader=Behavior.getComboBoxModel();
        
        /** Position of goal */
        private DoubleRangeModel goalPosition=new DoubleRangeModel(0,-1000,1000,.05);
        
        /** Capture region */
        private DoubleRangeModel captureRegion=new DoubleRangeModel(.01,0,1000,.01);
        /** Step size for simulation */
        private DoubleRangeModel stepSize=new DoubleRangeModel(.01,0,1000,.001);
        /** Number of steps in simulation */
        private IntegerRangeModel numSteps=new IntegerRangeModel(1000,0,999999,1);
        
        
        SimSettings(){
            addProperty("# Pursuers",numPursuer,Settings.EDIT_INTEGER);
            addProperty(" Pursuer Speed",speedPursuer,Settings.EDIT_DOUBLE);
            addProperty(" Pursuer Color",colorPursuer,Settings.EDIT_COLOR);
            //addProperty("Pursuer Algorithm",algorithmPursuer,Settings.EDIT_COMBO);
            addPropertySeparator();
            addProperty("# Evaders",numEvader,Settings.EDIT_INTEGER);
            addProperty(" Evader Speed",speedEvader,Settings.EDIT_DOUBLE);
            addProperty(" Evader Color",colorEvader,Settings.EDIT_COLOR);
            //addProperty("Evader Algorithm",algorithmEvader,Settings.EDIT_COMBO);
            addPropertySeparator();
            addProperty(" Evader Goal",goalPosition,Settings.EDIT_DOUBLE);
            addPropertySeparator();
            addProperty("Capture Distance",captureRegion,Settings.EDIT_DOUBLE);
            addProperty(" Step Size",stepSize,Settings.EDIT_DOUBLE);
            addProperty(" # Steps",numSteps,Settings.EDIT_INTEGER);
            initEventListening();
        }
        
        @Override
        public void stateChanged(ChangeEvent e){
            if(e.getSource()==colorEvader||e.getSource()==colorPursuer){        
                for(PointSet2D ps:ePaths){ps.setColor(colorEvader.getValue());}
                for(PointSet2D ps:pPaths){ps.setColor(colorPursuer.getValue());}
                fireActionPerformed("reset");
            }else if(e.getSource()==numPursuer||e.getSource()==numEvader){
                reset();
                run();
                fireActionPerformed("reset");
            }else{
                run();
                fireActionPerformed("redraw");
            }
        }
    }    
    
    // GUI SETTINGS PANEL
    public JPanel getPanel(){return settings.getPanel();}
    
    // EVENT HANDLING
    public void stateChanged(ChangeEvent e){run();}
    
    // Remaining code deals with action listening
    protected ActionEvent actionEvent=null;
    protected EventListenerList listenerList=new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));}
    protected void fireActionPerformed(ActionEvent e){
        actionEvent=e;
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2;i>=0;i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=e;}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
}
