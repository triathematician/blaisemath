/**
 * SettingsPropertyGroup.java
 * Created on Jun 12, 2008
 */

package sequor;

/**
 * The <b>SettingsPropertyGroup</b> class contains a <b>Settings</b> class which is used
 * to generate multiple entries on a GUI view of another <b>Settings</b> class. The view
 * of the group may be <i>expanded</i> or <i>collapsed</i>.
 * 
 * @author Elisha Peterson
 */
public class SettingsPropertyGroup extends SettingsProperty {

    /** The group of settings to insert. */
    Settings settings;
    
    /** Whether the group is expanded or collapsed. */
    boolean expanded;
        
    
    // CONSTRUCTORS

    /** Full constructor. */
    public SettingsPropertyGroup(String name, String tooltipText, boolean editable, boolean displayed, boolean expanded) {
        super(name, null, Settings.NO_EDIT, tooltipText, editable, displayed);
        this.expanded = expanded;
    }
    
    
    // LAYOUT METHODS
    
    /** Returns number of rows required. */
    @Override
    public int getNumRows(){
        return displayed ? 1 + settings.getNumRows() : 0;
    }
}
