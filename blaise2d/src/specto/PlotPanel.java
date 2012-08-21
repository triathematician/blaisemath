/*
 * PlotPanel.java
 * Created on Sep 14, 2007, 7:50:15 AM
 */

// TODO Require "toString" method for Plottable's

package specto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.VisualControl;
import sequor.component.RangeTimer;
import sequor.control.AnimationControl;
import sequor.control.DrawnPath;
import sequor.control.MarkerBox;
import sequor.control.NumberSlider;
import sequor.control.SliderBox;
import sequor.event.MouseVisometryEvent;
import sequor.event.MouseVisometryListener;
import sequor.model.IntegerRangeModel;

/**
 * This is a superclass for plot windows. It implements component handling and drawing
 * functions, where components are Plottable's or DynamicPlottable's.
 * <br><br>
 * @author Elisha Peterson
 */
public abstract class PlotPanel<V extends Visometry> extends JPanel 
        implements ActionListener,ChangeListener,MouseListener,MouseMotionListener,MouseWheelListener {
    
    
    // PROPERTIES
    
    /** The underlying visometry, which describes coordinate transformations. */
    protected V visometry;
    /** The components displayed on the panel before anything else (e.g. grid) */
    private PlottableGroup<V> baseComponents;
    /** General components */
    private PlottableGroup<V> components;
    /** Visual controls */
    private HashSet<VisualControl> controls;
    
    /** Model used for timer. */
    private IntegerRangeModel timerModel;
    /** Timer object for animations. */
    private RangeTimer timer;
    
    /** The context menu. */
    private JPopupMenu contextMenu;
    /** The options sub-menu of the context menu. */
    private JMenu optionsMenu;
    
    /** Standard layout components. */
    private AnimationControl animator;
    private NumberSlider timerBar;
    private MarkerBox markerBox;    
    
    /** Layout elements. */
    private JToolBar toolBar;
    private JPanel propertyPanel;
    private JPanel inputPanel;
    private JPanel statusPanel;
    private JPanel mainPanel;

    // SETTINGS
    
    /** Determines whether all components should be recomputed. If true, all elements are recomputed.
     * If false, just the animatable's are "recomputed". */
    private boolean recomputeAll = false;
    
    /** Determines whether or not animation controls are shown. */
    private boolean showAnimator = true;
    
    /** Determines whether or not markers are shown. */
    private boolean showMarkers = true;
        
    // CONSTRUCTORS
    
    public PlotPanel(){}
    public PlotPanel(V visometry){
        initComponents();
        initLayout();
        initVisometry(visometry);
    }
    
    
    // INITIALIZERS
    
    private void initComponents(){
        timerModel=new IntegerRangeModel(0,0,10);
        timerModel.addChangeListener(this);
        timer=new RangeTimer(timerModel);
        timer.setLooping(true);
        timer.addActionListener(this);
        initContextMenu();
        
        controls=new HashSet<VisualControl>();
        if(showAnimator){ addAnimationControls(); }
        if(showMarkers){ addMarkerBox(); }
        
        baseComponents=new PlottableGroup<V>();
        components=new PlottableGroup<V>();
        baseComponents.addChangeListener(this);
        components.addChangeListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    private void addAnimationControls(){
        if(animator==null){ animator=new AnimationControl(0,0,timer,AnimationControl.LAYOUT_HLINE); }
        if(!controls.contains(animator)){ add(animator,3,0); }
        if(timerBar==null){ timerBar = new NumberSlider(210,10,timer.getModel());}
        timerBar.setName("Timer");
        if(!controls.contains(timerBar)){ add(timerBar,3,1); }
    }
    private void removeAnimationControls(){
        if(controls.remove(animator)){animator.removeChangeListener(this);}
        if(controls.remove(timerBar)){timerBar.removeChangeListener(this);}
    }
    
    private void addMarkerBox(){
        if(markerBox==null){
            markerBox=new MarkerBox(Color.YELLOW,DrawnPath.HIGHLIGHTER);
            markerBox.addMarker(Color.BLUE, DrawnPath.MARKER);
            markerBox.addMarker(Color.RED, DrawnPath.PEN);
            markerBox.addMarker(Color.BLACK, DrawnPath.PENCIL);
        }
        if(!controls.contains(markerBox)){ add(markerBox,3,2); }
    }
    private void removeMarkerBox(){
        if(controls.remove(markerBox)){markerBox.removeChangeListener(this);}
    }
    
    private void initLayout(){
        setBackground(Color.WHITE);
        setOpaque(true);   
        setPreferredSize(new Dimension(300,200));
        toolBar=new JToolBar();
        propertyPanel=new JPanel();
        inputPanel=new JPanel();
        statusPanel=new JPanel();
        mainPanel=new JPanel();
        BorderLayout layout=new BorderLayout();
        layout.addLayoutComponent(toolBar,BorderLayout.NORTH);
        layout.addLayoutComponent(propertyPanel,BorderLayout.WEST);
        layout.addLayoutComponent(inputPanel,BorderLayout.EAST);
        layout.addLayoutComponent(statusPanel,BorderLayout.SOUTH);
        layout.addLayoutComponent(mainPanel,BorderLayout.CENTER);
        setLayout(layout);
    }
    
    private void initContextMenu(){
        contextMenu=new JPopupMenu();
        optionsMenu=new JMenu("Change style");
        contextMenu.add(optionsMenu);
        // add item to open window with settings
//        contextMenu.add(new JSeparator());
//        JMenuItem popup = new JMenuItem("All settings");
//        popup.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                new SettingsTreeDialog(components., this, false);
//            }
//        });
//        contextMenu.add(popup);
// commenting this out for now. would like to return in the future        
//        contextMenu.add(new AddMenu(this));
//        JMenuItem mi=new JMenuItem("test XML output");
//        mi.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("\nGenerating XML Output...\n----\n\n");
//                System.out.println("\n----\nEnd of XML Output...\n\n");
//            }
//        });
//        contextMenu.add(mi);
        setComponentPopupMenu(contextMenu);        
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public boolean isAnimatorVisible(){return showAnimator;}
    public void setAnimatorVisible(boolean newValue){
        if(newValue!=showAnimator){
            if(newValue==true){showAnimator=true;addAnimationControls();}
            if(newValue==false){showAnimator=false;removeAnimationControls();}
        }
    }
    
    public boolean isMarkerBoxVisible(){return showMarkers;}
    public void setMarkerBoxVisible(boolean newValue){
        if(newValue!=showMarkers){
            if(newValue==true){showMarkers=true;addMarkerBox();}
            if(newValue==false){showMarkers=false;removeMarkerBox();}
        }
    }
    
    /** Returns timer used with this plot. */
    public RangeTimer getTimer(){return timer;}
    
    /** Sets timer for use with this plot to that specified. */
    public void setTimer(RangeTimer timer){
        if(this.timer != timer) {
            if(animator!=null){animator.setTimer(timer);}
            if(timerBar!=null){timerBar.setModel(timer.getModel());}
            timerModel.removeChangeListener(this);
            timerModel = (IntegerRangeModel) timer.getModel();
            timerModel.addChangeListener(this);
            this.timer.removeActionListener(this);
            this.timer = timer;
            timer.addActionListener(this);
        }
    }
    
    /** Sets timer based on another plot window. */
    public void synchronizeTimerWith(PlotPanel pp) {
        setTimer(pp.getTimer());
    }
    
    /** Returns visometry used for this panel. */
    public V getVisometry(){return visometry;}
    
    /** Sets the timer to that specified. */    
    private void initVisometry(V newValue){
        visometry=newValue;
        visometry.initContainer(this);
        visometry.addChangeListener(this);
        addComponentListener(visometry);
        addToContextMenu(visometry.getMenuItems());
    }
    
    public Collection<Plottable> getBasePlottables(){return baseComponents.getElements();}
    public Collection<Plottable> getPlottables(){return components.getElements();}
        
    public Collection<VisualControl> getControls(){return controls;}    
    
    
    // COMPONENT HANDLING
        
   public void addToContextMenu(Vector<JMenuItem> mis){
       if(mis==null){return;}
       getComponentPopupMenu().addSeparator();
       for(JMenuItem mi:mis){
            if(mi==null){getComponentPopupMenu().addSeparator();}
            else{getComponentPopupMenu().add(mi);}
       }
   }
   
   public void add(VisualControl vc){
       if(vc!=null){
           controls.add(vc);
           vc.addChangeListener(this);
       }
   }
   public void add(VisualControl vc,int padding,int stickyPoint){
       add(vc);
       vc.enableSnapping(this,padding,stickyPoint);
   }
   public void remove(VisualControl vc){controls.remove(vc);vc.removeChangeListener(this);}
    
   public void addBase(Plottable<V> pv){baseComponents.add(pv);rebuildOptionsMenu();}
   public void add(Plottable<V> pv){components.add(pv);rebuildOptionsMenu();}
   public <T extends Plottable<V>> void addAll(Collection<T> cpv){for(T pv:cpv){add(pv);}}
   public void remove(Plottable<V> pv){components.remove(pv);rebuildOptionsMenu();}
   public <T extends Plottable<V>> void removeAll(Collection<T> cpv){for(T pv:cpv){remove(pv);}}
   public void clearPlottables(){components.clear();rebuildOptionsMenu();}
   public void rebuildOptionsMenu(){
        optionsMenu.removeAll();
        for(Plottable<V> p:baseComponents.getElements()){
            try{
                optionsMenu.add(p.getOptionsMenu());
            }catch(NullPointerException e){}
        }
        optionsMenu.addSeparator();
        for(Plottable<V> p:components.getElements()){
            try{
                optionsMenu.add(p.getOptionsMenu());
            }catch(NullPointerException e){}
        }
    }
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics gb){
        super.paintComponent(gb);
        if (isOpaque()) {
            gb.setColor(getBackground());
            gb.fillRect(0, 0, getWidth(), getHeight());
        }
        if(visometry==null){return;}
        Graphics2D g=(Graphics2D)gb;        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        // Recompute any necessary elements before plotting
        baseComponents.recompute(visometry,recomputeAll);
        components.recompute(visometry,recomputeAll);
        if(timer!=null&&!timer.isStopped()){            
            baseComponents.paintComponent(g,visometry,timer);
            components.paintComponent(g,visometry,timer);
        }else{
            baseComponents.paintComponent(g,visometry);
            components.paintComponent(g,visometry);
        }
        // Draw visual controls
        for(VisualControl vc:controls){
            vc.paintComponent(g);
        }
        recomputeAll = false;
    }
    
    // EVENT HANDLING
    
    /** When the state of one of the components changes, requiring the panel to be redrawn. May also represent
     * changes in the visometry.
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e){
        recomputeAll=true;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(timer)){
            if(e!=null && e.getActionCommand().equals("restart")){
                timerModel.setMaximum(components.getAnimatingSteps());                    
            }else{
                repaint();
            }
        }
    }
    
    // MOUSE EVENT HANDLING
    
    MouseVisometryListener<V> mover;
    MouseListener altMover;
    
    Vector<MouseVisometryListener<V>> getHits(MouseVisometryEvent mve){
        Vector<MouseVisometryListener<V>> result=components.getHits(mve);
        if(result.isEmpty()){
            result=baseComponents.getHits(mve);
        }
        return result;
    }
    Vector<MouseListener> getHits(MouseEvent e){
        Vector<MouseListener> result=new Vector<MouseListener>();
        for(VisualControl vc:controls){
            if(vc.clicked(e)){result.add(vc);}
        }
        result.add(visometry);
        return result;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        Vector<MouseVisometryListener<V>> hits=getHits(mve);
        if(!hits.isEmpty()){
            hits.firstElement().mouseClicked(mve);
        }
        else{
            Vector<MouseListener> altHits=getHits(e);
            altHits.firstElement().mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        Vector<MouseVisometryListener<V>> hits=getHits(mve);
        if(!hits.isEmpty()){
            mover=hits.firstElement();
            altMover=null;
            mover.mousePressed(mve);
        }
        else{
            Vector<MouseListener> altHits=getHits(e);
            altMover=altHits.firstElement();
            mover=null;
            altMover.mousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        if(mover!=null){mover.mouseDragged(mve);}
        else if(altMover!=null && altMover instanceof MouseMotionListener){((MouseMotionListener)altMover).mouseDragged(mve);}
    }

    @Override
    public void mouseReleased(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        if(mover!=null){mover.mouseReleased(mve);mover=null;}
        else if(altMover!=null){altMover.mouseReleased(mve);altMover=null;}
    }

    @Override
    public void mouseMoved(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        for(Plottable p:baseComponents.getElements()){
            if(p instanceof DynamicPlottable){
                ((DynamicPlottable)p).mouseMoved(mve);
            }
        }
        visometry.mouseMoved(mve);
    }

    @Override
    public void mouseEntered(MouseEvent e){        
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        for(Plottable p:baseComponents.getElements()){
            if(p instanceof DynamicPlottable){
                ((DynamicPlottable)p).mouseEntered(mve);
            }
        }
        visometry.mouseEntered(mve);
    }

    @Override
    public void mouseExited(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        for(Plottable p:baseComponents.getElements()){
            if(p instanceof DynamicPlottable){
                ((DynamicPlottable)p).mouseExited(mve);
            }
        }
        visometry.mouseExited(mve);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        if(e!=null){
            visometry.mouseWheelMoved(e);
        }
    }


    // STATIC METHODS

    /** Returns box of sliders for style of associated plottables. */
    public static SliderBox getStyleAdjusters(int x,int y,int girth,Collection<Plottable> cp,JPanel snapPanel){
        SliderBox result=new SliderBox(x,y,girth);
        for(Plottable p:cp){
            result.add(p.getStyleSlider(0,0));
        }
        result.adjustBounds();
        result.performLayout();
        if(snapPanel!=null){
            result.enableSnapping(snapPanel, 3, 6);
        }
        return result;
    }
}
