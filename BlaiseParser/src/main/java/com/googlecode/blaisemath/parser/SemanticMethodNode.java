/**
 * SemanticMethodNode.java
 * Created on Dec 27, 2009
 */

package com.googlecode.blaisemath.parser;

/*
 * #%L
 * BlaiseParser
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 *    This classes builds a semantic node upon a static Java method. The arguments and argument
 *    types must be compatible with the underlying method.
 * </p>
 * @author Elisha Peterson
 */
class SemanticMethodNode extends SemanticArgumentNodeSupport {

    /** Method used to implement the node. */
    Method method;

    /**
     * Constructs the node using the specified method.
     * @param method the method
     */
    public SemanticMethodNode(Method method, SemanticNode... arguments) {
        super(method.getParameterTypes(), arguments);
        this.method = method;
    }

    public Class[] getParameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    boolean compatibleArguments(Class[] types1, Class[] types2) {
//        System.out.println("SemanticMethodNode.compatibleArguments: checking equivalence of types " + Arrays.toString(types1) + " and " + Arrays.toString(types2));
        boolean result = false;
        if (Arrays.equals(types1, types2))
            result = true;
        else if (types1.length == types2.length) {
            // TODO - also check the compatibility
            result = true;
        } else if (types1.length == 1 && types1[0].isArray() && types2.length == 0) {
            // if first argument accepts arrays, OK for length 0
            result = true;
        }
//        System.out.println(result);
        return result;
    }

    public Object getValue() throws SemanticTreeEvaluationException {
//        if (method.isVarArgs() && args.length < 2) {
//            System.out.println("var-args method: " + Arrays.toString(method.getParameterTypes()));
//            System.out.println(Arrays.toString(args));
//            System.out.println(args.getClass());
//        }
        if (method.isVarArgs()) {
            Class vaType = method.getParameterTypes()[method.getParameterTypes().length-1].getComponentType();
            Object vargs = Array.newInstance(vaType, parameters.length);
            for (int i = 0; i < parameters.length; i++)
                Array.set(vargs, i, parameters[i].getValue());
            try {
                return method.invoke(null, vargs);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(SemanticMethodNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(SemanticMethodNode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(SemanticMethodNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // will only reach here if its not a varargs call
            Object[] args = null;
            args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++)
                args[i] = parameters[i].getValue();
            try {
                return method.invoke(null, args);
            } catch (IllegalAccessException ex) {
                throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(args) + ": " + ex);
            } catch (InvocationTargetException ex) {
                throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(args) + ": " + ex);
            } catch (IllegalArgumentException ex) {
                throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(args) + ": " + ex);
            }
        }
        throw new IllegalStateException("Should not be here!");
    }

    public Class getValueType() {
        return method.getReturnType();
    }
}
