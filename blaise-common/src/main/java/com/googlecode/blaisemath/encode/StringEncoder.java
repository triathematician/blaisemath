package com.googlecode.blaisemath.encode;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converts an object to a string, preferably in a way that can be also decoded.
 * Encoders may or may not handle null objects, and may or may not return null/empty strings.
 * Implementations should use {@link Nullable} annotations to indicate how they handle nulls.
 * 
 * @param <X> type of object to converter
 * @author Elisha Peterson
 */
public interface StringEncoder<X> {
    
    /**
     * Encode the object as a string.
     * @param obj object to encode
     * @return string value
     */
    String encode(X obj); 
    
}
