/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.parser;

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

import com.googlecode.blaisemath.parser.SemanticTreeEvaluationException;
import com.googlecode.blaisemath.parser.ParseException;
import com.googlecode.blaisemath.parser.SemanticNode;
import com.googlecode.blaisemath.parser.GrammarParser;
import com.googlecode.blaisemath.parser.BooleanGrammar;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class BooleanGrammarTest {

    GrammarParser gp = BooleanGrammar.getParser();

    void assertEqualTree(boolean value, String s2) {
        SemanticNode tree = null;
        boolean treeValue = false;
        try {
            tree = gp.parseTree(s2);
            try {
                treeValue = (Boolean) tree.getValue();
                assertEquals(value, treeValue);
            } catch (SemanticTreeEvaluationException ex) {
                Logger.getLogger(BooleanGrammarTest.class.getName()).log(Level.SEVERE, null, ex);
                fail("Unable to evaluate input");
            }
        } catch (ParseException ex) {
            Logger.getLogger(BooleanGrammarTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unable to parse input");
        }
    }

    /**
     * Test of buildTree method, of class BooleanTreeBuilder.
     */
    @Test
    public void testBuildTree() throws Exception {
        System.out.println("buildTree");

        assertEqualTree(true, "not false");
        assertEqualTree(false, "!true");
        assertEqualTree(false, "true && false");
        assertEqualTree(true, "true and true");
        assertEqualTree(false, "true false");
        assertEqualTree(true, "true true");
        assertEqualTree(false, "true && false && true");
        assertEqualTree(true, "true or false");
        assertEqualTree(false, "false || false");
        assertEqualTree(true, "true xor false");
        assertEqualTree(false, "true xor true");
        assertEqualTree(true, "false equals false");
        assertEqualTree(false, "true == false");

        assertEqualTree(true, "true and false or true");
        assertEqualTree(true, "true and false or true and true");
        assertEqualTree(true, "false and true or true");
        assertEqualTree(false, "false and (true or true)");
    }
}