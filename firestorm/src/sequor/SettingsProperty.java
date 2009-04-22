/*
 * SettingsParameter.java
 * Created on Feb 15, 2008
 */

package sequor;

import java.awt.Component;
import javax.swing.JLabel;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;

/**
 * <b>SettingsParameter</b> is a class which stores a property with a specified name
 * and an <b>int</b> specifying the default editor to be used. Also states whether
 * the element may be edited, and whether it should be displayed in a list of settings.
 * 
 * @author Elisha Peterson
 */
public class SettingsProperty {

    /** The label attached to the settings. */
    String name;
    /** Special tooltip text to use (if any). */
    String tooltipText;
    /** The type of editor associated with the data model. */   
    int editorType;
    /** The underlying data model. */
    FiresChangeEvents model;
    
    /** Flag specifying whether data can be edited. */
    @XmlAttribute boolean editable;
    /** Flag specifying whether data is displayed. */
    @XmlAttribute boolean displayed;

    
    // CONSTRUCTORS
    
    /** No-Arg Constructor (for JavaBeans) */
    public SettingsProperty(){
        this(null,null,-1);
    }
    
    /** Partial constructor. Defaults to no tooltip, but is editable and displayed.
     * @param name The label used for the property
     * @param model The data model
     * @param editorType Flag representing which editor to use
     */
    public SettingsProperty(String name, FiresChangeEvents model, int editorType) {
        this(name, model, editorType, null, true, true);
    }
       
    /** Partial constructor. Defaults to no tooltip, but is editable and displayed.
     * @param name The label used for the property
     * @param model The data model
     * @param editorType Flag representing which editor to use
     * @param tooltipText Tooltip text (may be null)
     */
    public SettingsProperty(String name, FiresChangeEvents model, int editorType, String tooltipText) {
        this(name, model, editorType, tooltipText, true, true);
    } 
    
    /** Full constructor.
     * @param name The label used for the property
     * @param model The data model
     * @param editorType Flag representing which editor to use
     * @param tooltipText Tooltip text (may be null)
     * @param editable Whether the property can be edited
     * @param displayed Whether the property will be displayed in a group of properties
     */
    public SettingsProperty(String name, FiresChangeEvents model, int editorType, String tooltipText, boolean editable, boolean displayed) {
        this.name = name;
        this.model = model;
        this.editorType = editorType;
        this.tooltipText = tooltipText;
    }
    
    
    // BEAN PATTERNS
    
    @XmlAttribute 
    public int getEditorType() { return editorType; }
    public void setEditorType(int editorType) { this.editorType = editorType; }

    @XmlTransient
    public FiresChangeEvents getModel() { return model; }
    public void setModel(FiresChangeEvents model) {
        if (model != null && model != this.model) {
            this.model = model;
        }
    }

    @XmlAttribute
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @XmlAttribute
    public String getTooltipText() { return tooltipText; }
    public void setTooltipText(String tooltipText) {this.tooltipText = tooltipText;}
    
    
    // LAYOUT METHODS
    
    /** Returns number of rows required to display the property. */
    public int getNumRows(){
        return displayed ? 1 : 0;
    }
    
    
    // INHERITANCE METHODS
    
    /** Copies values to another class and adds listening capabilities. Any changes made to this data model will
     * be reflected in the descendant, but changes in the descendant will not reflect here.
     * @return A descendant property
     */
    public SettingsProperty getDescendant(){ 
        return new SettingsProperty(name, model.getDescendant(), editorType, tooltipText, editable, displayed);
    }
    
    /** Creates a clone copy of this property (no relationship maintained between the two properties). */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new SettingsProperty(name, model.clone(), editorType, tooltipText, editable, displayed);
    }
    
    
    // COMPONENTS OF THIS METHOD

    public Component getDefaultEditor() {
        return SettingsFactory.getEditor(this);
    }

    public Component getRenderer() {
        return getDefaultEditor();
    }
    
    public Component getLabel() {
        JLabel result = new JLabel(getName());
        result.setHorizontalAlignment(JLabel.RIGHT);
        return result;
    }
}
