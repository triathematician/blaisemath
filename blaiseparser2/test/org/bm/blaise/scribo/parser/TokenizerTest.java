/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bm.blaise.scribo.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.bm.blaise.scribo.parser.Parser.Tokenizer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class TokenizerTest {

    public TokenizerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

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
            new Tokenizer(new Grammar(){
                public String[] preUnaryOperators() { return new String[] {"+", "-", "!"}; }
                public String[] postUnaryOperators() { return new String[] {"!"}; }
                public String[] naryOperators() { return new String[] {"+", "-", " ", "*", "/", "^", "&&", "||" }; }
                public String[] multaryOperators() { return new String[] { "+", "*", "&&", "||" }; }
                public String[][] parentheticals() { return new String[][] { {"(",")"}, {"/**", "*/"} }; }
                public String[] functions() { return new String[] {}; }
                public String[] constants() { return new String[] {}; }
                public Map<String, String> synonymMap() { return Collections.EMPTY_MAP; }
                public boolean isCaseSensitive() { return false; }
            }).tokenize(input, tokens, types);
            assertEquals(
                    "[+, -, 1, +, alphabet, +, b, (, c, -, d, ), " +
                    "*, _f, (, 5, !, ), +, -, 1.033e-5, f, " +
                    "/**, hi, */, " +
                    "a, ||, b, &&, !, c]", tokens.toString());
            assertEquals(
                    "[UNARY_OPERATOR, UNARY_OPERATOR, NUMBER, MULTARY_OPERATOR, IDENTIFIER, MULTARY_OPERATOR, IDENTIFIER, PARENTHETICAL_OPEN, IDENTIFIER, BINARY_OPERATOR, IDENTIFIER, PARENTHETICAL_CLOSE, " +
                    "MULTARY_OPERATOR, IDENTIFIER, PARENTHETICAL_OPEN, NUMBER, POST_UNARY_OPERATOR, PARENTHETICAL_CLOSE, MULTARY_OPERATOR, UNARY_OPERATOR, NUMBER, IDENTIFIER, " +
                    "PARENTHETICAL_OPEN, IDENTIFIER, PARENTHETICAL_CLOSE, " +
                    "IDENTIFIER, MULTARY_OPERATOR, IDENTIFIER, MULTARY_OPERATOR, UNARY_OPERATOR, IDENTIFIER]", types.toString());
        } catch (ParseException ex) {
            fail("Failed to tokenize the provided input");
        }
    }
}
