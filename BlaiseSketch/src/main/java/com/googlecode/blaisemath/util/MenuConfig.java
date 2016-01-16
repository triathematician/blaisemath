/**
 * MenuConfig.java
 * Created Nov 5, 2014
 */
package com.googlecode.blaisemath.util;

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


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @version 1.1
 */
public final class MenuConfig {

    public static final String MENU_SUFFIX = "_menu";
    public static final String DEFAULT_TOOLBAR_KEY = "Toolbar";
    public static final String DEFAULT_MENUBAR_KEY = "Menubar";

    /** utility class - prevent instantiation */
    private MenuConfig() {
    }
    
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
     * @param appClass root application type
     * @param actionParents potential parents for actions
     * @return toolbar
     * @throws java.io.IOException 
     */
    public static JToolBar readToolBar(Class<? extends Application> appClass, Object... actionParents) throws IOException {
        Map<String,javax.swing.Action> actionMap = actionMap(appClass, actionParents);
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
     * @param appClass root application type
     * @param actionParents potential parents for actions
     * @return menu bar
     * @throws java.io.IOException 
     */
    public static JMenuBar readMenuBar(Class appClass, Object... actionParents) throws IOException {
        Map<String,javax.swing.Action> actionMap = actionMap(appClass, actionParents);
        Map<String, List> config = readMenuBarConfig(appClass, DEFAULT_MENUBAR_KEY);
        JMenuBar res = new JMenuBar();
        for (Entry<String, List> en : config.entrySet()) {
            JMenu menu = new JMenu(en.getKey());
            addMenuBarItems(menu, en.getValue(), actionMap);
            res.add(menu);
        }
        return res;
    }

    /**
     * Adds collection of actions to menu
     * @param menu the menu to add to
     * @param items the values to add
     * @param am string/action mapping
     */
    private static void addMenuBarItems(JMenu menu, List items, Map<String,javax.swing.Action> am) {
        for (Object it : items) {
            if (it == null) {
                menu.addSeparator();
            } else if (it instanceof String) {
                menu.add(am.get((String)it));
            } else if (it instanceof Map) {
                Map<String, List> config = (Map<String, List>) it;
                for (Entry<String, List> en : config.entrySet()) {
                    JMenu submenu = new JMenu(en.getKey());
                    addMenuBarItems(submenu, en.getValue(), am);
                    menu.add(submenu);
                }
            } else {
                Logger.getLogger(MenuConfig.class.getName()).log(Level.WARNING,
                        "Invalid menu item:{0}", it);
            }
        }
    }

    /**
     * Uses a variable number of parent objects as potential action owners, creating a common lookup table for actions.
     * If more than one object has the same action, the result table will use the first it finds.
     * @param appClass 
     * @param actionParents
     * @return 
     */
    private static Map<String, javax.swing.Action> actionMap(Class<? extends Application> appClass, Object... actionParents) {
        ApplicationContext appContext = Application.getInstance(appClass).getContext();
        Map<String, javax.swing.Action> res = Maps.newHashMap();
        for (Object o : actionParents) {
            ActionMap am = appContext.getActionMap(o);
            for (Object k : am.allKeys()) {
                if (!res.containsKey((String) k)) {
                    res.put(((String) k), am.get(k));
                }
            }
        }
        return res;
    }
    
}
