/*
 */
package org.blaise.firestarter.editor;

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class ChooserComboPopup extends JPopupMenu {

    ColorEditor ce;

    public ChooserComboPopup(ColorEditor c) {
        super();
        this.ce = c;
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        final SwatchChooserPanel s = new SwatchChooserPanel(c, this);
        s.buildChooser();
        p.add(s, BorderLayout.NORTH);
        JButton b = new JButton("Other ...");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = null;
                color = JColorChooser.showDialog(getParent(), "Color Chooser", color);
                ce.setNewValue(color);
                ce.initEditorValue();
                setVisible(false);
            }
        });
        p.add(b, BorderLayout.SOUTH);
        add(p);
        s.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) { setVisible(false); }
        });

    }

    public ColorEditor getCE() {
        return this.ce;
    }
}
