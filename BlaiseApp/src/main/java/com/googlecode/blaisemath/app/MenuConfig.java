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


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
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
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.yaml.snakeyaml.Yaml;

/**
 * Utility for reading a .yaml configuration file and generating menus and toolbars.
 * 
 * @author elisha
 * @version 2.0
 */
public class MenuConfig {

    private static final Logger LOG = Logger.getLogger(MenuConfig.class.getName());

    public static final String MENU_SUFFIX = "_menu";
    public static final String DEFAULT_TOOLBAR_KEY = "Toolbar";
    public static final String DEFAULT_MENUBAR_KEY = "Menubar";
    
    /** String key used in configurable menus for the action being referenced. */
    public static final String ACTION_KEY = "action";
    /** String key used in configurable menus pointing to the method being referenced for a list of items. */
    public static final String OPTIONS_KEY = "options";
    
    private static final String INVALID_METHOD_ERROR = "Invalid method used for configurable options menu";
    
    // utility class
    private MenuConfig() {
    }
    
    /**
     * Create toolbar component from file
     * @param appClass application type
     * @param actionParents potential parents for actions
     * @return toolbar
     * @throws IOException if there's an error reading from the config file 
     */
    public static JToolBar readToolBar(Class<? extends Application> appClass, Object... actionParents) throws IOException {
        ApplicationContext appContext = Application.getInstance(appClass).getContext();
        Map<String,javax.swing.Action> actionMap = actionMap(appContext, actionParents);
        List<String> config = readToolBarConfig(appClass, DEFAULT_TOOLBAR_KEY);
        JToolBar res = new JToolBar();
        for (String k : config) {
            if (Strings.isNullOrEmpty(k)) {
                res.addSeparator();
            } else {
                res.add(actionMap.get(k));
            }
        }
        return res;
    }
    
    /**
     * Create menubar from file
     * @param appClass application type
     * @param actionParents potential parents for actions
     * @return menu bar
     * @throws IOException if there's an error reading from the config file 
     */
    public static JMenuBar readMenuBar(Class<? extends Application> appClass, Object... actionParents) throws IOException {
        ApplicationContext appContext = Application.getInstance(appClass).getContext();
        Map<String,javax.swing.Action> actionMap = actionMap(appContext, actionParents);
        Map<String, ?> config = readMenuBarConfig(appClass, DEFAULT_MENUBAR_KEY);
        JMenuBar res = new JMenuBar();
        for (Entry<String, ?> en : config.entrySet()) {
            res.add(readMenu(en, actionMap));
        }
        return res;
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
        checkState(res instanceof List);
        return (List<String>) res;
    }
    
    /**
     * Gets the menubar configuration from file.
     * @param cls class with associated resource
     * @param key key for menubar component in config file
     * @return menus to comprise menubar, where values are lists with either strings or nested menus
     * @throws IOException if there's an error reading from the config file
     */
    public static Map<String, List> readMenuBarConfig(Class cls, String key) throws IOException {
        Object res = readConfig(cls).get(key);
        checkState(res instanceof Map);
        return (Map<String, List>) res;
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
        Yaml yaml = new Yaml();
        return yaml.loadAs(rsc.openStream(), Map.class);
    }

    /**
     * Build a menu from a given map entry with key the menu name, and value a list
     * or map that determines the menu items.
     * @param en entry
     * @param am string/action mapping
     * @return menu
     */
    private static JMenu readMenu(Entry<String, ?> en, Map<String,javax.swing.Action> am) {
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
    private static void addMenuBarItems(JMenu menu, @Nullable List items, Map<String,javax.swing.Action> am) {
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
                LOG.log(Level.WARNING, "Invalid menu item:{0}", it);
            }
        }
    }

    /**
     * Adds a configurable collection of menu items to the menu.
     * @param menu the menu to add to
     * @param props configuration options for the menu
     * @param am string/action mapping
     */
    private static void addConfigurableMenuItems(JMenu menu, Map props, Map<String,javax.swing.Action> am) {
        Object actionName = props.get(ACTION_KEY);
        Object methodName = props.get(OPTIONS_KEY);
        checkArgument(actionName instanceof String && methodName instanceof String,
                "Custom configuration options requires use of "+ACTION_KEY+" and "+OPTIONS_KEY+" properties.");
        Action action = am.get((String) actionName);
        List options = invokeListMethod((String)methodName);
        if (action == null) {
            LOG.log(Level.SEVERE, "Action not found: {0}", actionName);
            return;
        }
        if (options.isEmpty()) {
            LOG.log(Level.SEVERE, "Method did not return any valid options: {0}", methodName);
        }
        for (Object o : options) {
            menu.add(sourceAction(action, o));
        }
    }

    /** Attempt to invoke static method defined by given string, returning result as a list. */
    private static List invokeListMethod(String string) {
        try {
            Method method = findMethod(string);
            if (method == null) {
                return Collections.emptyList();
            }
            Object res = method.invoke(null);
            if (!(res instanceof Iterable)) {
                LOG.log(Level.SEVERE, "Did not return iterable: {0}", res);
            }
            return Lists.newArrayList((Iterable) res);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (SecurityException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (InvocationTargetException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        }
        return Collections.emptyList();
    }
    
    /** Looks up method based on given string name */
    @Nullable
    private static Method findMethod(String name) throws NoSuchMethodException, ClassNotFoundException {
        String[] spl = name.split("#");
        if (spl.length != 2) {
            return null;
        }
        String className = spl[0];
        String methodName = spl[1];
        Class clazz = Class.forName(className);
        return clazz.getMethod(methodName);
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

    /**
     * Uses a variable number of parent objects as potential action owners, creating a common lookup table for actions.
     * If more than one object has the same action, the result table will use the first it finds.
     * @param appContext context of application
     * @param actionParents parent objects for actions
     * @return table of actions available within the app context
     */
    private static Map<String, javax.swing.Action> actionMap(ApplicationContext appContext, Object... actionParents) {
        Map<String, javax.swing.Action> res = Maps.newHashMap();
        for (Object o : actionParents) {
            ActionMap am = appContext.getActionMap(o);
            for (Object k : am.allKeys()) {
                if (!res.containsKey((String) k)) {
                    res.put((String) k, am.get(k));
                }
            }
        }
        return res;
    }
    
}
