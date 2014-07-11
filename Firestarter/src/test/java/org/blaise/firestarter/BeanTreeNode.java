/**
 * BeanTreeNode.java
 * Created on Aug 19, 2009
 */
package org.blaise.firestarter;

/*
 * #%L
 * Firestarter
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

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.blaise.firestarter.editor.EditorRegistration;

/**
 * <p>
 *   <code>BeanTreeNode</code> adds the sub-properties of a bean object (that do
 *   not have registered editors) as children of the tree.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BeanTreeNode extends DefaultMutableTreeNode {

    /** Object of this class. */
    Object bean;
    /** The info of the bean. */
    BeanInfo info;
    /** Property descriptors. */
    PropertyDescriptor[] descriptors;

    public BeanTreeNode(Object bean) {
        super(bean);
        if (bean != null) {
            this.bean = bean;
            info = BeanEditorSupport.getBeanInfo(bean.getClass());
            System.out.println("adding node: " + bean);
            descriptors = info.getPropertyDescriptors();
            addSubNodes();
        }
    }

    /** Creates and adds subnodes, which are the "non-terminal" properties of the bean. */
    void addSubNodes() {
        for (int i = 0; i < descriptors.length; i++) {
            if (!BeanFilterRule.STANDARD.apply(descriptors[i])) {
                // required to prevent infinite loop!
                continue;
            } else if (descriptors[i] instanceof IndexedPropertyDescriptor) {
                try {
                    // add all array elements
                    Object[] elts = (Object[]) descriptors[i].getReadMethod().invoke(bean);
                    for (int j = 0; j < elts.length; j++) {
                        add(new BeanTreeNode(elts[j]));
                    }
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(BeanTreeNode.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(BeanTreeNode.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(BeanTreeNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // add only elements not supporting custom editors
                PropertyEditor editor = EditorRegistration.getEditor(bean, descriptors[i]);
                if (!editor.supportsCustomEditor()) {
                    add(new BeanTreeNode(editor.getValue()));
                }
            }
        }
    }
}
