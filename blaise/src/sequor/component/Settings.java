/*
 * Settings.java
 * Created on Sep 6, 2007, 9:51:00 AM
 */
package sequor.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import sequor.model.*;

/**
 * <p>
 * The <b>Settings</B> class is a tool for easily constructing a panel of adjustable options in a GUI. It is intended
 * to streamline the often complicated process of handling event listening for such GUI elements. These options
 * will appear, one in each row, as a list of possible changes which can be made. The <b>Settings</b> class supports
 * several different kinds of options which can be added, featuring editors such as sliders, spinners, textfields,
 * comboboxes, and more. Eventually it should get to the point where it supports lots of different custom editors.
 * </p>
 * <p>
 * Much of this class consists of event handling routines to eliminate repetitious code. Any class which implements
 * this one does however need to do the following for each added property:
 * <ol>
 *   <li>Implement the property a model which generates checks the values and change events
 *   <li>Implement normal get/set methods for the property
 *   <li>Use the <i>addProperty</i> method to add the property along with its string, editor, and type code
 * </ol>
 * The supported type codes are as follows:
 * <ul>
 *   <li> EDIT_BOOLEAN (boolean as a checkbox)
 *   <li> EDIT_COLOR (creates a button to change the color)
 *   <li> EDIT_COMBO (combobox; requires a comboboxmodel)
 *   <li> EDIT_DOUBLE (double as a spinner)
 *   <li> EDIT_FUNCTION (function as a textfield)
 *   <li> EDIT_INTEGER (integer as a spinner)
 *   <li> EDIT_PARAMETRIC (parametric equation)
 *   <li> EDIT_SEPARATOR (separator bar... no property being edited)
 *   <li> EDIT_STRING (simple textfield)
 *   <li> NO_EDIT (does not appear in the panel)
 * </ul>
 * </p>
 * <p>
 * <b>PropertyChangeEvent</b>'s are automatically generated using the string from the property, and fired
 * to any classes which listen for property changes from this one.
 * </p>
 * <p>
 * If a GUI is required, a panel can be automatically generated using the underlying models
 * and a standard set of editors. The panel uses a <b>SpringLayout</b> to create two columns... one
 * with text labels and the other with the editing objects. Eventually this may be implemented as a table
 * rather than a <b>SpringLayout</b> (to be decided at a later date). For further functionality (including a tree),
 * the user may generate a <b>SettingsPanel</b> instead.
 * </p>
 * <p>
 * There is also support for inheriting settings from one class to another. Besides the list of <b>FiresChangeEvent</b>s
 * which are enclosed within the settings class, there is a list of <b>Settings</b> classes, which can be thought of as
 * "parent" classes to this one. In this way, all changes made to the parent class will descend to this sub-class.
 * </p>
 * @author Elisha Peterson
 */
public class Settings extends Vector<SettingsProperty> implements ChangeListener, PropertyChangeListener {

    // PROPERTIES
    /** Name of this collection of settings. */
    private String name="SettingsGroup";
    /** Parent classes. */
    private Vector<Settings> parents;
    /** Child classes. */
    private HashMap<Settings,Integer> children;
    
    /** A relationship between Settings classes whose properties are completely independent. */
    public static final int PROPERTY_INDEPENDENT=0;
    /** A relationship between Settings classes whereby the properties of the parent are copied
     * to the child, and changes to the parent propagate to the child. Changes do not propagate upwards.
     */
    public static final int PROPERTY_PROPAGATE=1;
    /** A relationship between Settings classes whereby the properties of the child are directly linked
     * to the properties of the parent. They are not stored by the child at all.
     */
    public static final int PROPERTY_INHERIT=2;
    
    
    // PROPERTY HANDLING SUPPORT
    /** Adds a listening property which cannot be edited */
    public static final int NO_EDIT = 0;
    /** Adds a spinner with double editing */
    public static final int EDIT_DOUBLE = 1;
    /** Adds a spinner with integer editing */
    public static final int EDIT_INTEGER = 2;
    /** Adds a combo box with several strings (okay for menu) */
    public static final int EDIT_COMBO = 3;
    /** Adds a text field with string editing */
    public static final int EDIT_STRING = 4;
    /** Adds a checkbox for boolean editing (okay for menu) */
    public static final int EDIT_BOOLEAN = 6;
    /** Adds field for function editing */
    public static final int EDIT_FUNCTION = 101;
    /** Adds two fields for parametric function editing */
    public static final int EDIT_PARAMETRIC = 102;
    /** Adds a button for editing a more general object */
    public static final int EDIT_COLOR = 201;
    /** Adds a button for editing a more general object */
    public static final int EDIT_PARAMETER = 202;
    /** Adds a separator */
    public static final int EDIT_SEPARATOR = 1000;
    
    
    // CONSTRUCTORS
    public Settings() {
        super();
        parents = new Vector<Settings>();
        children = new HashMap<Settings,Integer>();
    }
    
    
    // BEAN PATTERNS
    @Override
    public String toString() {return name;}
    public void setName(String name) {this.name = name;}
    public HashMap<Settings,Integer> getChildren(){return children;}
    public void removeChild(Settings s){children.remove(s);}
    public Color getColor(){return Color.BLACK;}
    
    // ADDING PROPERTIES    
    public static final SettingsProperty PROPERTY_SEPARATOR = new SettingsProperty(null, null, EDIT_SEPARATOR);

    /** Adds a property */
    @Override
    public boolean add(SettingsProperty sp){
        boolean result=super.add(sp);
        if(sp.getModel()!=null){
            sp.getModel().addChangeListener(this);
        }
        return result;
    }
    
    /** Adds a property */
    public void addProperty(String s,FiresChangeEvents e,int type){add(new SettingsProperty(s,e,type));}
    /** Adds a property with tooltip text */
    public void addProperty(String s,FiresChangeEvents e,int type,String tooltipText){add(new SettingsProperty(s,e,type,tooltipText));}
    /** Adds a separator */
    public void addSeparator(){add(PROPERTY_SEPARATOR);}

    /** Changes type of editor of a particular property, chosen by name */
    public void setPropertyEditor(String s, int newType) {
        for (SettingsProperty sp : this) {
            if (sp.getName() == null) {
                continue;
            }
            if (sp.getName().equals(s)) {
                sp.setEditorType(newType);
            }
        }
    }


    // INHERITANCE ELEMENTS
    /** Adds a child node, and implements appropriate event handling procedures. */
    public void addChild(Settings child,int relationship){
        children.put(child,relationship);
        //child.addParent(this,relationship);
        switch(relationship){
            case PROPERTY_PROPAGATE : // copy parent's properties to this class
                child.copySettingsFrom(this);
            case PROPERTY_INHERIT : // allow for "lookup" of parent properties
                child.addLookupParent(this);
                break;
            case PROPERTY_INDEPENDENT : // no event handling required
            default :
                break;
        }
    }
    /** Copies settings from another class; adds event handling procedures. */
    public void copySettingsFrom(Settings s){
        for(SettingsProperty sp:s){
            add(sp.getDescendant());
        }
    }
    /** Adds a parent class from which this class may look up settings. */
    public void addLookupParent(Settings s){
        parents.add(s);
    }
    /** Returns list of lookup parents. */
    public Vector<Settings> getLookupParents(){
        return parents;
    }
    
    // EVENT HANDLING SUPPORT
    /** Sets up event listening. Should be called by the constructor!! */
    public void initEventListening() {
        for (SettingsProperty sp : this) {
            if (sp.getModel() == null) {
                continue;
            }
            sp.getModel().addChangeListener(this);
        }
    }
    /**This should pass state changes to pcs. */
    @Override
    public void stateChanged(ChangeEvent e) {
        for (SettingsProperty sp : this) {
            if (sp.getModel() == null) {
                continue;
            }
            if (e.getSource().equals(sp.getModel())) {
                propertyChange(sp.getModel().getChangeEvent(sp.getName()));
            }
        }
    }
    /** Utility class for handling bean property changes. */
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /**Add a property change listener for a specific property. */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    /**Remove a property change listener for a specific property. */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    /** Handles property change events fired from a few properties */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        pcs.firePropertyChange(e);
    }

    
    // GUI METHODS
    
    /** Generates a JMenu under the title of this Settings class containing everything of a certain edit type. */
    public JMenu getMenu(String s) {
        JMenu result=new JMenu(s==null?name:s);
        for(SettingsProperty sp:this){
            switch(sp.getEditorType()){
                case EDIT_COMBO : 
                    result.add(getMenuItem(sp.getName(),(ComboBoxRangeModel)sp.getModel()));
                    break;
                case EDIT_COLOR :
                    if (sp.getModel() instanceof ColorModel){
                        result.add(getMenuItem(sp.getName(),(ColorModel)sp.getModel()));
                    }
                    break;
                default :
                    break;
            }
        }
        return result;
    }
    
    /** Places the settings on a given panel, and performs proper layout. */
    public JPanel initPanel(JPanel jp){
        if(jp==null){
            jp=new JPanel();
        }else{
            jp.removeAll();
        }
        jp.setLayout(new SpringLayout());   
        
        // adds all the elements defined in this class
        int numComponents = size();
        for (SettingsProperty sp : this) {
            if (sp.getEditorType()==NO_EDIT){
                numComponents--;
                continue;
            }
            if (sp.getEditorType()==EDIT_SEPARATOR){
                jp.add(new JSeparator());
                jp.add(new JSeparator());
                continue;
            }
            JLabel label=new JLabel(sp.getName());
            JComponent component = null;
            switch (sp.getEditorType()) {
                case EDIT_DOUBLE:
                    component=getSpinner((DoubleRangeModel) sp.getModel());
                    break;
                case EDIT_INTEGER:
                    component=getSpinner((IntegerRangeModel) sp.getModel());
                    break;
                case EDIT_COMBO:
                    component=getComboBox((ComboBoxRangeModel) sp.getModel());
                    break;
                case EDIT_STRING:
                    component=new JTextField();
                    break;
                case EDIT_COLOR:
                    component=new ColorEditor((ColorModel) sp.getModel()).getButton();
                    break;
                case EDIT_PARAMETER:
                    component=new ParameterEditor((ParameterListModel)sp.getModel()).getButton();
                    break;
                case EDIT_FUNCTION:
                    component=new FunctionTextComboBox((FunctionTreeModel)sp.getModel());
                    break;
                case EDIT_PARAMETRIC:
                    component=new BParametricFunctionPanel((ParametricModel) sp.getModel());
                    break;
                }
            label.setToolTipText(sp.getTooltipText());
            jp.add(label);
            if(component!=null){
                component.setToolTipText(sp.getTooltipText());
                jp.add(component);
            }
        }
        SpringUtilities.makeCompactGrid(jp, numComponents, 2, 5, 5, 5, 5);
        jp.setToolTipText(name);
        jp.setName(name);
        jp.validate();
        return jp;
    }
    
    /** Generates a JPanel with the settings contained herein.
     * @return JPanel with all the visible parameters.
     */
    public JPanel getPanel() { return initPanel(null); }

    /** Generates a JSplitPane with a tree on the left and settings on the right. */
    public JSplitPane getSplitPanel() {
        return new SettingsPanel(this);
    }

    /** Generates tree node. */
    public DefaultMutableTreeNode getTreeNode(){
        DefaultMutableTreeNode result=new DefaultMutableTreeNode(this);
        for(Settings s:children.keySet()){result.add(s.getTreeNode());}
        return result;
    }
    
    /** Generates tree model of child settings. */
    public DefaultTreeModel getTreeModel() {
        return new DefaultTreeModel(getTreeNode());
    }
    
    

    // GUI HANDLING SUPPORT
    
    /** Generates a combo box given a string list. */
    public static JComboBox getComboBox(ComboBoxRangeModel cbrm) {
        JComboBox result = new JComboBox(new ComboBoxEditor(cbrm));
        result.setMinimumSize(new Dimension(50, 20));
        result.setPreferredSize(new Dimension(50, 25));
        result.setMaximumSize(new Dimension(200, 25));
        return result;
    }

    /** Generates a spinner given a range model and a step size. */
    public static JSpinner getSpinner(DoubleRangeModel drm) {
        JSpinner result = new JSpinner(new SpinnerDoubleEditor(drm));
        result.setMinimumSize(new Dimension(20, 20));
        result.setPreferredSize(new Dimension(50, 25));
        result.setMaximumSize(new Dimension(50, 25));
        return result;
    }

    public static JSpinner getSpinner(IntegerRangeModel irm) {
        JSpinner result = new JSpinner(new SpinnerIntegerEditor(irm));
        result.setMinimumSize(new Dimension(20, 20));
        result.setPreferredSize(new Dimension(50, 25));
        result.setMaximumSize(new Dimension(50, 25));
        return result;
    }
    
    public static JSlider getSlider(IntegerRangeModel irm) {
        JSlider result = new JSlider(new SliderIntegerEditor(irm));
        result.setPaintTicks(true);
        result.setPaintLabels(true);
        result.setMinimumSize(new Dimension(20, 20));
        result.setPreferredSize(new Dimension(50, 25));
        result.setMaximumSize(new Dimension(50, 25));
        return result;
    }
    
    public static JSlider getSlider(DoubleRangeModel irm) {
        JSlider result = new JSlider(new SliderDoubleEditor(irm));
        result.setPaintTicks(true);
        result.setPaintLabels(true);
        result.setMinimumSize(new Dimension(20, 20));
        result.setPreferredSize(new Dimension(50, 25));
        result.setMaximumSize(new Dimension(50, 25));
        return result;
    }

    public static JMenuItem getMenuItem(String name,final ComboBoxRangeModel cbrm){
        // TODO test this method; really not sure if it works
        JMenu subMenu=new JMenu(name);
        ButtonGroup group=new ButtonGroup();
        for(int i=cbrm.getMinimum();i<=cbrm.getMaximum();i++){
            final int j=i;
            JRadioButtonMenuItem item=new JRadioButtonMenuItem(cbrm.getString(i));
            item.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbrm.setValue(j);
                }                
            });
            subMenu.add(item);
            group.add(item);
            if(i==cbrm.getValue()){item.setSelected(true);}
        }
        return subMenu;
    }
    
    public static JMenuItem getMenuItem(final String name,final ColorModel cm){
        // TODO test this method; really not sure if it's going to work. 
        final JMenuItem menuItem=new JMenuItem(name);
        menuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JColorChooser colorChooser=new JColorChooser();
                if (name.equals(e.getActionCommand())){
                    colorChooser.setColor(cm.getValue());
                    JDialog dialog = JColorChooser.createDialog(menuItem,"Pick a Color",true,colorChooser,this,null);
                    dialog.setVisible(true);
                }else{
                    cm.setValue(colorChooser.getColor());}
                }
        });
        return menuItem;
    }
    
    public static class Default extends Settings {
        public Default(){
            super();
            //addProperty("boolean",null,Settings.EDIT_BOOLEAN);
            addProperty("color",new ColorModel(Color.BLUE),Settings.EDIT_COLOR);
            addProperty("combo",new ComboBoxRangeModel(),Settings.EDIT_COMBO);
            addProperty("double",new DoubleRangeModel(1.0,-10.0,100.0,0.1),Settings.EDIT_DOUBLE);
            addProperty("function",new FunctionTreeModel(),Settings.EDIT_FUNCTION);
            addProperty("integer",new IntegerRangeModel(1,-10,100,1),Settings.EDIT_INTEGER);
            addProperty("parameter",new ParameterListModel(),Settings.EDIT_PARAMETER);
            addProperty("parametric",new ParametricModel(),Settings.EDIT_PARAMETRIC);
            //addProperty("separator",null,Settings.EDIT_SEPARATOR);
            //addProperty("string",null,Settings.EDIT_STRING);
            //addProperty("noedit",null,Settings.NO_EDIT);
        }
    }
}
