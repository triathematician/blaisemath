package com.googlecode.blaisemath.annotation;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to document information about the thread(s) that will
 * be invoking a method. Typical values used include "{@code EDT}" for the swing
 * event-dispatch thread, "{@code multiple}" where the method may be called from
 * multiple threads, or "{@code unknown}" where the thread calling the method is
 * not known. Other values can also be used. This annotation generally "propagates"
 * in two ways. If a method has an {@code InvokedFromThread} annotation, any
 * overriding methods should have the same annotation, and any other methods
 * called within that method should have the annotation (although the scope might
 * be widened, e.g. moving from {@code InvokedFromThread("my-thread")} to
 * {@code InvokedFromThread("unknown")}).
 * 
 * <p> This annotation is only intended to be applied where the thread is not
 * understood from context, or when invalid assumptions might be made. Thread-safe
 * classes, for example, have methods assumed to be accessible from arbitrary
 * threads, and don't need to be annotated. However, any other method invoked
 * within one of those methods might have the annotation {@code InvokedFromThread("multiple")}
 * or {@code InvokedFromThread("unknown")}.
 * 
 * @author Elisha Peterson
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface InvokedFromThread {
    /**
     * What thread the method is invoked from.
     * @return string description of the thread, or {@code "unknown"} or {@code "multiple"}
     */
    String value();
    
}
