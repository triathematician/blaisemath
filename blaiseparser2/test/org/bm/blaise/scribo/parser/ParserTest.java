/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scribo.parser;

import java.util.Collections;
import java.util.Map;
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
public class ParserTest {

    public ParserTest() {
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

    Parser parser = new Parser(new Grammar(){
            public String[] preUnaryOperators() { return new String[] {"+", "-", "!"}; }
            public String[] postUnaryOperators() { return new String[] {"!"}; }
            public String[] naryOperators() { return new String[] { ",", "+", "-", " ", "*", "/", "^", "%", "&&", "||" }; }
            public String[] multaryOperators() { return new String[] { "+", "*", "&&", "||", "," }; }
            public String[][] parentheticals() { return new String[][] { {"(",")"}, {"/**", "*/"}, {"[", "]"} }; }
            public String[] functions() { return new String[] { "sin" }; }
            public String[] constants() { return new String[] {}; }
            public Map<String, String> synonyms() { return Collections.EMPTY_MAP; }
            public boolean isCaseSensitive() { return false; }
        });

    /**
     * Test of parseTree method, of class Parser.
     */
    @Test
    public void testParseTreeBasic() throws Exception {
        System.out.println("parseTree -- basic");

        // basic order of operations
        assertEquals("TOP[[[,[a, b, c, d]]]",                       parser.parseTree("[a, b, c, d]").toString());
        assertEquals("TOP[+[a, -[b, c]]]",                          parser.parseTree("a+b-c").toString());
        assertEquals("TOP[+[([-[a, b]], c]]",                       parser.parseTree("(a-b)+c").toString());
        assertEquals("TOP[+[a, -[*[b, /[c, d]], ^[f, g]], h]]",     parser.parseTree("a+b*c/d-f^g+h").toString());
        assertEquals("TOP[+[a, -[*[b, ([+[c, -[d, 2]]]], 2], f]]",  parser.parseTree("a+b*(c+d-2)-2+f").toString());
        assertEquals("TOP[-[sin[([x]], 5]]",                        parser.parseTree("sin(x)-5").toString());
        assertEquals("TOP[/[/[a, b], c]]",                          parser.parseTree("a/b/   c").toString());
        assertEquals("TOP[-[-[a, b], c]]",                          parser.parseTree("a-b-c").toString());

        // implicit multiplication
        assertEquals("TOP[*[a, b, c]]",                             parser.parseTree("a b c").toString());
        assertEquals("TOP[[[*[a, b, c]]]",                          parser.parseTree("[a b*c]").toString());
    }

    /**
     * Test of parseTree method, of class Parser.
     */
    @Test
    public void testParseTreeUnaryOperators() throws Exception {
        System.out.println("parseTree -- test behavior of unary operators");

        System.out.println(parser.parseTree("--a--b").toString());
        assertEquals("TOP[-[a]]",                                   parser.parseTree("-a").toString());
        assertEquals("TOP[+[a]]",                                   parser.parseTree("+a").toString());
        assertEquals("TOP[-[-[a], b]]",                             parser.parseTree("-a-b").toString());
        assertEquals("TOP[+[+[a], b]]",                             parser.parseTree("+a+b").toString());

        assertEquals("TOP[-[-[-[a]], -[b]]]",                       parser.parseTree("--a--b").toString());
        assertEquals("TOP[-[-[-[-[a]]], -[-[b]]]]",                 parser.parseTree("---a---b").toString());
        assertEquals("TOP[+[-[a], -[b]]]",                          parser.parseTree("-a+-b").toString());
        assertEquals("TOP[-[-[+[a]], +[b]]]",                       parser.parseTree("-+a-+b").toString());
        assertEquals("TOP[+[+[-[a]], -[b]]]",                       parser.parseTree("+-a+-b").toString());
        assertEquals("TOP[+[+[+[a]], +[b]]]",                       parser.parseTree("++a++b").toString());
    }

    /**
     * Test of parseTree method, of class Parser.
     */
    @Test
    public void testParseTreeAdvanced() throws Exception {
        System.out.println("parseTree -- advanced");

        assertEquals("TOP[+[a, *[b, c, d, ([+[*[e, ^[g, 3]], -[f]]], ([-[2, 3]], [[,[*[5, 1], 2, 7]]]]]",
                parser.parseTree("a+b*c*d*(e*g^3+-f)(2-3)[5 1,2,7]").toString());

        System.out.println(parser.parseTree("+-1+alphabet+b(c-d)*_f(5)+-1.033e-5f /** hi */ a ||b c"));
        assertEquals("TOP[+[+[-[1]], alphabet, *[b, ([-[c, d]], _f, ([5]], *[-[1.033e-5], f, /**[hi], ||[a, b], c]]]",
                parser.parseTree("+-1+alphabet+b(c-d)*_f(5)+-1.033e-5f /** hi */ a ||b c").toString());

        assertEquals("TOP[+[*[a, b], -[c, /[^[d, f], %[g, h]]]]]",
                parser.parseTree("a*b+c-d^f/g%h").toString());
    }

}