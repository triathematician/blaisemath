/*
 * Settings.java
 * Created on Sep 6, 2007, 9:51:00 AM
 */
package sequor;

import sequor.component.*;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
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
 * the user may generate a <b>SettingsTreePanel</b> instead.
 * </p>
 * <p>
 * There is also support for inheriting settings from one class to another. Besides the list of <b>FiresChangeEvent</b>s
 * which are enclosed within the settings class, there is a list of <b>Settings</b> classes, which can be thought of as
 * "parent" classes to this one. In this way, all changes made to the parent class will descend to this sub-class.
 * </p>
 * @author Elisha Peterson
 */
@XmlRootElement
public class Settings extends Vector<SettingsProperty> implements ChangeListener, PropertyChangeListener {

    // PROPERTIES
    
    /** Name of this collection of settings. */
    private String name="SettingsGroup";
    /** Type of inheritance */
    private Integer inheriting = PROPERTY_INDEPENDENT;
    /** Parent classes. */
    private Vector<Settings> parents;
    /** Child classes. */
    @XmlElement private Vector<Settings> children;
    
    // CONSTANTS
    
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
    /** Adds a slider with double editing */
    public static final int EDIT_DOUBLE_SLIDER = 3;
    /** Adds a slider with integer editing */
    public static final int EDIT_INTEGER_SLIDER = 4;
    /** Adds a combo box with several strings (okay for menu) */
    public static final int EDIT_COMBO = 5;
    /** Adds a text field with string editing */
    public static final int EDIT_STRING = 6;
    /** Adds a checkbox for boolean editing (okay for menu) */
    public static final int EDIT_BOOLEAN = 7;
    /** Adds group of boolean elements for editing (converts to several checkboxes). */
    public static final int EDIT_BOOLEAN_GROUP = 77;
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
        children = new Vector<Settings>();
    }

    public Settings(String string) {
        this();
        setName(string);
    }
    
    
    // BEAN PATTERNS
    
    @Override
    public String toString() {return name;}
    
    @XmlAttribute
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    
    public Vector<Settings> getChildren(){return children;}
    public void removeChild(Settings s){children.remove(s);}
    
    public Color getColor(){return Color.BLACK;}
    
    @XmlAttribute
    public Integer getInheriting(){return inheriting;}
    public void setInheriting(Integer inheriting){this.inheriting = inheriting;}
    
    /** Returns the total number of rows required to display the setting. */
    public int getNumRows(){
        int result = 0;
        for(SettingsProperty sp : this) {
            result += sp.getNumRows();
        }
        return result;
    }
    /** Returns the total number of rows, excluding separators. */
    public int getNumDataRows(){
        int result = 0;
        for(SettingsProperty sp : this) {
            if (sp.getEditorType() == EDIT_SEPARATOR) { continue; }
            result += sp.getNumRows();
        }
        return result;
    }
    
    /** Returns list of properties. */
    @XmlElement(name="property")
    public Vector<SettingsProperty> getProperties(){ return (Vector<SettingsProperty>)this; }
    public void setProperties(Vector<SettingsProperty> sps){
        for(SettingsProperty sp:this){remove(sp);}
        for(SettingsProperty sp:sps){add(sp);}
    }
    
    
    // METHODS FOR ADDING PROPERTIES    
    
    /** Static property representing a horizontal bar or separator of some sort. */
    public static final SettingsProperty PROPERTY_SEPARATOR = new SettingsProperty(null, null, EDIT_SEPARATOR);

    /** Adds a separator */
    public void addSeparator(){add(PROPERTY_SEPARATOR);}

    /** Adds group of several properties (Displayed many times, events handled once) */
    public void addGroup(final String evtName, final FiresChangeEvents e, int type, String tooltipText) {
        switch(type) {
            case EDIT_BOOLEAN_GROUP:
                if (e instanceof SubsetModel) {
                    for(Object o : ((SubsetModel)e).getSet()) {
                        super.add(new SettingsProperty(o.toString(), ((SubsetModel)e).getModel(o), EDIT_BOOLEAN, tooltipText));
                    }
                } else {
                    for(BooleanModel bm : ((BooleanModelGroup)e).getModels()) {
                        super.add(new SettingsProperty(bm.toString(), e, EDIT_BOOLEAN, tooltipText));
                    }
                }
                e.addChangeListener(new ChangeListener(){
                    public void stateChanged(ChangeEvent ce) {               
                        propertyChange(e.getChangeEvent(evtName));
                    }                    
                });
            default:
                break;
        }
    }
    
    /** Adds a property */
    public void addProperty(String s,FiresChangeEvents e,int type){addProperty(s,e,type,null);}

    /** Adds a property with tooltip text */
    public void addProperty(String s, FiresChangeEvents e, int type, String tooltipText){
        add(new SettingsProperty(s, e, type, tooltipText));
    }
    
    /** Adds a generic property. Adds this class as a ChangeListener if the model is not null. */
    @Override
    public boolean add(SettingsProperty sp){
        boolean result=super.add(sp);
        if(sp.getModel() != null){
            sp.getModel().addChangeListener(this);
        }
        return result;
    }
    
    /** Removes a generic property. */
    public void remove(SettingsProperty sp){
        if(contains(sp)) {
            super.remove(sp);
            sp.getModel().removeChangeListener(this);
        }
    }
    
    /** Changes type of editor of a particular property which is identified by name */
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
        //if(children.contains(child)){return;}
        children.add(child);
        child.setInheriting(relationship);
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

    
    public void removeAllChildren() {
        children.clear();
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
    
    /** Fires a change event by renaming the ChangeEvent and passing it to the PropertyChangeSupport class. */
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
    public void addPropertyChangeListener(PropertyChangeListener l) {pcs.addPropertyChangeListener(l);}
    /**Remove a property change listener for a specific property. */
    public void removePropertyChangeListener(PropertyChangeListener l) {pcs.removePropertyChangeListener(l);}
    /** Handles property change events fired from a few properties */
    @Override
    public void propertyChange(PropertyChangeEvent e) {pcs.firePropertyChange(e);}
    
    
    // GUI METHODS

    /** Generates a JPanel with the Settings and IRM/DRM displayed as JSpinner's. */
    public JPanel getPanel(){return new SettingsPanel(this,SettingsPanel.DISPLAY_SPINNERS);}    
    /** Generates a JPanel with the Settings and IRM/DRM displayed as JSlider's. */
    public JPanel getSliderPanel(){return new SettingsPanel(this,SettingsPanel.DISPLAY_SLIDERS);}
    /** Generates a JPanel with the Settings displayed in table format. */
    public JPanel getTablePanel(){return new SettingsPanel(this,SettingsPanel.DISPLAY_TABLE);}
    /** Generates a JToolBar with the Settings. */
    public JToolBar getBar(){return new SettingsBar(this);}
    /** Generates a JSplitPane with a tree on the left and settings on the right. */
    public JSplitPane getTreePanel(){return new SettingsTreePanel(this);}
    /** Generates a JDialog with panel display of the settings. */
    public JDialog getDialog(java.awt.Frame parent,boolean modal){return new SettingsDialog(this,parent,modal);}
    /** Generates a JMenu with Color and ComboBox items in this class. */
    public JMenu getMenu(){return new SettingsMenu(this);}

    /** Generates tree node. */
    public DefaultMutableTreeNode getTreeNode(){
        DefaultMutableTreeNode result=new DefaultMutableTreeNode(this);
        for(Settings s:children){result.add(s.getTreeNode());}
        return result;
    }
    
    /** Generates tree model of child settings. */
    public DefaultTreeModel getTreeModel() {
        return new DefaultTreeModel(getTreeNode());
    }
    
    /** Used to generate a default list of displayed properties. */
    public void addDefaultItems(){
        addProperty("color",new ColorModel(Color.BLUE),Settings.EDIT_COLOR);
        addSeparator();
        IntegerRangeModel irm=new IntegerRangeModel(1,-10,100,1);
        addProperty("integer (spinner)",irm,Settings.EDIT_INTEGER);
        addProperty("integer (slider)",irm,Settings.EDIT_INTEGER_SLIDER);
        DoubleRangeModel drm=new DoubleRangeModel(1.,-10.,100.,.1);
        addProperty("double (spinner)",drm,Settings.EDIT_DOUBLE);
        addProperty("double (slider)",drm,Settings.EDIT_DOUBLE_SLIDER);
        addSeparator();
        addProperty("combo",new StringRangeModel(),Settings.EDIT_COMBO);
        addProperty("function",new FunctionTreeModel(),Settings.EDIT_FUNCTION);
        addProperty("parametric",new ParametricModel(),Settings.EDIT_PARAMETRIC);
        //addProperty("parameter",new ParameterListModel(),Settings.EDIT_PARAMETER);
        addProperty("boolean",new BooleanModel(true),Settings.EDIT_BOOLEAN);
            //addProperty("separator",null,Settings.EDIT_SEPARATOR);
            //addProperty("string",null,Settings.EDIT_STRING);
            //addProperty("noedit",null,Settings.NO_EDIT);
    }
    
    public static class Default extends Settings {
        public Default(){
            super();
            addDefaultItems();
        }
    }
}
