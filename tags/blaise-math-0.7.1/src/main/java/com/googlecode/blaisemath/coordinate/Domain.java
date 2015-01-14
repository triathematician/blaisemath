/*
 * Domain.java
 * Created on Nov 18, 2009
 */

package com.googlecode.blaisemath.coordinate;

/*
 * #%L
 * BlaiseMath
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

/**
 * <p>
 *   Provides a method describing whether a point of specified type is in the domain.
 * </p>
 * @author Elisha Peterson
 */
public interface Domain<C> {

    /**
     * Determines whether provided coordinate is in the domain
     * @param coord coordinate
     * @return <code>true</code> if <code>coord</code> is in the domain, otherwise false
     */
    public boolean contains(C coord);

}
