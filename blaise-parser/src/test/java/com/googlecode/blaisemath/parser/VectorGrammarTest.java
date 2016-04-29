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

import com.googlecode.blaisemath.parser.VectorGrammar;
import com.googlecode.blaisemath.parser.SemanticTreeEvaluationException;
import com.googlecode.blaisemath.parser.ParseException;
import com.googlecode.blaisemath.parser.SemanticNode;
import com.googlecode.blaisemath.parser.GrammarParser;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class VectorGrammarTest {

    GrammarParser gp = VectorGrammar.getParser();

    void assertEqualTree(double[] value, String s2) {
        SemanticNode tree = null;
        double[] treeValue = new double[]{};
        try {
            tree = gp.parseTree(s2);
            try {
                treeValue = (double[]) tree.getValue();
                assertEquals(value.length, treeValue.length);
                for (int i = 0; i < treeValue.length; i++) {
                    assertEquals(value[i], treeValue[i], 1e-6);
                }
            } catch (SemanticTreeEvaluationException ex) {
                Logger.getLogger(VectorGrammarTest.class.getName()).log(Level.SEVERE, null, ex);
                fail("Unable to evaluate input");
            }
        } catch (ParseException ex) {
            Logger.getLogger(VectorGrammarTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unable to parse input");
        }
    }

    @Test
    public void testBasicCases() throws Exception {
        System.out.println("testBasicCases");

        assertEqualTree(new double[]{1,2}, "vec(1,2)");
        assertEqualTree(new double[]{1,2}, "1;2");
        assertEqualTree(new double[]{1,2}, "[1;2]");
        assertEqualTree(new double[]{1,2,3}, "vec(1,2,3)");
        assertEqualTree(new double[]{1,2,3,4,5,6,7}, "1 2 3 4 5 6 7");
        assertEqualTree(new double[]{5,7,6,7}, "vec(1,2)+vec(4,5,6,7)");
        assertEqualTree(new double[]{6,7,10}, "[1 2 3]  + [2 4 5]  +   [3 1 2]");
        assertEqualTree(new double[]{0,0,1}, "cross([1 0 0],[0 1 0])");
    }

    @Test
    public void testBoundaryCases() throws Exception {
        System.out.println("testBoundaryCases");

        assertEqualTree(new double[]{}, "vec()");
        assertEqualTree(new double[]{1}, "vec(1)");
        assertEquals(1.0, (Double) gp.parseTree("[1]").getValue(), 1e-9);
    }

}
