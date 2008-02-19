/*
 * SettingsParameter.java
 * Created on Feb 15, 2008
 */

package sequor.model;

/**
 * <p>
 * SettingsParameter is a class which stores a name, a property model, and a code describing the
 * means of editing the parameter. These can be added to the Settings class.
 * </p>
 * @author Elisha Peterson
 */
public class SettingsProperty {

    String name;
    FiresChangeEvents model;
    int editorType;
    String tooltipText;

    public SettingsProperty(String name, FiresChangeEvents model, int editorType) {
        this.name = name;
        this.model = model;
        this.editorType = editorType;
    }
    
    public SettingsProperty(String name, FiresChangeEvents model, int editorType, String tooltipText) {
        this.name = name;
        this.model = model;
        this.editorType = editorType;
        this.tooltipText = tooltipText;
    }
    
    public int getEditorType() {
        return editorType;
    }

    public void setEditorType(int editorType) {
        this.editorType = editorType;
    }

    public FiresChangeEvents getModel() {
        return model;
    }

    public void setModel(FiresChangeEvents model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getTooltipText() {
        return tooltipText;
    }
    
    // INHERITANCE METHODS
    
    /** Copies values to another class and adds listening capabilities */
    public SettingsProperty getDescendant(){        
        FiresChangeEvents newModel=getModel().getDescendant();
        return new SettingsProperty(getName(),newModel,getEditorType());
    }
}
