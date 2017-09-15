/**
 * Preconditions.java
 * Created Sep 24, 2014
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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


/**
 * Static utilities for checking arguments and state.
 * 
 * @author Elisha
 */
public class Preconditions {
   
    // utility class
    private Preconditions() {
    }
    
    /**
     * Throws an exception if argument is null
     * @param arg the argument
     * @return the argument, if it is not null
     * @throws IllegalArgumentException if it is null
     */
    public static <T> T checkNotNull(T arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Null argument: "+arg);
        }
        return arg;
    }
    
    /**
     * Throws an exception if test fails
     * @param test the test
     * @throws IllegalStateException if test fails
     */
    public static void checkState(boolean test) {
        if (!test) {
            throw new IllegalStateException();
        }
    }
    
    /**
     * Throws an exception if test fails
     * @param test the test
     * @param msg message for exception if test fails
     * @throws IllegalStateException if test fails
     */
    public static void checkState(boolean test, String msg) {
        if (!test) {
            throw new IllegalStateException(msg);
        }
    }
    
}
