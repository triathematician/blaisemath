/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scribo.parser.grammars;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bm.blaise.scribo.parser.GrammarParser;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import org.bm.blaise.scribo.parser.semantic.TokenParser;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class BooleanGrammarTest {

    GrammarParser gp = new TokenParser(BooleanGrammar.INSTANCE);

    void assertEqualTree(boolean value, String s2) {
        SemanticNode tree = null;
        boolean treeValue = false;
        try {
            tree = gp.parseTree(s2);
            try {
                treeValue = (Boolean) tree.value();
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