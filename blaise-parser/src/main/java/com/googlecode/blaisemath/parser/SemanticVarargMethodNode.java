/**
 * SemanticVarargMethodNode.java
 * Created on Dec 27, 2009
 */

package com.googlecode.blaisemath.parser;

/*
 * #%L
 * BlaiseParser
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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <p>
 *    This classes builds a semantic node upon a static Java method that takes a variable
 *    number of inputs of a single type (as an array)
 * </p>
 * @author Elisha Peterson
 */
class SemanticVarargMethodNode extends SemanticMethodNode {

    /**
     * Constructs the node using the specified method.
     * @param method the method
     */
    public SemanticVarargMethodNode(Method method, SemanticNode... arguments) {
        super(method, arguments);
        this.method = method;
    }

    @Override
    boolean compatibleArguments(Class[] types1, Class[] types2) {
        if (super.compatibleArguments(types1, types2)) {
            return true;
        } else {
            return types1.length == 1 && types1[0].isArray();
        }
    }

    @Override
    public Object getValue() throws SemanticTreeEvaluationException {
        Class pType = method.getParameterTypes()[0].getComponentType();
        try {
            // use explicit array creation for primitive types
            if (pType.equals(double.class)) {
                double[] args = new double[parameters.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = (Double) parameters[i].getValue();
                }
                return method.invoke(null, args);
            } else if (pType.equals(boolean.class)) {
                boolean[] args = new boolean[parameters.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = (Boolean) parameters[i].getValue();
                }
                return method.invoke(null, args);
            } else if (pType.equals(int.class)) {
                int[] args = new int[parameters.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = (Integer) parameters[i].getValue();
                }
                return method.invoke(null, args);
            } else {
                return value(pType);
            }
        } catch (IllegalAccessException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(parameters) + ": " + ex + " (invalid method)");
        } catch (InvocationTargetException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(parameters) + ": " + ex + " (invoking method)");
        } catch (ClassCastException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(parameters) + ": " + ex + " (incorrect parameters passed to method)");
        }
    }

    <T> Object value(Class<T> t) throws SemanticTreeEvaluationException {
        try {
            T[] args = (T[]) Array.newInstance(t, parameters.length);
            for (int i = 0; i < args.length; i++) {
                args[i] = (T) parameters[i].getValue();
            }
            return method.invoke(null, args);
        } catch (IllegalAccessException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(parameters) + ": " + ex);
        } catch (InvocationTargetException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(parameters) + ": " + ex);
        }
    }
}
