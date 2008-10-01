/**
 * SettingsFactory.java
 * Created on Jun 12, 2008
 */

package sequor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import sequor.component.DoubleSlider;
import sequor.editor.BParametricFunctionPanel;
import sequor.editor.ColorEditor;
import sequor.editor.FunctionTextComboBox;
import sequor.editor.ParameterEditor;
import sequor.editor.SliderIntegerEditor;
import sequor.editor.SpinnerDoubleEditor;
import sequor.editor.SpinnerIntegerEditor;
import sequor.model.BooleanModel;
import sequor.model.ColorModel;
import sequor.model.DoubleRangeModel;
import sequor.model.FunctionTreeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.ParameterListModel;
import sequor.model.ParametricModel;
import sequor.model.StringRangeModel;

/**
 * Contains elements for looking up editors/renderers for particular settings.
 * 
 * @author Elisha Peterson
 */
public class SettingsFactory {
    
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
    
    /** Gets component corresponding to a particular SettingsProperty. */
    public static JComponent getEditor(SettingsProperty sp){
        switch(sp.getEditorType()){
            case EDIT_BOOLEAN:
                return ((BooleanModel) sp.getModel()).getCheckBox();
            case EDIT_COLOR:
                return new ColorEditor((ColorModel) sp.getModel()).getButton();
            case EDIT_COMBO:
                return ((StringRangeModel) sp.getModel()).getComboBox();
            case EDIT_DOUBLE:
                return getSpinner((DoubleRangeModel)sp.getModel());
            case EDIT_DOUBLE_SLIDER:
                return new DoubleSlider((DoubleRangeModel)sp.getModel());
            case EDIT_FUNCTION:
                return new FunctionTextComboBox((FunctionTreeModel)sp.getModel());
            case EDIT_INTEGER:
                return getSpinner((IntegerRangeModel)sp.getModel());
            case EDIT_INTEGER_SLIDER:
                return getSlider((IntegerRangeModel) sp.getModel());
            case EDIT_PARAMETER:
                return new ParameterEditor((ParameterListModel)sp.getModel()).getButton();
            case EDIT_PARAMETRIC:
                return new BParametricFunctionPanel((ParametricModel) sp.getModel());
            case EDIT_STRING:
                return new JTextField();
        }     
        return null;
    }
    
    // GUI HANDLING SUPPORT

    /** Generates a spinner given a range model and a step size. */
    public static JSpinner getSpinner(DoubleRangeModel drm) {
        JSpinner result = new JSpinner(new SpinnerDoubleEditor(drm));
        //result.setMinimumSize(new Dimension(20, 20));
        //result.setPreferredSize(new Dimension(50, 25));
        //result.setMaximumSize(new Dimension(50, 25));
        return result;
    }

    public static JSpinner getSpinner(IntegerRangeModel irm) {
        JSpinner result = new JSpinner(new SpinnerIntegerEditor(irm));
        //result.setMinimumSize(new Dimension(20, 20));
        //result.setPreferredSize(new Dimension(50, 25));
        //result.setMaximumSize(new Dimension(50, 25));
        return result;
    }
    
    public static JSlider getSlider(IntegerRangeModel irm) {
        JSlider result = new JSlider(new SliderIntegerEditor(irm));
        result.setMajorTickSpacing(10);
        result.setMinorTickSpacing(5);
        result.setPaintTicks(true);
        result.setPaintLabels(true);
        //result.setMinimumSize(new Dimension(20, 20));
        //result.setPreferredSize(new Dimension(100, 25));
        //result.setMaximumSize(new Dimension(100, 25));
        return result;
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
}
