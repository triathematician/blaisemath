/*-
 * #%L
 * blaise-graphics-kt-0.1.0-SNAPSHOT
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
import java.util.ServiceLoader
import kotlin.Throws
import java.io.IOException
import java.lang.IllegalAccessException
import java.lang.reflect.InvocationTargetException
import java.lang.NoSuchMethodException
import java.awt.Graphics2D
import java.awt.Dimension
import java.awt.geom.AffineTransform
import java.text.DecimalFormat
import java.lang.StringBuilder
import java.lang.NumberFormatException
import kotlin.jvm.JvmOverloads
import java.util.Arrays
import java.lang.Runnable
import javax.swing.SwingUtilities
import java.util.stream.Collectors
import java.util.LinkedHashSet
import javax.swing.JPopupMenu
import java.awt.Stroke
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.geom.GeneralPath
import java.awt.RenderingHints
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.awt.image.BufferedImage
import java.awt.GradientPaint
import java.awt.AlphaComposite
import javax.swing.Icon
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.event.PopupMenuListener
import javax.swing.event.PopupMenuEvent
import javax.swing.JComponent
import java.lang.IllegalStateException
import java.awt.event.MouseAdapter
import java.awt.event.MouseWheelEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeSupport
import java.util.Collections
import java.util.HashSet
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import junit.framework.TestCase
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.jvm.JvmStatic
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import javax.swing.AbstractAction
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
