/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.BooleanGrammar;
import org.bm.blaise.scribo.parser.Grammar;
import org.bm.blaise.scribo.parser.RealGrammar;

/**
 *
 * @author ae3263
 */
public enum ParserType {
    REAL (RealGrammar.getInstance()),
    BOOLEAN (BooleanGrammar.getInstance()),
    VECTOR (VectorGrammar.getInstance());

    Grammar g;
    ParserType(Grammar g) { this.g = g; }

}
