/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.Grammar;
import org.bm.blaise.scribo.parser.grammars.*;

/**
 *
 * @author ae3263
 */
public enum ParserType {
    REAL(RealGrammar.INSTANCE),
    BOOLEAN(BooleanGrammar.INSTANCE),
    VECTOR(VectorGrammar.INSTANCE);

    Grammar g;
    ParserType(Grammar g) {
        this.g = g;
    }
}
