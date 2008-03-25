/**
 * SettingsMenu.java
 * Created on Mar 11, 2008
 */

package sequor.component;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.ColorModel;
import sequor.model.StringRangeModel;

/**
 * A JMenu with several editors determined by a particular Settings class. Currently, only colors and combobox's are supported. 
 * 
 * @author Elisha Peterson
 */
public class SettingsMenu extends JMenu {
    /** Underlying data. */
    Settings s;
    /** Display type. */
    int displayType=DISPLAY_SPINNERS;
    /** Display as a table. */
    public static final int DISPLAY_TABLE=0;
    /** Display using spinners. */
    public static final int DISPLAY_SPINNERS=1;
    /** Display using sliders. */
    public static final int DISPLAY_SLIDERS=2;
    
    public SettingsMenu(){
        s=new Settings();
        s.addDefaultItems();
        updateMenu();
    }
    public SettingsMenu(Settings s){
        this.s=s;
        updateMenu();
    }
    
    public void setSettings(Settings s) {this.s=s;}
    public Settings getSettings(){return s;}
    
    /** Places the settings on a given panel, and performs proper layout. */
    public void updateMenu(){     
        removeAll();   
        super.setText(s.toString());
        for(SettingsProperty sp:s){
            switch(sp.getEditorType()){
                case Settings.EDIT_COMBO : 
                    for(JMenuItem mi:((StringRangeModel)sp.getModel()).getMenuItems()){add(mi);}
                    break;
                case Settings.EDIT_COLOR :
                    if (sp.getModel() instanceof ColorModel){
                        add(Settings.getMenuItem(sp.getName(),(ColorModel)sp.getModel()));
                    }
                    break;
                default :
                    break;
            }
        }
        validate();
    }
}
