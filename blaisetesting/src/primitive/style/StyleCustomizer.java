/*
 * StyleCustomizer.java
 * Created Jan 8, 2011
 */

package primitive.style;

import java.util.TreeMap;

/**
 * This class provides for customization to style classes.
 * @author elisha
 */
public class StyleCustomizer {

    /** Null customizer */
    public static final StyleCustomizer NULL = new StyleCustomizer() {
        @Override public <O> O value(String key, O def) { return def; }
    };

    /** Maintains custom map */
    TreeMap<String,Object> custom = new TreeMap<String,Object>();

    /** Construct customizer from arguments */
    public StyleCustomizer(Object... keyValues) {
        for (int i = 0; i < keyValues.length; i+=2)
            custom.put((String) keyValues[i], keyValues[i+1]);
    }

    /**
     * Returns either a default style or a custom style if its in the table.
     * @param key style key to value
     * @param def default object to return
     */
    public <O> O value(String key, O def) {
        return (O) (custom.containsKey(key) ? custom.get(key) : def);
    }

}
