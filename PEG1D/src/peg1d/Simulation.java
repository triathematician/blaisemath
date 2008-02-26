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
import sequor.model.ComboBoxRangeModel;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import specto.dynamicplottable.Point2D;
import specto.plottable.PointSet2D;
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
    Vector<Double> pDirections;
    Vector<Double> eDirections;
    
    public Simulation(){
        settings=new SimSettings();
        pStartsAt=new Vector<PointRangeModel>(getNP());
        pPaths=new Vector<PointSet2D>(getNP());
        pDirections=new Vector<Double>(getNP());
        eStartsAt=new Vector<PointRangeModel>(getNE());
        ePaths=new Vector<PointSet2D>(getNE());
        eDirections=new Vector<Double>(getNE());
    }
    
    /** resets the entire simulation */
    void reset(Euclidean2 vis){
        initDataModels(vis);
        randomize();    
        initListening();
        run();
    }
    
    /** Sets up data models which can then be placed on a PlotPanel. */    
    void initDataModels(Euclidean2 vis){
        PointRangeModel addThis;
        pStartsAt.clear();
        pPaths.clear();
        for(int i=0;i<getNP();i++){
            addThis=new PointRangeModel();
            pStartsAt.add(addThis);
            pPaths.add(new PointSet2D(vis,settings.colorPursuer.getValue()));
        }
        eStartsAt.clear();
        ePaths.clear();
        for(int i=0;i<getNE();i++){
            addThis=new PointRangeModel();
            eStartsAt.add(addThis);
            ePaths.add(new PointSet2D(vis,settings.colorEvader.getValue()));
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
        reset(p.getVisometry());
        if(pStartsAt==null||pPaths==null||eStartsAt==null||ePaths==null){return;}
        for(PointRangeModel prm:pStartsAt){p.add(new Point2D(p.getVisometry(),prm,settings.colorPursuer.getValue()));}
        for(PointRangeModel prm:eStartsAt){p.add(new Point2D(p.getVisometry(),prm,settings.colorEvader.getValue()));}
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
    
    /** Returns last current position. */
    Double curPPos(int i){return getPPath(i).lastElement().x;}   
    /** Returns evader's current position. */
    Double curEPos(int i){return getEPath(i).lastElement().x;}  
    /** Returns previous pursuer position. */
    Double lastPPos(int i){if(getPPath(i).size()<=1){return null;}else{return getPPath(i).get(getPPath(i).size()-1).x;}}
    /** Returns previous pursuer position. */
    Double lastEPos(int i){if(getEPath(i).size()<=1){return null;}else{return getEPath(i).get(getEPath(i).size()-1).x;}}
    /** Returns ith pursuer path. */
    Vector<R2> getPPath(int i){return pPaths.get(i).getPath();}    
    /** Returns ith evader path. */
    Vector<R2> getEPath(int i){return ePaths.get(i).getPath();}   
    /** Returns vector of pursuer positions. */
    Vector<Double> getPursuerPositions(){
        Vector<Double> result=new Vector<Double>();
        for(int i=0;i<getNP();i++){result.add(curPPos(i));}
        return result;
    } 
    /** Returns vector of pursuer positions. */
    Vector<Double> getEvaderPositions(){
        Vector<Double> result=new Vector<Double>();
        for(int i=0;i<getNE();i++){result.add(curEPos(i));}
        return result;
    }
    
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
    
    /** Main loop for the simulation. Performs one iteration. */
    void loopSimulation(int curStep){     
        // compute directions of all players for use in algorithms
        computeDirections();        
        
        // determine new positions of all players... use the current algorithm settings.
        Vector<Double> newEPos;
        Vector<Double> newPPos;
        switch(getEAlgorithm()){
            case SimSettings.EVADE_TO_GOAL:
            default:
                newEPos=Algorithms.evadersTowardOrigin(getPursuerPositions(), getEvaderPositions(), this, curStep);
                break;
        }
        switch(getPAlgorithm()){
            case SimSettings.PURSUE_DJ:
                // insert your algorithm here
                // you can pass pDirections and eDirections to the algorithm in addition to the parameters shown above and below
                // each of these vectors contains +1,0,-1, depending upon whether the direction is in the positive x-direction, negative x-direction, or not moving
                // be sure to uncomment the "break" line below.
                //break;
            case SimSettings.PURSUE_CLOSEST:
            default:
                newPPos=Algorithms.pursuersTowardClosest(getPursuerPositions(), getEvaderPositions(), this, curStep);
                break;
        }
        
        // moves captured players outside the playing field
        moveCapturedPlayersToInfinity(newPPos,newEPos);
        
        // add new points onto the displayed paths
        for(int i=0;i<getNP();i++){getPPath(i).add(new R2(newPPos.get(i),curStep*getStepSize()));}
        for(int i=0;i<getNE();i++){getEPath(i).add(new R2(newEPos.get(i),curStep*getStepSize()));}
    }
    
    /** Computes directions in which each player is heading. Returns +1,0, or -1. If not enough data to determine a direction, returns 0. */
    void computeDirections(){
        pDirections.clear();
        for(int i=0;i<getNP();i++){
            if(lastPPos(i)==null){
                pDirections.add(0.0);
            }else if(lastPPos(i)<curPPos(i)){
                pDirections.add(1.0);
            }else if(lastPPos(i)>curPPos(i)){
                pDirections.add(-1.0);
            }else{
                pDirections.add(0.0);
            }
        }
        eDirections.clear();
        for(int i=0;i<getNE();i++){
            if(lastEPos(i)==null){
                eDirections.add(0.0);
            }else if(lastEPos(i)<curEPos(i)){
                eDirections.add(1.0);
            }else if(lastEPos(i)>curEPos(i)){
                eDirections.add(-1.0);
            }else{
                eDirections.add(0.0);
            }
        }
    }
    
    /** Moves captured players to infinity. */
    void moveCapturedPlayersToInfinity(Vector<Double> pursuerPositions,Vector<Double> evaderPositions){
        // if any two elements are closer than capture distance, remove to infinity.
        for(int i=0;i<getNP();i++){
            for(int j=0;j<getNE();j++){
                if(Math.abs(pursuerPositions.get(i)-evaderPositions.get(j))<getCaptureRange()){
                    pursuerPositions.set(i,Double.POSITIVE_INFINITY);
                    evaderPositions.set(j,Double.NEGATIVE_INFINITY);
                }
            }
        }
    }
    
    // Bean patterns
    public int getNP(){return settings.numPursuer.getValue();}
    public int getNE(){return settings.numEvader.getValue();}
    public double getPSpeed(){return settings.speedPursuer.getValue();}
    public double getESpeed(){return settings.speedEvader.getValue();}
    public int getPAlgorithm(){return settings.algorithmPursuer.getValue();}
    public int getEAlgorithm(){return settings.algorithmEvader.getValue();}
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
        /** Pursuit algorithm strings */
        public final String[] PURSUIT_STRINGS={"Pursue Closest","DJ's Algorithm"};
        public static final int PURSUE_CLOSEST=0;
        public static final int PURSUE_DJ=1;
        /** Choice of algorithm */
        private ComboBoxRangeModel algorithmPursuer=new ComboBoxRangeModel(PURSUIT_STRINGS,PURSUE_CLOSEST,0,PURSUIT_STRINGS.length-1);
        
        /** Number of evaders */
        private IntegerRangeModel numEvader=new IntegerRangeModel(3,1,50);
        /** Speed of evaders */
        private DoubleRangeModel speedEvader=new DoubleRangeModel(1,0,10,.05);
        /** Evader color */
        private ColorModel colorEvader=new ColorModel(Color.BLUE);
        /** Evader algorithm strings */
        public final String[] EVADER_STRINGS={"Head to Goal"};
        public static final int EVADE_TO_GOAL=0;
        /** Choice of algorithm */
        private ComboBoxRangeModel algorithmEvader=new ComboBoxRangeModel(EVADER_STRINGS,EVADE_TO_GOAL,0,EVADER_STRINGS.length-1);
        
        /** Position of goal */
        private DoubleRangeModel goalPosition=new DoubleRangeModel(0,-1000,1000,.05);
        
        /** Capture region */
        private DoubleRangeModel captureRegion=new DoubleRangeModel(.01,0,1000,.01);
        /** Step size for simulation */
        private DoubleRangeModel stepSize=new DoubleRangeModel(.01,0,1000,.001);
        /** Number of steps in simulation */
        private IntegerRangeModel numSteps=new IntegerRangeModel(1000,0,999999,1);
        
        
        SimSettings(){
            setName("Simulation");
            addProperty("# Pursuers",numPursuer,Settings.EDIT_INTEGER);
            addProperty(" Pursuer Speed",speedPursuer,Settings.EDIT_DOUBLE);
            addProperty(" Pursuer Color",colorPursuer,Settings.EDIT_COLOR);
            addProperty("Pursuer Algorithm",algorithmPursuer,Settings.EDIT_COMBO);
            addSeparator();
            addProperty("# Evaders",numEvader,Settings.EDIT_INTEGER);
            addProperty(" Evader Speed",speedEvader,Settings.EDIT_DOUBLE);
            addProperty(" Evader Color",colorEvader,Settings.EDIT_COLOR);
            addProperty("Evader Algorithm",algorithmEvader,Settings.EDIT_COMBO);
            addSeparator();
            addProperty(" Evader Goal",goalPosition,Settings.EDIT_DOUBLE);
            addSeparator();
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
                fireActionPerformed("reset");
                run();
                fireActionPerformed("redraw");
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
