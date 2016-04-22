/*
 * TokenType.java
 * Created Nov 2009
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

/**
 * <p>
 *  Describes the type of token and where it fits in the tree.
 * </p>
 * @author Elisha Peterson
 */
enum TokenType {

    IDENTIFIER(true, true),
    NUMBER(true, true),
    FUNCTION(true, false),
    PARENTHETICAL_OPEN(true, false),
    PARENTHETICAL_CLOSE(false, true),
    PRE_UNARY_OPERATOR(true, false),
    POST_UNARY_OPERATOR(false, false),
    BINARY_OPERATOR(false, false),
    MULTARY_OPERATOR(false, false);
    
    /** Whether the type makes sense at the beginning of a phrase */
    public boolean canStartPhrase;
    /** Whether the type can be followed by an n-ary operator */
    public boolean naryFollow;

    TokenType(boolean canStartPhrase, boolean naryFollow) {
        this.canStartPhrase = canStartPhrase;
        this.naryFollow = naryFollow;
    }
}
