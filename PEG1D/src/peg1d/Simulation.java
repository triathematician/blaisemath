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
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import scio.coordinate.R2;
import sequor.Settings;
import sequor.control.NumberSlider;
import sequor.control.SliderBox;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import specto.dynamicplottable.InitialPointSet2D;

/**
 *
 * @author ae3263
 */
public class Simulation implements ChangeListener {    
    public DataLog log;
    SimSettings settings;
    Vector<InitialPointSet2D> pursuers;
    Vector<InitialPointSet2D> evaders;
    Vector<Double> pDirections;
    Vector<Double> eDirections;
    public boolean batchProcessing = false;
    
    JTextArea outputArea;
    
    public Simulation(){
        settings=new SimSettings();
        initializeAll();
        reset();
        run();
    }
    
    void initializeAll(){
        log=new DataLog(this);
        pursuers=new Vector<InitialPointSet2D>(getNP());
        evaders=new Vector<InitialPointSet2D>(getNE());
    }
    
    /** resets the entire simulation */
    void reset(){
        pursuers.clear();
        for(int i=0;i<getNP();i++){pursuers.add(new InitialPointSet2D(settings.colorPursuer.getValue()));}
        for(int i=0;i<getNP();i++){pursuers.get(i).setLabel("P"+i);}
        evaders.clear();
        for(int i=0;i<getNE();i++){evaders.add(new InitialPointSet2D(settings.colorEvader.getValue()));}
        for(int i=0;i<getNE();i++){evaders.get(i).setLabel("E"+i);}
        randomizePositions();    
        initListening();
    }
    
    boolean adjusting=false;
    
    void initListening(){        
        for(InitialPointSet2D ips:pursuers){ips.addChangeListener(this);}
        for(InitialPointSet2D ips:evaders){ips.addChangeListener(this);}
    }
    
    /** Randomizes positions */
    public void randomizePositions(){randomizePositions(10,10);}
    /** Places points at random along the ``starting line'' */
    void randomizePositions(double maxp,double maxe){
        adjusting=true;
        for(int i=0;i<getNP();i++){                        
            // place somewhere between x=-max and x=+max and at y=0
            pursuers.get(i).setPoint(new R2(Math.random()*2*maxp-maxp,0.0));
            pursuers.get(i).getModel().setBounds(-2*maxp,0.0,2*maxp,0.0);
        }
        for(int i=0;i<getNE();i++){                        
            // place somewhere between x=-max and x=+max and at y=0
            evaders.get(i).setPoint(new R2(Math.random()*2*maxe-maxe,0.0));
            evaders.get(i).getModel().setBounds(-2*maxe,0.0,2*maxe,0.0);
        }
        adjusting=false;
    }
    
    /** Returns last current position. */
    Double curPPos(int i){return getPPath(i).lastElement().x;}   
    /** Returns evader's current position. */
    Double curEPos(int i){return getEPath(i).lastElement().x;}  
    /** Returns previous pursuer position. */
    Double lastPPos(int i){if(getPPath(i).size()<=1){return null;}else{return getPPath(i).get(getPPath(i).size()-2).x;}}
    /** Returns previous pursuer position. */
    Double lastEPos(int i){if(getEPath(i).size()<=1){return null;}else{return getEPath(i).get(getEPath(i).size()-2).x;}}
    /** Returns ith pursuer path. */
    Vector<R2> getPPath(int i){return pursuers.get(i).getPath();}
    /** Returns ith evader path. */
    Vector<R2> getEPath(int i){return evaders.get(i).getPath();}
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
    
    /** Clears all paths. */
    void clearPaths(){
        for(int i=0;i<getNP();i++){getPPath(i).clear();}
        for(int i=0;i<getNE();i++){getEPath(i).clear();}
    }
    
    /** Runs the simulation.
     * @param steps the maximum number of steps to allow the simulation to run
     * @return true if a team won, otherwise false
     */
    boolean runSimulation(int steps){
        boolean winner=false;
        clearPaths();
        pDirections = null;
        eDirections = null;
        curPPos = null;
        newPPos = null;
        curEPos = null;
        newEPos = null;
        curTime = 0;
        log.preRun();
        for (int i = 0; i < getNP(); i++) {
            getPPath(i).add(new R2(pursuers.get(i).getPoint()));
        }
        for (int i = 0; i < getNE(); i++) {
            getEPath(i).add(new R2(evaders.get(i).getPoint()));
        }
        Boolean stateChange = true;     // used to determine whether algorithm needs to be re-run in a given step
        for (int i = 0; i < steps; i++) {
            if(loopSimulation(i, stateChange)){
                winner=true;
                break;
            }
        }
        if (!batchProcessing && outputArea != null) {
            //try {outputArea.getDocument().remove(0, outputArea.getDocument().getLength()-1);}catch(Exception e){}
            outputArea.append("\n--New Simulation--\n");
            log.output(outputArea);
        }
        return winner;
    }
    
    /** Runs with current number of steps.
     * @return true if a team won, otherwise false
     */
    public boolean run(){return runSimulation(getNumSteps());} 
    
    Vector<Double> curPPos;
    Vector<Double> newPPos;
    Vector<Double> curEPos;
    Vector<Double> newEPos;
    double curTime;
    
    /** Main loop for the simulation. Performs one iteration. */
    boolean loopSimulation(int curStep, Boolean stateChange){ 
        curTime = getStepSize() * curStep;
        // determine new positions of all players... use the current algorithm settings.
        HashMap<Integer, Integer> pursuerAssignment = new HashMap<Integer, Integer> ();
        curPPos = (newPPos == null ? getPursuerPositions() : newPPos);
        curEPos = (newEPos == null ? getEvaderPositions() : newEPos);
        switch(getEAlgorithm()){
            case SimSettings.EVADE_TO_GOAL:
            default:
                newEPos = Algorithms.evadersTowardOrigin(curPPos, curEPos, this, curStep);
                break;
        }
        
        // only use the algorithm if the simulation status has changed
        if(stateChange) {
            switch(getPAlgorithm()){
                case SimSettings.PURSUE_DJ:                
                    if(eDirections!=null){
                        pursuerAssignment=Algorithms.pursuers_DJ(curPPos, curEPos, eDirections, this, curStep);
                    }else{
                        pursuerAssignment=Algorithms.pursuersTowardClosest(curPPos, curEPos, this, curStep);
                    }    
                    break;
                case SimSettings.PURSUE_CLOSEST:
                default:
                    pursuerAssignment=Algorithms.pursuersTowardClosest(curPPos, curEPos, this, curStep);
                    break;
            }
        }
        
       //  checks to see if algorithm failed to give complete assignment
        newPPos = Algorithms.getNewPursuerPositions(pursuerAssignment, curPPos, curEPos, this);
        if(newPPos==null) { return false; }
        
        // moves captured players outside the playing field
        stateChange = moveCapturedPlayersToInfinity(newPPos, newEPos, curTime);
        
        // add new points onto the displayed paths
        for(int i=0;i<getNP();i++){getPPath(i).add(new R2(newPPos.get(i),curTime));}
        for(int i=0;i<getNE();i++){getEPath(i).add(new R2(newEPos.get(i),curTime));} 
        
        // compute directions of all players for use in algorithms
        computeDirections();                
          
        // checks for victory, returns true if the simulation should stop
        return checkVictoryConditions(newPPos,newEPos,curTime);
    }
    
    /** Computes directions in which each player is heading. Returns +1,0, or -1. If not enough data to determine a direction, returns 0. */
    void computeDirections(){
        if(pDirections==null){pDirections=new Vector<Double>(getNP());}else{pDirections.clear();}
        for(int i=0;i<newPPos.size();i++){
            pDirections.add(Math.signum(newPPos.get(i)-curPPos.get(i)));
        }
        if(eDirections==null){eDirections=new Vector<Double>(getNE());}else{eDirections.clear();}
        for(int i=0;i<newEPos.size();i++){
            eDirections.add(Math.signum(newEPos.get(i)-curEPos.get(i)));
        }
    }
    
    /** Moves captured players to infinity. Returns true if any players were captured, otherwise false. */
    boolean moveCapturedPlayersToInfinity(Vector<Double> pursuerPositions,Vector<Double> evaderPositions,double time){
        boolean capture = false;
        double capRange = getCaptureRange();
        // if any two elements are closer than capture distanceTo, remove to infinity.        
        for(int i=0;i<pursuerPositions.size();i++){
            for(int j=0;j<evaderPositions.size();j++){
                try {
                if(Math.abs(pursuerPositions.get(i)-evaderPositions.get(j))<capRange){
                    log.logCapture(i,j,pursuerPositions.get(i),time);
                    pursuerPositions.set(i,Double.POSITIVE_INFINITY);
                    evaderPositions.set(j,Double.NEGATIVE_INFINITY);
                    capture = true;
                }
                } catch (NullPointerException e){                    
                }
            }
        }
        return capture;
    }
    
    /** Checks to see if either team has won. Returns true if either team has won. */
    boolean checkVictoryConditions(Vector<Double> pursuerPositions,Vector<Double> evaderPositions,double time){
        boolean pursuersWin=true;
        boolean evadersWin=false;
        
        double capRange = getCaptureRange();
        for(Double d:evaderPositions){
            if(!d.equals(Double.NEGATIVE_INFINITY)){pursuersWin=false;}
            if(Math.abs(d-getGoal()) < capRange){evadersWin=true;break;}
        }
        
        if(pursuersWin){
            log.significantEvents.add(new DataLog.SignificantEvent(time,"Pursuers win"));              
            log.pursuersWin=true;
            log.time=time;
        }else if(evadersWin){
            log.significantEvents.add(new DataLog.SignificantEvent(time,"Evaders win"));  
            log.pursuersWin=false;
            log.time=time;
        }
        
        return pursuersWin || evadersWin;
    }
    
    // Bean patterns
    public int getNP(){return settings.numPursuer.getValue();}
    public int getNE(){return settings.numEvader.getValue();}
    public double getPSpeed(){return settings.speedPursuer.getValue();}
    public double getESpeed(){return settings.speedEvader.getValue();}
    public int getPAlgorithm(){return settings.algorithmPursuer.getValue();}
    public int getEAlgorithm(){return settings.algorithmEvader.getValue();}
    public Color getPColor(){return settings.colorPursuer.getValue();}
    public Color getEColor(){return settings.colorEvader.getValue();}
    public double getGoal(){return settings.goalPosition.getValue();}
    public double getCaptureRange(){return settings.captureRegion.getValue();}
    public double getStepSize(){return settings.stepSize.getValue();}
    public int getNumSteps(){return settings.numSteps.getValue();}
    
    /** Returns line corresponding to the evader's goal. */
    public InitialPointSet2D getGoalLine(){
        final InitialPointSet2D result=new InitialPointSet2D(
                new PointRangeModel(settings.goalPosition,
                new DoubleRangeModel(0,0,0,0)));
        result.setColor(Color.DARK_GRAY);
        result.style.setValue(InitialPointSet2D.CIRCLE);
        settings.goalPosition.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                Vector<R2> path=new Vector<R2>();
                path.add(result.getPoint());
                path.add(result.getPoint().plus(new R2(0,999999)));
                result.setPath(path);
            }
        });
        return result;
    }
    
    /** Returns control box with pursuer/evader speeds. */
    public SliderBox getSpeedBox(){
        SliderBox result=new SliderBox();
        result.add(new NumberSlider(settings.speedPursuer));
        result.add(new NumberSlider(settings.speedEvader));
        return result;
    }
    
    /** Contains all the initial settings for the simulation. */
    private class SimSettings extends Settings {
        /** Number of pursuers */
        private IntegerRangeModel numPursuer=new IntegerRangeModel(3,1,50);
        /** Speed of pursuers */
        private DoubleRangeModel speedPursuer=new DoubleRangeModel(1,0,5,.05);
        /** Pursuer color */
        private ColorModel colorPursuer=new ColorModel(Color.RED);
        /** Pursuit algorithm strings */
        public final String[] PURSUIT_STRINGS={"Pursue Closest","DJ's Algorithm"};
        public static final int PURSUE_CLOSEST=0;
        public static final int PURSUE_DJ=1;
        /** Choice of algorithm */
        private StringRangeModel algorithmPursuer=new StringRangeModel(PURSUIT_STRINGS,PURSUE_CLOSEST,0,PURSUIT_STRINGS.length-1);
        
        /** Number of evaders */
        private IntegerRangeModel numEvader=new IntegerRangeModel(3,1,50);
        /** Speed of evaders */
        private DoubleRangeModel speedEvader=new DoubleRangeModel(1,0,5,.05);
        /** Evader color */
        private ColorModel colorEvader=new ColorModel(Color.BLUE);
        /** Evader algorithm strings */
        public final String[] EVADER_STRINGS={"Head to Goal"};
        public static final int EVADE_TO_GOAL=0;
        /** Choice of algorithm */
        private StringRangeModel algorithmEvader=new StringRangeModel(EVADER_STRINGS,EVADE_TO_GOAL,0,EVADER_STRINGS.length-1);
        
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
        }
        
        @Override
        public void stateChanged(ChangeEvent e){
            if(e.getSource()==colorEvader || e.getSource()==colorPursuer){
                log.recolor();
                fireActionPerformed("redraw");
            }else if(e.getSource()==numPursuer||e.getSource()==numEvader){
                reset();
                log.initializeNumbersOnly();
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
    public void stateChanged(ChangeEvent e){if(adjusting){return;}run();}
    
    // Remaining code deals with action listening
    protected ActionEvent actionEvent=null;
    protected EventListenerList listenerList=new EventListenerList();
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class, l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class, l);}
    protected void fireActionPerformed(String s){fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,s));}
    protected void fireActionPerformed(ActionEvent e){
        if(adjusting){return;}
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
