/**
 * MenuConfig.java
 * Created Nov 5, 2014
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import org.yaml.snakeyaml.Yaml;

/**
 * Utility for reading a .yaml configuration file and generating menus and toolbars.
 * 
 * @author elisha
 * @version 1.1
 */
public class MenuConfig {

    public static final String MENU_SUFFIX = "_menu";
    public static final String DEFAULT_TOOLBAR_KEY = "Toolbar";
    public static final String DEFAULT_MENUBAR_KEY = "Menubar";
    
    /** String key used in configurable menus for the action being referenced. */
    public static final String ACTION_KEY = "action";
    /** String key used in configurable menus pointing to the method being referenced for a list of items. */
    public static final String OPTIONS_KEY = "options";
    
    private static final String INVALID_METHOD_ERROR = "Invalid method used for configurable options menu";
    
    /** 
     * Reads menu components from file.
     * @param cls class with associated resource
     * @return map, where values are either nested maps, or lists of strings representing actions
     * @throws IOException if there's an error reading from the resource file
     */
    public static Map<String, Object> readConfig(Class cls) throws IOException {
        String path = "resources/"+cls.getSimpleName() + MENU_SUFFIX + ".yml";
        URL rsc = cls.getResource(path);
        checkNotNull(rsc, "Failed to locate "+path);
        Yaml yaml = new Yaml();
        Map res = yaml.loadAs(rsc.openStream(), Map.class);
        return res;
    }
    
    /**
     * Gets the toolbar configuration from file.
     * @param cls class with associated resource
     * @param key key for toolbar component in config file
     * @return list of actions for the toolbar
     * @throws IOException if there's an error reading from the resource file
     */
    public static List<String> readToolBarConfig(Class cls, String key) throws IOException {
        Object res = readConfig(cls).get(key);
        checkState(res instanceof List);
        return (List<String>) res;
    }
    
    /**
     * Gets the menubar configuration from file.
     * @param cls class with associated resource
     * @param key key for menubar component in config file
     * @return menus to comprise menubar, where values are lists with either strings or nested menus
     * @throws IOException if there's an error reading from the resource file
     */
    public static Map<String, List> readMenuBarConfig(Class cls, String key) throws IOException {
        Object res = readConfig(cls).get(key);
        checkState(res instanceof Map);
        return (Map<String, List>) res;
    }
    
    /**
     * Create toolbar component from file
     * @param cls class determining where to look for the config file.
     * @param am string/action mapping
     * @return toolbar
     * @throws java.io.IOException 
     */
    public static JToolBar readToolBar(Class cls, ActionMap am) throws IOException {
        List<String> config = readToolBarConfig(cls, DEFAULT_TOOLBAR_KEY);
        JToolBar res = new JToolBar();
        for (String k : config) {
            if (Strings.isNullOrEmpty(k)) {
                res.addSeparator();
            } else {
                res.add(am.get(k));
            }
        }
        return res;
    }
    
    /**
     * Create menubar from file
     * @param cls class determining where to look for the config file.
     * @param am string/action mapping
     * @return menu bar
     * @throws java.io.IOException 
     */
    public static JMenuBar readMenuBar(Class cls, ActionMap am) throws IOException {
        Map<String, ?> config = readMenuBarConfig(cls, DEFAULT_MENUBAR_KEY);
        JMenuBar res = new JMenuBar();
        for (Entry<String, ?> en : config.entrySet()) {
            res.add(readMenu(en, am));
        }
        return res;
    }

    /**
     * Build a menu from a given map entry with key the menu name, and value a list
     * or map that determines the menu items.
     * @param en entry
     * @param am string/action mapping
     * @return menu
     */
    private static JMenu readMenu(Entry<String, ?> en, ActionMap am) {
        JMenu menu = new JMenu(en.getKey());
        if (en.getValue() instanceof List) {
            // add each element to submenu
            addMenuBarItems(menu, (List) en.getValue(), am);
        } else if (en.getValue() instanceof Map) {
            // configurable menu
            addConfigurableMenuItems(menu, (Map) en.getValue(), am);
        }
        return menu;
    }

    /**
     * Adds collection of actions to menu
     * @param menu the menu to add to
     * @param items the values to add
     * @param am string/action mapping
     */
    private static void addMenuBarItems(JMenu menu, @Nullable List items, ActionMap am) {
        if (items == null) {
            return;
        }
        for (Object it : items) {
            if (it == null) {
                // add separator
                menu.addSeparator();
            } else if (it instanceof String) {
                // add action
                menu.add(am.get((String)it));
            } else if (it instanceof Map) {
                // add submenu
                Map<String, ?> config = (Map<String, ?>) it;
                for (Entry<String, ?> en : config.entrySet()) {
                    menu.add(readMenu(en, am));
                }
            } else {
                Logger.getLogger(MenuConfig.class.getName()).log(Level.WARNING,
                        "Invalid menu item:{0}", it);
            }
        }
    }

    /**
     * Adds a configurable collection of menu items to the menu.
     * @param menu the menu to add to
     * @param props configuration options for the menu
     * @param am string/action mapping
     */
    private static void addConfigurableMenuItems(JMenu menu, Map props, ActionMap am) {
        Object actionName = props.get(ACTION_KEY);
        Object methodName = props.get(OPTIONS_KEY);
        checkArgument(actionName instanceof String && methodName instanceof String,
                "Custom configuration options requires use of "+ACTION_KEY+" and "+OPTIONS_KEY+" properties.");
        Action action = am.get(actionName);
        List options = invokeListMethod((String)methodName);
        if (action == null) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, "Action not found: {0}", actionName);
        }
        if (options == null) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, "Method not found or invalid: {0}", methodName);
        }
        if (action == null || options == null) {
            return;
        }
        for (Object o : options) {
            menu.add(sourceAction(action, o));
        }
    }

    /**
     * Attempt to invoke static method defined by given string, returning result as a list.
     */
    private static List invokeListMethod(String string) {
        try {
            String[] spl = string.split("#");
            if (spl.length != 2) {
                return null;
            }
            String className = spl[0];
            String methodName = spl[1];
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            Object res = method.invoke(null);
            if (!(res instanceof Iterable)) {
                Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, "Did not return iterable: {0}", res);
            }
            return Lists.newArrayList((Iterable) res);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MenuConfig.class.getName()).log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        }
        return null;
    }

    /**
     * Override the default action behavior to always use the provided object as source.
     */
    private static Action sourceAction(final Action action, final Object src) {
        return new AbstractAction(src+"") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionEvent evt = new ActionEvent(src, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
                action.actionPerformed(evt);
            }
        };
    }
    
}
