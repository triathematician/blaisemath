/**
 * MenuConfig.java
 * Created Nov 5, 2014
 */
package com.googlecode.blaisemath.app;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 * <p>
 *   Provides utility methods for loading menus and toolbars from configuration
 *   files, merging them with action maps for functionality.
 * </p>
 * <p>
 *   The menu configuration files include one or more nested menus/toolbars.
 *   Menu content is a list of content items, where a content item may be either:
 * </p>
 * <ul>
 *   <li>An empty value corresponding to a separator</li>
 *   <li>A string corresponding to an action</li>
 *   <li>A customized action, encoded as a key-value pair where the key is the
 *     action and the value is a map of key-value pairs configuring the action's
 *     appearance (e.g. regular menu item vs. checkbox menu item)</li>
 *   <li>A submenu, encoded as a key-value pair where the key is the submenu name
 *     and the value is a list of content items</li>
 *   <li>A customized submenu, encoded as a key-value pair where the key is
 *     an action and the value is a map including a {@code name} key for the
 *     sub-menus display name and an {@code options} key referencing a static
 *     method that returns a list of options to use for the menu.</li>
 * </ul>
 * <p>
 *   Toolbars may not have embedded sub-menus.
 * </p>
 * <p>
 *   Here is an example:
 * </p>
 * <pre>{@code# YAML menu config
 * ---
 * MENUBAR:
 *   File:
 *     - load
 *     - save
 *     -
 *     - quit
 *   Edit:
 *     - Edit Something: {
 *       action: editThis,
 *       options: org.yours.package.Utils#editThisOptions
 *     }
 *     - SubMenu 2:
 *        - subOption1
 *        - subOption2
 *     -
 *     - toggle: { type: CHECKBOX }
 * TOOLBAR:
 *   - load
 *   - save
 *   -
 *   - toggle: { type: TOGGLE }
 *   -
 *   - toolbar1
 *   - toolbar2
 * Other Menu:
 *   - action1
 *   - action2
 * }</pre>
 * <p>
 *   The following configuration options for actions are supported:
 * </p>
 * <ul>
 *   <li>{@code type: CHECKBOX} to display the action with a checkbox in the menu or toolbar</li>
 *   <li>{@code type: TOGGLE} to display the action with a toggle button in the toolbar</li>
 * </ul>
 * 
 * @author elisha
 * @version 2.0
 */
public class MenuConfig {

    private static final Logger LOG = Logger.getLogger(MenuConfig.class.getName());

    public static final String MENU_SUFFIX = "_menu";
    public static final String DEFAULT_TOOLBAR_KEY = "TOOLBAR";
    public static final String DEFAULT_MENUBAR_KEY = "MENUBAR";
    
    // utility class
    private MenuConfig() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="READING FROM CONFIGURATION FILE">
    
    /**
     * Gets the menubar configuration from file.
     * @param cls class with associated resource
     * @param key key for menubar component in config file
     * @return menus to comprise menubar, where values are lists with either strings or nested menus
     * @throws IOException if there's an error reading from the config file
     */
    public static List<Map> readMenuBarConfig(Class cls, String key) throws IOException {
        Object res = readConfig(cls).get(key);
        checkState(res instanceof List, "Expected menubar to be a list of maps but was "+res);
        checkState(Iterables.all((List) res, Predicates.instanceOf(Map.class)),
            "Expected list elements of menubar to be maps");
        return (List<Map>) res;
    }
    
    /**
     * Gets the toolbar configuration from file.
     * @param cls class with associated resource
     * @param key key for toolbar component in config file
     * @return list of actions for the toolbar
     * @throws IOException if there's an error reading from the config file
     */
    public static List<String> readToolBarConfig(Class cls, String key) throws IOException {
        Object res = readConfig(cls).get(key);
        checkState(res instanceof List, "Expected toolbar to be a list of strings");
        return (List<String>) res;
    }
    
    /**
     * Gets the menu configuration from file.
     * @param cls class with associated resource
     * @param key key for menu component in config file
     * @return list of actions for the toolbar
     * @throws IOException if there's an error reading from the config file
     */
    public static List readMenuConfig(Class cls, String key) throws IOException {
        Object res = readConfig(cls).get(key);
        checkState(res instanceof List, "Expected menu to be a list");
        return (List) res;
    }
    
    /** 
     * Reads menu components from file.
     * @param cls class with associated resource
     * @return map, where values are either nested maps, or lists of strings representing actions
     * @throws IOException if there's an error reading from the config file
     */
    public static Map<String, Object> readConfig(Class cls) throws IOException {
        String path = "resources/"+cls.getSimpleName() + MENU_SUFFIX + ".yml";
        URL rsc = cls.getResource(path);
        checkNotNull(rsc, "Failed to locate "+path);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(rsc, Map.class);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CONVERTING CONFIGURATION CONTENT TO MENUS/ITEMS">

    /**
     * Build a menu from a given map entry with key the menu name, and value a list
     * or map that determines the menu items.
     * @param name menu name
     * @param items content of menu
     * @param am string/action mapping
     * @return menu
     */
    static JToolBar createToolBar(String name, List items, Map<String,Action> am) {
        JToolBar tb = new JToolBar(name);
        for (Object it : items) {
            boolean used = false;
            for (MenuConfigElement el : MenuConfigElement.values()) {
                if (el.appliesTo(it)) {
                    el.addToolbarItem(tb, it, am);
                    used = true;
                    break;
                }
            }
            if (!used) {
                LOG.log(Level.WARNING, "Invalid toolbar item:{0}", it);
            }
        }
        return tb;
    }

    /**
     * Build a menu from a given map entry with key the menu name, and value a list
     * or map that determines the menu items.
     * @param name menu name
     * @param menuContent content of menu
     * @param am string/action mapping
     * @return menu
     */
    static JMenu createMenu(String name, List menuContent, Map<String,Action> am) {
        JMenu menu = new JMenu(name);
        for (JMenuItem el : createMenuItemList(menuContent, am)) {
            if (el == null) {
                menu.addSeparator();
            } else {
                menu.add(el);
            }
        }
        return menu;
    }

    /**
     * Builds a menu based on a list of items provided as actions or submenus.
     * @param items configuration of menu items
     * @param actionMap string/action mapping to lookup actions
     * @return menu items, with null values for separators
     */
    static List<JMenuItem> createMenuItemList(List items, Map<String,javax.swing.Action> actionMap) {
        checkNotNull(items);
        checkNotNull(actionMap);
        List<JMenuItem> res = Lists.newArrayList();
        for (Object it : items) {
            boolean used = false;
            for (MenuConfigElement el : MenuConfigElement.values()) {
                if (el.appliesTo(it)) {
                    res.add(el.menuItem(it, actionMap));
                    used = true;
                    break;
                }
            }
            if (!used) {
                LOG.log(Level.WARNING, "Invalid menu item:{0}", it);
            }
        }
        return res;
    }

    private static JButton popupButton(String name, final JPopupMenu popup) {
        final JButton button = new JButton(name);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        return button;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="GETTING STUFF OUT OF MAPS">
    
    /** Tests whether map is a singleton */
    static boolean isSingletonMap(Map<String, ?> map) {
        return map != null && map.size() == 1;
    }

    /** Convert a map with a single entry to the entry */
    static <X> Entry<String, X> singletonMapEntry(Map<String, X> map) {
        checkArgument(map != null);
        checkArgument(map.size() == 1);
        return map.entrySet().iterator().next();
    }
    
    /** Tests whether key exists as a key in a singleton map's value map */
    static boolean existsKeyNestedInSingletonMap(String key, Map<String, ?> map) {
        if (!isSingletonMap(map)) {
            return false;
        }
        Entry<String, ?> en = singletonMapEntry(map);
        if (!(en.getValue() instanceof Map)) {
            return false;
        }
        return ((Map) en.getValue()).containsKey(key);
    }
    
    //</editor-fold>
    
    
    /** Decode options supported for the menu config */
    private static enum MenuConfigElement {
        SEPARATOR(){
            @Override
            boolean appliesTo(Object item) {
                return item == null || (item instanceof String && ((String)item).isEmpty());
            }
            @Override
            JMenuItem menuItem(Object item, Map<String, Action> actionMap) {
                return null;
            }
            @Override
            void addToolbarItem(JToolBar tb, Object item, Map<String, Action> actionMap) {
                tb.addSeparator();
            }
        },
        ACTION(){
            @Override
            boolean appliesTo(Object item) {
                return item instanceof String;
            }
            @Override
            JMenuItem menuItem(Object item, Map<String, Action> actionMap) {
                return new JMenuItem(actionMap.get((String) item));
            }
            @Override
            void addToolbarItem(JToolBar tb, Object item, Map<String, Action> actionMap) {
                tb.add(actionMap.get((String) item));
            }
        },
        SPECIAL_ACTION(){
            @Override
            boolean appliesTo(Object item) {
                return item instanceof Map
                        && existsKeyNestedInSingletonMap("type", (Map) item);
            }
            @Override
            JMenuItem menuItem(Object item, Map<String, Action> actionMap) {
                Entry<String,Object> en = singletonMapEntry((Map) item);
                Action action = actionMap.get(en.getKey());
                Map submap = (Map) en.getValue();
                Object type = submap.get("type");
                if ("CHECKBOX".equals(type)) {
                    return new JCheckBoxMenuItem(action);
                } else if ("RADIO".equals(type)) {
                    return new JRadioButtonMenuItem(action);
                } else {
                    LOG.log(Level.SEVERE, "Unsupported item type: {0}", type);
                    return null;
                }
            }
            @Override
            void addToolbarItem(JToolBar tb, Object item, Map<String, Action> actionMap) {
                Entry<String,Object> en = singletonMapEntry((Map) item);
                Action action = actionMap.get(en.getKey());
                Map submap = (Map) en.getValue();
                Object type = submap.get("type");
                if ("CHECKBOX".equals(type)) {
                    tb.add(new JCheckBox(action));
                } else if ("RADIO".equals(type)) {
                    tb.add(new JRadioButton(action));
                } else if ("TOGGLE".equals(type)) {
                    tb.add(new JToggleButton(action));
                } else {
                    LOG.log(Level.SEVERE, "Unsupported item type: {0}", type);
                }
            }
        },
        OPTION_MENU(){
            @Override
            boolean appliesTo(Object item) {
                return item instanceof Map
                        && existsKeyNestedInSingletonMap("options", (Map) item);
            }
            @Override
            JMenuItem menuItem(Object item, Map<String, Action> actionMap) {
                Entry<String,Object> en = singletonMapEntry((Map) item);
                OptionMenuConfig config = new ObjectMapper().convertValue(en.getValue(), OptionMenuConfig.class);
                config.setAction(en.getKey());
                return config.createMenu(actionMap);
            }
            @Override
            void addToolbarItem(JToolBar tb, Object item, Map<String, Action> actionMap) {
                Entry<String,Object> en = singletonMapEntry((Map) item);
                OptionMenuConfig config = new ObjectMapper().convertValue(en.getValue(), OptionMenuConfig.class);
                config.setAction(en.getKey());
                tb.add(popupButton(config.getName(), config.createPopupMenu(actionMap)));
            }
        },
        SUB_MENU(){
            @Override
            boolean appliesTo(Object item) {
                return item instanceof Map
                        && singletonMapEntry((Map) item).getValue() instanceof List;
            }
            @Override
            JMenuItem menuItem(Object item, Map<String, Action> actionMap) {
                Entry<String,Object> en = singletonMapEntry((Map) item);
                return createMenu(en.getKey(), (List) en.getValue(), actionMap);
            }
            @Override
            void addToolbarItem(JToolBar tb, Object item, Map<String, Action> actionMap) {
                Entry<String,Object> en = singletonMapEntry((Map) item);
                JPopupMenu popup = new JPopupMenu(en.getKey());
                for (JMenuItem mi : createMenuItemList((List) en.getValue(), actionMap)) {
                    popup.add(mi);
                }
                tb.add(popupButton(en.getKey(), popup));
            }
        };
        
        abstract boolean appliesTo(Object item);
        abstract JMenuItem menuItem(Object item, Map<String,javax.swing.Action> actionMap);
        abstract void addToolbarItem(JToolBar tb, Object item, Map<String, javax.swing.Action> actionMap);
    }
    
}
