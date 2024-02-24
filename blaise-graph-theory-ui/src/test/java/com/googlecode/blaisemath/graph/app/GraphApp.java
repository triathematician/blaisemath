package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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


import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * Application for displaying graphs and testing BlaiseGraphTheory code.
 * @author Elisha Peterson
 */
public final class GraphApp extends SingleFrameApplication {

    @Override
    protected void startup() {
        show(new GraphAppFrameView(this));
    }
    
    public static void main(String[] args) {
        Application.launch(GraphApp.class, args);
    }
    
}
