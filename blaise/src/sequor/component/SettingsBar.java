/**
 * SettingsBar.java
 * Created on Mar 11, 2008
 */

package sequor.component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import sequor.Settings;
import sequor.SettingsFactory;
import sequor.SettingsProperty;

/**
 * A collection of Settings components on a toolbar; only practical for small numbers of components.
 * 
 * @author Elisha Peterson
 */
public class SettingsBar extends JToolBar {
    /** Underlying data. */
    Settings s;
    
    public SettingsBar(){
        s=new Settings();
        setOrientation(VERTICAL);
        s.addTestItems();
        updateBar();
    }
    public SettingsBar(Settings s){
        this.s=s;
        setOrientation(VERTICAL);
        updateBar();
    }
    
    public void setSettings(Settings s) {this.s=s;}
    public Settings getSettings(){return s;}
    
    /** Places the settings on a given panel, and performs proper layout. */
    public void updateBar(){
        if(s==null){return;}
        removeAll();
        for (SettingsProperty sp : s) {
            if (sp.getEditorType()==Settings.NO_EDIT){continue;}
            if (sp.getEditorType()==Settings.EDIT_SEPARATOR){
                addSeparator();
                continue;
            }
            JLabel label=new JLabel(sp.getName());
            JComponent component = SettingsFactory.getEditor(sp);
            if(component!=null){
                label.setToolTipText(sp.getTooltipText());
                component.setToolTipText(sp.getTooltipText());
                add(label);
                add(component);
                addSeparator();
            }
        }
        setToolTipText(s.toString());
        setName(s.toString());
        validate();
    }
}
