/*
 * PlotPanel.java
 * Created on Sep 14, 2007, 7:50:15 AM
 */

// TODO Require "toString" method for Plottable's

package specto;

import sequor.component.RangeTimer;
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
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.event.MouseVisometryEvent;

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
    private V visometry;
    /** Contains all the basic components. */    
    private PlottableGroup<V> basicComponents;
    /** Contains components which receive mouse input. */
    private PlottableGroup<V> dynamicComponents;
    /** Contains the underlying grid component. */
    //private DynamicPlottable<V> gridComponent;
    
    /** Timer object for animations. */
    private RangeTimer timer;
    
    /** Static object drawing pane. */
    private PlotLayer<V> staticPlottables;
    /** Animating object drawing pane. */
    private PlotLayer<V> animatePlottables;
    
    /** The context menu. */
    private JPopupMenu contextMenu;
    /** The options sub-menu of the context menu. */
    private JMenu optionsMenu;
    
    /** Layout elements. */
    private JToolBar toolBar;
    private JPanel propertyPanel;
    private JPanel inputPanel;
    private JPanel statusPanel;
    private JPanel mainPanel;
    
    /** Determines whether all components should be redrawn. */
    private boolean refresh=false;
        
    // CONSTRUCTORS
    
    public PlotPanel(){}
    public PlotPanel(V visometry){
        super();
        initComponents(visometry);
        initLayout();
    }
    
    
    // INITIALIZERS
    
    private void initComponents(V visometry){
        timer=new RangeTimer(this);
        initContextMenu();
        setVisometry(visometry);
        basicComponents=new PlottableGroup<V>();
        dynamicComponents=new PlottableGroup<V>();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        staticPlottables=new PlotLayer<V>(this);
        animatePlottables=new PlotLayer<V>(this);
        add(staticPlottables);
        add(animatePlottables);
        animatePlottables.setAnimates(true);
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
        optionsMenu=new JMenu("Options");
        contextMenu.add(optionsMenu);
        contextMenu.addSeparator();
        contextMenu.add(timer.getMenuItems().get(0));
        setComponentPopupMenu(contextMenu);        
    }
    
    
    // BEAN PATTERNS: GETTERS & SETTERS
    
    public V getVisometry(){return visometry;}
    public RangeTimer getTimer(){return timer;}
    
    public void setVisometry(V newValue){
        visometry=newValue;
        visometry.initContainer(this);
        addComponentListener(visometry);
        addToContextMenu(visometry.getMenuItems());
    }
    
    public Collection<Plottable<V>> getBasicPlottables(){return basicComponents.getElements();}
    public Collection<Plottable<V>> getDynamicPlottables(){return dynamicComponents.getElements();}
        
        
    
    
    // COMPONENT HANDLING
        
   public void addToContextMenu(Vector<JMenuItem> mis){
       getComponentPopupMenu().addSeparator();
       for(JMenuItem mi:mis){
            if(mi==null){getComponentPopupMenu().addSeparator();}
            else{getComponentPopupMenu().add(mi);}
       }
   }
    
    public void add(Plottable<V> pv){
        pv.addChangeListener(this);
        if(pv instanceof PlottableGroup){
            if(pv.getOptionsMenu()!=null){optionsMenu.add(pv.getOptionsMenu());}
            dynamicComponents.add(pv);
            animatePlottables.add(pv);            
        }else{
            if(pv.getOptionsMenu()!=null){
                optionsMenu.add(pv.getOptionsMenu());
            }
            if(pv instanceof DynamicPlottable){
                dynamicComponents.add(pv);
            }else{
                basicComponents.add(pv);
            }
            if(pv instanceof Animatable){
                animatePlottables.add(pv);
            }else{
                staticPlottables.add(pv);
            }
        }
    }
    public <T extends Plottable<V>> void addAll(Collection<T> cpv){for(T pv:cpv){add(pv);}}
    public void remove(Plottable<V> pv){
        //TODO remove menu items here as well!!
        if(pv instanceof DynamicPlottable){
            dynamicComponents.remove(pv);
        }else{
            basicComponents.remove(pv);
        }
        if(pv instanceof Animatable){
            animatePlottables.remove(pv);
        }else{
            staticPlottables.remove(pv);
        }
        pv.removeChangeListener(this);
    }
    public void removeAll(){
        basicComponents.clear();
        dynamicComponents.clear();
        animatePlottables.clear();
        staticPlottables.clear();
        optionsMenu.removeAll();
        contextMenu.removeAll();
        contextMenu.add(optionsMenu);
        contextMenu.addSeparator();
        contextMenu.add(timer.getMenuItems().get(0));
        addToContextMenu(visometry.getMenuItems());
    }
    public <T extends Plottable<V>> void removeAll(Collection<T> cpv){for(T pv:cpv){remove(pv);}}
    public void rebuildOptionsMenu(){
        optionsMenu.removeAll();
        for(Plottable p:basicComponents.getElements()){
            optionsMenu.add(p.getOptionsMenu());
        }
        for(Plottable p:dynamicComponents.getElements()){
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
        if(refresh){
            staticPlottables.recompute();
            animatePlottables.recompute();
            refresh=false;
        }
        else if(timer!=null&&timer.isNotStopped()){
            animatePlottables.recompute();
        }
        staticPlottables.paintComponent(g);
        if(timer!=null&&timer.isNotStopped()){            
            animatePlottables.paintComponent(g,timer);
        }else{
            animatePlottables.paintComponent(g);
        }
    }
    
    
    // EVENT HANDLING
    
    @Override
    public void stateChanged(ChangeEvent e){
        refresh=true;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(timer)){
            repaint();
        }
    }
    
    
    // MOUSE EVENT HANDLING
    
    DynamicPlottable<V> mover;
    
    Vector<DynamicPlottable<V>> getHits(MouseVisometryEvent mve){
        Vector<DynamicPlottable<V>> result=new Vector<DynamicPlottable<V>>();
        for (Plottable<V> dp:dynamicComponents.getElements()){
            if(((DynamicPlottable<V>)dp).clicked(mve)){
                result.add((DynamicPlottable<V>)dp);
            }
        }
        return result;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        Vector<DynamicPlottable<V>> hits=getHits(mve);
        if(hits.isEmpty()){visometry.mouseClicked(mve);}
        else{hits.get(0).mouseClicked(mve);}
    }

    @Override
    public void mousePressed(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        Vector<DynamicPlottable<V>> hits=getHits(mve);
        if(hits.isEmpty()){visometry.mousePressed(mve);}
        else{mover=hits.get(0);mover.mousePressed(mve);}
    }

    @Override
    public void mouseDragged(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        if(mover!=null){mover.mouseDragged(mve);}
        else{visometry.mouseDragged(mve);}
    }

    @Override
    public void mouseReleased(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        if(mover!=null){mover.mouseReleased(mve);mover=null;}
        else{visometry.mouseReleased(mve);}
    }

    @Override
    public void mouseMoved(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        visometry.mouseMoved(mve);
    }

    @Override
    public void mouseEntered(MouseEvent e){        
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        visometry.mouseEntered(mve);
    }

    @Override
    public void mouseExited(MouseEvent e){
        MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
        visometry.mouseExited(mve);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        if(e!=null){
            MouseVisometryEvent mve=new MouseVisometryEvent(e,visometry);
            visometry.mouseWheelMoved(e);
        }
    }
}
