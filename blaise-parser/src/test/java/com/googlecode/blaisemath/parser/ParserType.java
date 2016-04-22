/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.parser;

import com.googlecode.blaisemath.parser.VectorGrammar;
import com.googlecode.blaisemath.parser.Grammar;
import com.googlecode.blaisemath.parser.RealGrammar;
import com.googlecode.blaisemath.parser.BooleanGrammar;

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
