/*-
 * #%L
 * blaise-graphics-ui-kt-0.1.0-SNAPSHOT
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
import javax.swing.JPanel
import java.beans.Customizer
import java.beans.PropertyChangeListener
import javax.swing.JSpinner
import javax.swing.JComboBox
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import javax.swing.JLabel
import javax.swing.SpinnerNumberModel
import java.awt.event.ActionListener
import javax.swing.event.ChangeEvent
import java.beans.PropertyChangeEvent
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.Icon
import java.awt.Graphics2D
import java.awt.RenderingHints
import com.googlecode.blaisemath.firestarter.property.PropertyModelSupport
import com.googlecode.blaisemath.firestarter.property.PropertySheet
import javax.swing.event.ListDataListener
import javax.swing.event.ListDataEvent
import java.awt.event.MouseAdapter
import javax.swing.JPopupMenu
import javax.swing.AbstractAction
import javax.swing.UIManager
import javax.swing.JFrame
import javax.swing.SwingUtilities
import com.googlecode.blaisemath.firestarter.property.PropertySheetDialog
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.Arrays
import kotlin.jvm.JvmOverloads
import javax.swing.DefaultListModel
import java.util.Collections
import java.util.LinkedHashMap
import javax.swing.JScrollPane
import java.awt.GradientPaint
import com.googlecode.blaisemath.firestarter.editor.MPanelEditorSupport
import javax.swing.DefaultComboBoxModel
import com.googlecode.blaisemath.firestarter.swing.RollupPanel
import kotlin.jvm.JvmStatic
import java.lang.Runnable
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration
import java.beans.PropertyEditorManager
import java.awt.GridLayout
import javax.swing.UIManager.LookAndFeelInfo
import java.lang.ClassNotFoundException
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import javax.swing.UnsupportedLookAndFeelException
import java.lang.IllegalAccessException
import java.lang.InstantiationException
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import javax.swing.JToolBar
import javax.swing.JButton
import javax.imageio.ImageIO
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import java.io.IOException
import com.googlecode.blaisemath.firestarter.swing.MPanel
import java.awt.geom.GeneralPath
import javax.swing.JOptionPane
import java.util.HashSet
import com.googlecode.blaisemath.app.ApplicationMenuConfig
