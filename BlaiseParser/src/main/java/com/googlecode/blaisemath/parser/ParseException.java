/*
 * ParseException.java
 * Created Nov 2009
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

/**
 * <p>
 *  Basic exception handling for parsing string inputs.
 * </p>
 * @author Elisha Peterson
 */
public class ParseException extends Exception {
    public ParseErrorCode code = ParseErrorCode.UNKNOWN_ERROR;

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, ParseErrorCode code) {
        super(message);
        this.code = code;
    }

    @Override
    public String toString() { return code.name() + " " + super.toString(); }



    public static enum ParseErrorCode {
        PARENTHETICAL_ERROR,
        UNRECOGNIZED_SYMBOL,
        INVALID_OPERATOR_POSITION,
        ENDED_EARLY,
        UNKNOWN_ERROR,
        UNKNOWN_FUNCTION_NAME,
        UNSUPPORTED_FEATURE,
        EMPTY_EXPRESSION
    };
}
