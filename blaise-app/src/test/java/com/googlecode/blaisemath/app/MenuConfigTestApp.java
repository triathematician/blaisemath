/*
 * Copyright 2016 elisha.
 *
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
 */
package com.googlecode.blaisemath.app;

/*
 * #%L
 * blaise-app
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


import java.util.Arrays;
import java.util.List;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 *
 * @author elisha
 */
public class MenuConfigTestApp extends SingleFrameApplication {

    @Action
    public void load() {
        System.out.println("load");
    }
    
    @Action
    public void save() {
        System.out.println("save");
    }

    private boolean sel = false;
    
    public boolean isToggleSelected() {
        return sel;
    }
    
    public void setToggleSelected(boolean val) {
        if (this.sel != val) {
            sel = val;
            System.out.println("new toggle valeu: "+val);
            firePropertyChange("toggleSelected", !val, val);
        }
    }
    
    @Action(selectedProperty = "toggleSelected")
    public void toggle() {
    }
    
    @Action
    public void toolbar1() {
        System.out.println("toolbar1");
    }
    
    @Action
    public void toolbar2() {
        System.out.println("toolbar2");
    }
    
    @Override
    protected void startup() {
        show(new MenuConfigTestFrame(this));
    }
    
    public static List<String> editThisOptions() {
        return Arrays.asList("Option 1", "Option 2");
    }
    
    public static void main(String... args) {
        Application.launch(MenuConfigTestApp.class, args);
    }
    
}
