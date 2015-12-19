/*
 * Copyright 2015 elisha.
 *
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
 */
package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elisha
 */
    public class TestPropertyModel extends PropertyModelSupport {

        /** Properties to edit */
        public final List<String> props;
        /** Types of the properties */
        public final Map<String,Class> types;
        /** Map being edited */
        public final Map<String,Object> map;

        public TestPropertyModel(Map<String, Class> types, Map<String, Object> map) {
            this.props = new ArrayList<String>(types.keySet());
            this.types = types;
            this.map = map;
        }

        @Override
        public int getSize() {
            return props.size();
        }

        @Override
        public String getElementAt(int index) {
            return props.get(index);
        }

        @Override
        public Class getPropertyType(int row) {
            return types.get(props.get(row));
        }

        @Override
        public boolean isWritable(int row) {
            return true;
        }

        @Override
        public Object getPropertyValue(int row) {
            return map.get(props.get(row));
        }

        @Override
        public void setPropertyValue(int row, Object value) {
            map.put(props.get(row), value);
        }

    }
