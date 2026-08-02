/*-
 * #%L
 * blaise-graph-theory-ui-kt-0.1.0-SNAPSHOT
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
import java.beans.PropertyChangeListener
import com.googlecode.blaisemath.graph.view.VisualGraph
import java.beans.PropertyChangeEvent
import kotlin.jvm.JvmOverloads
import java.awt.Graphics2D
import java.util.stream.Collectors
import com.googlecode.blaisemath.graph.view.GraphComponent
import java.awt.Dimension
import java.awt.event.HierarchyListener
import java.awt.event.HierarchyEvent
import com.googlecode.blaisemath.graph.view.WeightedEdgeStyler
import com.googlecode.blaisemath.graph.app.GraphAppFrameView
import kotlin.jvm.JvmStatic
import com.googlecode.blaisemath.graph.app.GraphApp
import com.googlecode.blaisemath.graph.app.AnimationUtils
import com.googlecode.blaisemath.graph.app.MetricScaler
import com.googlecode.blaisemath.graph.app.GraphAppCanvas
import com.googlecode.blaisemath.app.PropertyActionPanel
import javax.swing.JComboBox
import javax.swing.JLabel
import com.googlecode.blaisemath.firestarter.swing.MPanel
import javax.swing.ActionMap
import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.app.PresetMenuInitializer
import javax.swing.JPopupMenu
import java.io.IOException
import javax.swing.JPanel
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JSeparator
import javax.swing.SwingConstants
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration
import java.beans.PropertyEditorManager
import java.awt.event.ActionListener
import com.googlecode.blaisemath.firestarter.swing.RollupPanel
import java.awt.BorderLayout
import javax.swing.JScrollPane
import java.util.LinkedHashSet
import java.util.HashSet
import java.util.stream.IntStream
import java.util.function.IntFunction
import javax.swing.JFrame
import javax.swing.JToolBar
import javax.swing.JButton
import java.lang.Runnable
import com.googlecode.blaisemath.graph.test.GraphTestFrame
import com.googlecode.blaisemath.firestarter.property.PropertySheet
import java.awt.event.WindowAdapter
import javax.swing.SwingUtilities
import java.util.TimerTask
import com.googlecode.blaisemath.graph.test.DynamicGraphTestFrame
