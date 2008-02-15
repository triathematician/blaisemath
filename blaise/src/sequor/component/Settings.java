/*
 * Settings.java
 * Created on Sep 6, 2007, 9:51:00 AM
 */
package sequor.component;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
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
public abstract class Settings extends Vector<SettingsProperty> implements ChangeListener, PropertyChangeListener {

    // PROPERTIES
    /** Name of this collection of settings. */
    private String name="SettingsGroup";
    /** Parent classes. */
    private Vector<Settings> parents;
    /** Child classes. */
    private Vector<Settings> children;
    
    
    // PROPERTY HANDLING SUPPORT
    /** Adds a listening property which cannot be edited */
    public static final int NO_EDIT = 0;
    /** Adds a spinner with double editing */
    public static final int EDIT_DOUBLE = 1;
    /** Adds a spinner with integer editing */
    public static final int EDIT_INTEGER = 2;
    /** Adds a combo box with several strings */
    public static final int EDIT_COMBO = 3;
    /** Adds a text field with string editing */
    public static final int EDIT_STRING = 4;
    /** Adds a button for color editing */
    public static final int EDIT_COLOR = 5;
    /** Adds a checkbox for boolean editing */
    public static final int EDIT_BOOLEAN = 6;
    /** Adds field for function editing */
    public static final int EDIT_FUNCTION = 9;
    /** Adds two fields for parametric function editing */
    public static final int EDIT_PARAMETRIC = 10;
    /** Adds a separator */
    public static final int EDIT_SEPARATOR = 99;


    // BEAN PATTERNS
    
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    
    
    // CONSTRUCTORS
    public Settings() {
        super();
        parents = new Vector<Settings>();
        children = new Vector<Settings>();
    }
    // ADDING PROPERTIES    
    public static SettingsProperty PROPERTY_SEPARATOR = new SettingsProperty(null, null, EDIT_SEPARATOR);

    /** Adds a separator */
    public void addSeparator() {
        add(PROPERTY_SEPARATOR);
    }

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

    // EVENT HANDLING SUPPORT
    /** Sets up event listening. Should be called by the constructor!! */
    protected void initEventListening() {
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
    /** Generates a JPanel with the settings contained herein.
     * @return JPanel with all the visible parameters.
     */
    public JPanel getPanel() {
        JPanel result = new JPanel(new SpringLayout());
        
        // adds all the elements defined in this class
        int numComponents = size();
        for (SettingsProperty sp : this) {
            switch (sp.getEditorType()) {
                case NO_EDIT:
                    numComponents--;
                    break;
                case EDIT_SEPARATOR:
                    result.add(new JSeparator());
                    result.add(new JSeparator());
                    break;
                case EDIT_DOUBLE:
                    result.add(new JLabel(sp.getName()));
                    result.add(getSpinner((DoubleRangeModel) sp.getModel()));
                    break;
                case EDIT_INTEGER:
                    result.add(new JLabel(sp.getName()));
                    result.add(getSpinner((IntegerRangeModel) sp.getModel()));
                    break;
                case EDIT_COMBO:
                    result.add(new JLabel(sp.getName()));
                    result.add(getComboBox((ComboBoxRangeModel) sp.getModel()));
                    break;
                case EDIT_STRING:
                    result.add(new JLabel(sp.getName()));
                    result.add(new JTextField());
                    break;
                case EDIT_COLOR:
                    result.add(new JLabel(sp.getName()));
                    result.add(new ColorEditor((ColorModel) sp.getModel()).getButton());
                    break;
                case EDIT_PARAMETRIC:
                    result.add(new JLabel(sp.getName()));
                    result.add(new BParametricFunctionPanel((ParametricModel) sp.getModel()));
                    break;
                }
        }
        SpringUtilities.makeCompactGrid(result, numComponents, 2, 5, 5, 5, 5);
        result.setToolTipText(name);
        result.setName(name);
        return result;
    }

    /** Generates a JSplitPane with a tree on the left and settings on the right. */
    public JSplitPane getSplitPanel() {
        return new SettingsPanel(this);
    }

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
}
