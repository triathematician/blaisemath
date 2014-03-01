/*
 * VisibilityHint.java
 * Created Jan 16, 2011
 */

package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
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
 * The visibility status of a shape, used to set whether a shape is drawn,
 * hidden, highlighted, or otherwise emphasized.
 *
 * @author Elisha
 */
public enum VisibilityHint {
    /** HIGHLIGHTed setting */
    HIGHLIGHT,
    /** SELECTED item */
    SELECTED,
    /** FADED, used to de-emphasize */
    FADED,
    /** Draw outline only */
    OUTLINE,
    /** This hint means the graphic is not drawn and receives no events from its parent. */
    HIDDEN;
}
