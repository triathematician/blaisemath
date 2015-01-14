/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

import com.googlecode.blaisemath.parser.TokenParser;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ae3263
 */
public class TokenTreeTest {

    TokenParser parser = new TokenParser(new TestGrammar());

    /**
     * Test of parseTree method, of class Parser.
     */
    @Test
    public void testParseTreeBasic() throws Exception {
        System.out.println("parseTree -- basic");

        // basic order of operations
        assertEquals("TOP[[[,[a, b, c, d]]]",                       parser.tokenizeTree("[a, b, c, d]").toString());
        assertEquals("TOP[+[a, -[b, c]]]",                          parser.tokenizeTree("a+b-c").toString());
        assertEquals("TOP[+[([-[a, b]], c]]",                       parser.tokenizeTree("(a-b)+c").toString());
        assertEquals("TOP[+[a, -[*[b, /[c, d]], ^[f, g]], h]]",     parser.tokenizeTree("a+b*c/d-f^g+h").toString());
        assertEquals("TOP[+[a, -[*[b, ([+[c, -[d, 2]]]], 2], f]]",  parser.tokenizeTree("a+b*(c+d-2)-2+f").toString());
        assertEquals("TOP[-[sin[([x]], 5]]",                        parser.tokenizeTree("sin(x)-5").toString());
        assertEquals("TOP[/[/[a, b], c]]",                          parser.tokenizeTree("a/b/   c").toString());
        assertEquals("TOP[-[-[a, b], c]]",                          parser.tokenizeTree("a-b-c").toString());

        // implicit multiplication
        assertEquals("TOP[*[a, b, c]]",                             parser.tokenizeTree("a b c").toString());
        assertEquals("TOP[[[*[a, b, c]]]",                          parser.tokenizeTree("[a b*c]").toString());
    }

    /**
     * Test of parseTree method, of class Parser.
     */
    @Test
    public void testParseTreeUnaryOperators() throws Exception {
        System.out.println("parseTree -- test behavior of unary operators");

        assertEquals("TOP[-[a]]",                                   parser.tokenizeTree("-a").toString());
        assertEquals("TOP[+[a]]",                                   parser.tokenizeTree("+a").toString());
        assertEquals("TOP[-[-[a], b]]",                             parser.tokenizeTree("-a-b").toString());
        assertEquals("TOP[+[+[a], b]]",                             parser.tokenizeTree("+a+b").toString());

        assertEquals("TOP[-[-[-[a]], -[b]]]",                       parser.tokenizeTree("--a--b").toString());
        assertEquals("TOP[-[-[-[-[a]]], -[-[b]]]]",                 parser.tokenizeTree("---a---b").toString());
        assertEquals("TOP[+[-[a], -[b]]]",                          parser.tokenizeTree("-a+-b").toString());
        assertEquals("TOP[-[-[+[a]], +[b]]]",                       parser.tokenizeTree("-+a-+b").toString());
        assertEquals("TOP[+[+[-[a]], -[b]]]",                       parser.tokenizeTree("+-a+-b").toString());
        assertEquals("TOP[+[+[+[a]], +[b]]]",                       parser.tokenizeTree("++a++b").toString());
    }

    /**
     * Test of parseTree method, of class Parser.
     */
    @Test
    public void testParseTreeAdvanced() throws Exception {
        System.out.println("parseTree -- advanced");

        assertEquals("TOP[+[a, *[b, c, d, ([+[*[e, ^[g, 3]], -[f]]], ([-[2, 3]], [[,[*[5, 1], 2, 7]]]]]",
                parser.tokenizeTree("a+b*c*d*(e*g^3+-f)(2-3)[5 1,2,7]").toString());

//        System.out.println(parser.tokenizeTree("+-1+alphabet+b(c-d)*_f(5)+-1.033e-5f /** hi */ a ||b c"));
        assertEquals("TOP[+[+[-[1]], alphabet, *[b, ([-[c, d]], _f, ([5]], *[-[1.033e-5], f, /**[hi], ||[a, b], c]]]",
                parser.tokenizeTree("+-1+alphabet+b(c-d)*_f(5)+-1.033e-5f /** hi */ a ||b c").toString());

        assertEquals("TOP[+[*[a, b], -[c, /[^[d, f], %[g, h]]]]]",
                parser.tokenizeTree("a*b+c-d^f/g%h").toString());
    }

}
