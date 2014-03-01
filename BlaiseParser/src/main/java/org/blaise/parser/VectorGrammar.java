/**
 * BooleanGrammar.java
 * Created on Dec 1, 2009
 */

package org.blaise.parser;

/*
 * #%L
 * BlaiseParser
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 *    This class describes the supported language elements
 *    of a vector parsing library. EXPERIMENTAL ONLY AT THIS STAGE!
 * </p>
 * @author Elisha Peterson
 */
class VectorGrammar implements Grammar {

    static VectorGrammar INSTANCE;
    static TokenParser PARSER;

    /** @return a static instance of a VectorGrammar that can be used to construct a parser. */
    public static VectorGrammar getInstance() {
        if (INSTANCE == null)
            INSTANCE = new VectorGrammar();
        return INSTANCE;
    }

    /** @return a static instance of a parser that uses the VectorGrammar. */
    public static TokenParser getParser() {
        if (PARSER == null)
            PARSER = new TokenParser(getInstance());
        return PARSER;
    }

    //
    // STATIC DEFINITIONS
    //

    final static String[][] PARS = {{"(", ")"}, {"[", "]"}};

    final static Map<String, Boolean> CONSTANTS = Collections.EMPTY_MAP;
    final static Map<String, Method> PRE_OPS = Collections.EMPTY_MAP;
    final static Map<String, Method> POST_OPS = Collections.EMPTY_MAP;
    final static Map<String, Method> NARY_OPS = new TreeMap<String, Method>() {{
        try {
            put(";", VectorGrammar.class.getMethod("id", double[].class));
            put("+", VectorGrammar.class.getMethod("plus", double[].class, double[].class));
            put("x", VectorGrammar.class.getMethod("cross", double[].class, double[].class));
        } catch (Exception ex) {
            Logger.getLogger(VectorGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    final static String[] MULTARY_OPS = new String[] { ";", "," };
    final static String[] ORDER_OF_OPS = new String[] { ",", ";", "+" };

    final static Map<String, Method> FUNCTIONS = new TreeMap<String, Method>() {{
        try {
            put("vec", VectorGrammar.class.getMethod("id", double[].class));
            put("cross", VectorGrammar.class.getMethod("cross", double[].class, double[].class));
        } catch (Exception ex) {
            Logger.getLogger(VectorGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    //
    // INTERFACE METHODS
    //

    public boolean isCaseSensitive() { return false; }

    public String[][] parentheticals() { return PARS; }
    public String argumentListOpener() { return "("; }
    public String argumentListSeparator() { return ","; }
    public String implicitSpaceOperator() { return ";"; }

    public Map<String, Boolean> constants() { return CONSTANTS; }

    public Map<String, Method> preUnaryOperators() { return PRE_OPS; }
    public Map<String, Method> postUnaryOperators() { return POST_OPS; }
    public Map<String, Method> naryOperators() { return NARY_OPS; }
    public String[] multaryOperators() { return MULTARY_OPS; }
    public String[] orderOfOperations() { return ORDER_OF_OPS; }

    public Map<String, Method> functions() { return FUNCTIONS; }


    //
    // STATIC FUNCTIONS TO USE IN SEMANTIC EVALUATION
    //
    
    public static double[] id(double... x) { return x; }
    public static double[] plus(double[] x, double[] y) {
        if (x.length < y.length) return plus(y, x);
        assert x.length >= y.length;
        double[] r = new double[x.length];
        for (int i = 0; i < y.length; i++) {
            r[i] = x[i] + y[i];
        }
        for (int i = y.length; i < x.length; i++) {
            r[i] = x[i];
        }
        return r;
    }
    public static double[] cross(double[] x1, double[] x2) {
        return new double[] {
            x1[1]*x2[2]-x1[2]*x2[1],
            x1[2]*x2[0]-x1[0]*x2[2],
            x1[0]*x2[1]-x1[1]*x2[0]
        };
    }
}
