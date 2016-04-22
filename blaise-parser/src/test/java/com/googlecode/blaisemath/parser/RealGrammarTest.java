/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

import com.googlecode.blaisemath.parser.SemanticTreeEvaluationException;
import com.googlecode.blaisemath.parser.ParseException;
import com.googlecode.blaisemath.parser.SemanticNode;
import com.googlecode.blaisemath.parser.GrammarParser;
import com.googlecode.blaisemath.parser.RealGrammar;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class RealGrammarTest {

    GrammarParser gp = RealGrammar.getParser();

    void assertEqualTree(double value, String s2) {
        SemanticNode tree = null;
        double treeValue = 0;
        try {
            tree = gp.parseTree(s2);
            try {
                treeValue = (Double) tree.getValue();
                assertEquals(value, treeValue, 1e-5);
            } catch (SemanticTreeEvaluationException ex) {
                Logger.getLogger(RealGrammarTest.class.getName()).log(Level.SEVERE, null, ex);
                fail("Unable to evaluate input");
            }
        } catch (ParseException ex) {
            Logger.getLogger(RealGrammarTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unable to parse input");
        }
    }

    @Test
    public void testBuildTreeReal() {
        System.out.println("buildTree (real operators)");

        assertEqualTree(1., "1.");
        assertEqualTree(-1., "-1");
        assertEqualTree(-9., "-(5+4)");
        assertEqualTree(-1., "1-1-1");
        assertEqualTree(3., "1+1+1");
        assertEqualTree(8., "2*2*2");
        assertEqualTree(0.5, "2/2/2");
        assertEqualTree(16., "2^2^2");
        assertEqualTree(3486784401.0, "3^4^5");

        assertEqualTree(0.5, "1.5%1");
        assertEqualTree(120., "5!");
    }

    @Test
    public void testBuildTreeConstants() {
        System.out.println("buildTree (constants)");

        assertEqualTree(Math.PI / 2, "pi/2");
        assertEqualTree(-Math.PI, "-pi");
        assertEqualTree(2.718281828, "e^1");
        assertEqualTree(1.0, "e^0");
    }

    @Test
    public void TestBuildTreeBasicPower() {
        System.out.println("buildTree (basic power functions)");

        assertEqualTree(5.0, "sqrt(3^2+4^2)");
        assertEqualTree(3.0, "cbrt(27)");
        assertEqualTree(8.0, "pow(2,3)");
        assertEqualTree(5.0, "hypot(3,4)");

        assertEqualTree(Math.PI / 4, "atan2(1,1)");
        assertEqualTree(3 * Math.PI / 4, "atan2(1,-1)");
        assertEqualTree(-Math.PI / 4, "atan2(-1,1)");
        assertEqualTree(-3 * Math.PI / 4, "atan2(-1,-1)");
    }

    @Test
    public void testBuildTreeNonContinuous() {
        System.out.println("buildTree (non-continuous functions)");

        assertEqualTree(1.0, "abs(-1.0)");
        assertEqualTree(1.0, "ceil(0.5)");
        assertEqualTree(1.0, "ceiling(0.5)");
        assertEqualTree(1.0, "floor 1.5");
        assertEqualTree(0.5, "frac 1.5");
        assertEqualTree(1.0, "round 0.8");
        assertEqualTree(1.0, "round 1.2");

        assertEqualTree(1.0, "signum(sin(e))");
        assertEqualTree(1.0, "sign(sin(e))");
        assertEqualTree(1.0, "sgn(sin(e))");
        assertEqualTree(-1.0, "signum(cos(e))");
    }

    @Test
    public void testBuildTreeTrig() {
        System.out.println("buildTree (trig functions)");

        assertEqualTree(1., "cos(0)");
        assertEqualTree(0.5, "cos(pi/4)*cos(pi/4)");
        assertEqualTree(0., "sin(0)");
        assertEqualTree(1., "tan(pi/4)");

        assertEqualTree(1., "cosine(0)");
        assertEqualTree(0., "sine(0)");
        assertEqualTree(1., "tangent(pi/4)");

        assertEqualTree(3.141592653589793, "acos(-1)");
        assertEqualTree(0.0, "asin(0)");
        assertEqualTree(Math.PI / 4, "atan(1)");

        assertEqualTree(3.141592653589793, "arccos(-1)");
        assertEqualTree(0.0, "arcsin(0)");
        assertEqualTree(Math.PI / 4, "arctan(1)");

        assertEqualTree(3.141592653589793, "arccosine(-1)");
        assertEqualTree(0.0, "arcsine(0)");
        assertEqualTree(Math.PI / 4, "arctangent(1)");
    }

    @Test
    public void testBuildTreeExpLog() {
        System.out.println("buildTree (exp, log, hyperbolic functions)");

        assertEqualTree(1.1752, "sinh(1.0)");
        assertEqualTree(1.54308, "cosh(1.0)");
        assertEqualTree(0.761594, "tanh(1.0)");

        assertEqualTree(2.718281828, "exp(1)");
        assertEqualTree(1.0, "exp(0)");
        assertEqualTree(1.0, "log(e)");
        assertEqualTree(0.0, "log(1)");
        assertEqualTree(3.0, "log10(1000)");
        assertEqualTree(0.0, "log10(1)");
        assertEqualTree(2.0, "log2(4)");
        assertEqualTree(0.0, "log2(1)");

        assertEqualTree(1.0, "ln(e)");
        assertEqualTree(0.0, "ln(1)");
    }

    @Test
    public void testBuildTreeRandom() {
        System.out.println("buildTree (random functions)");

        assertEqualTree(0.0, "randbetween(0,1e-6)");
        assertEqualTree(0.0, "randbetween(0,1e-6)");
        assertEqualTree(0.0, "1e-6*rand()");
        assertEqualTree(0.0, "1e-6*random()");
    }

    @Test
    public void testBuildTreeStat() {
        System.out.println("buildTree (statistical functions)");

        assertEqualTree(-Double.MAX_VALUE, "min()");
        assertEqualTree(Double.MAX_VALUE, "max()");
        assertEqualTree(0.0, "min(0.0)");
        assertEqualTree(0.0, "min(0.0,1.0)");
        assertEqualTree(1.0, "max(0.0,1.0)");

        assertEqualTree(0.0, "minimum(0.0,1.0,2.0,3.0,4.0)");
        assertEqualTree(4.0, "maximum(0.0,1.0,2.0,3.0,4.0)");

        assertEqualTree(Double.NaN, "avg()");
        assertEqualTree(1.0, "avg(1.0)");
        assertEqualTree(0.5, "avg(0.0,1.0)");
        assertEqualTree(2.0, "average(0.0,1.0,2.0,3.0,4.0)");

        assertEqualTree(0.0, "sum()");
        assertEqualTree(1.0, "sum(1.0)");
        assertEqualTree(15.0, "sum(0.0,1.0,2.0,3.0,4.0,5.0)");

        assertEqualTree(1.0, "multiply()");
        assertEqualTree(2.0, "multiply(2.0)");
        assertEqualTree(120.0, "multiply(1.0,2.0,3.0,4.0,5.0)");
    }

//    @Test
//    public void testBuildTreeConditional() {
//        System.out.println("buildTree (conditional statements)");
//
//        assertEqualTree(1.0, "if(1>0,1.0,2.0)");
//        assertEqualTree(2.0, "if(1<=0,1.0,2.0)");
//
//        assertEqualTree(15.0, "sum(i,i,1,5)");
//        assertEqualTree(120.0, "prod(i,i,1,5)");
//    }
}
