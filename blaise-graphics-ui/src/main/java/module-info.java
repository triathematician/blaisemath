/*-
 * #%L
 * blaise-graphics-ui
 * --
 * Copyright (C) 2019 - 2021 Elisha Peterson
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
module com.googlecode.blaisemath.graphics.ui {
    requires java.desktop;

    requires com.google.common;
    requires org.checkerframework.checker.qual;

    requires com.googlecode.blaisemath.firestarter;
    requires com.googlecode.blaisemath.common;
    requires com.googlecode.blaisemath.graphics;

    exports com.googlecode.blaisemath.palette.ui;
    exports com.googlecode.blaisemath.primitive.ui;
    exports com.googlecode.blaisemath.style.ui;
}
