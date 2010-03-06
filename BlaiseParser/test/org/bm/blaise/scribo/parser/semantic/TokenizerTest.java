/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bm.blaise.scribo.parser.semantic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bm.blaise.scribo.parser.Grammar;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.semantic.TokenParser.Tokenizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class TokenizerTest {


    /**
     * Test of tokenize method, of class Tokenizer.
     */
    @Test
    public void testTokenize() {
        System.out.println("tokenize");

        String input = "+-1+alphabet+b(c-d)*_f(5!)+-1.033e-5f /** hi */ a ||b && !c";
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<TokenType> types = new ArrayList<TokenType>();
        try { 
            new Tokenizer(new TestGrammar()).tokenize(input, tokens, types);
            assertEquals(
                    "[+, -, 1, +, alphabet, " +
                    "+, b, (, c, -, d, ), " +
                    "*, _f, (, 5, !, ), " +
                    "+, -, 1.033e-5, f, " +
                    "/**, hi, */, " +
                    "a, ||, b, &&, !, c]", tokens.toString());
            assertEquals(
                    "[PRE_UNARY_OPERATOR, PRE_UNARY_OPERATOR, NUMBER, MULTARY_OPERATOR, IDENTIFIER, " +
                    "MULTARY_OPERATOR, IDENTIFIER, PARENTHETICAL_OPEN, IDENTIFIER, BINARY_OPERATOR, IDENTIFIER, PARENTHETICAL_CLOSE, " +
                    "MULTARY_OPERATOR, IDENTIFIER, PARENTHETICAL_OPEN, NUMBER, POST_UNARY_OPERATOR, PARENTHETICAL_CLOSE, " +
                    "MULTARY_OPERATOR, PRE_UNARY_OPERATOR, NUMBER, IDENTIFIER, " +
                    "PARENTHETICAL_OPEN, IDENTIFIER, PARENTHETICAL_CLOSE, " +
                    "IDENTIFIER, MULTARY_OPERATOR, IDENTIFIER, MULTARY_OPERATOR, PRE_UNARY_OPERATOR, IDENTIFIER]", types.toString());
        } catch (ParseException ex) {
            fail("Failed to tokenize the provided input");
        }
    }
}
