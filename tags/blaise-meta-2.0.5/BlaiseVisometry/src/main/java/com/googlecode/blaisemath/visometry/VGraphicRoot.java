/**
 * VGraphicRoot.java
 * Created Jan 28, 2011
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkNotNull;
import java.awt.Component;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;

/**
 * <p>
 *      Adds additional {@link Visometry} (local coordinates) support to the parent {@link JGraphicRoot}.
 *      Functionally, this class behaves differently than {@code GraphicRoot}. It maintains the {@code Visometry},
 *      as well as the {@link VisometryProcessor} used to convert entries from local coordinates to view coordinates.
 *      It also maintains the {@link PlottableComposite}, which is another tree of plottable elements that can be
 *      <em>recomputed</em>.
 * </p>
 * <p>
 *      So in all, there are three trees maintained by this class:
 * </p>
 * <ul>
 *      <li>the {@link Graphic} tree maintained by the parent {@link JGraphicRoot};</li>
 *      <li>the {@link VGraphic} tree maintained within the {@link PlottableComposite};</li>
 *      <li>the {@link Plottable} tree maintained within the {@link PlottableComposite}.</li>
 * </ul>
 * <p>
 *      The {@code Plottable} maintains the generic source object, and has the logic to generate one or more {@code VGraphic}s.
 *      Passing from the {@code Plottable} to the {@code VGraphic} requires <em>recomputation</em>
 *          (see {@link Plottable#recompute()} and {@link Plottable#getGraphicEntry()}).
 *      The {@code VGraphic} maintains a graphics primitive in local coordinates.
 *      Passing from the {@code VGraphic} to the {@code Graphic} requires <em>reconversion</em>
 *          (see {@link VGraphic#setUnconverted(boolean)} and {@link VGraphic#getWindowGraphic()}).
 *      The conversion step is completed with the help of the {@code Visometry} and the {@code VisometryProcessor}.
 *      Both the computation and conversion steps are currently completed within
 *      the {@link VGraphicComponent#renderTo(java.awt.Graphics2D)} method.
 * </p>
 * <p>
 *      While in some cases, the duplication of objects may be inefficient,
 *      it allows certain things to be done without expensive recomputation.
 *      This is particularly true when the recomputation steps or conversion steps
 *      take a substantial amount of time, or it makes sense to separate them.
 *      For example, a 3D visometry would maintain 3D graphics primitives, which would
 *      NOT need to be recomputed whenever the view changes. In the above language,
 *      the 3D primitives comprise the {@code VGraphic}.
 * </p>
 * <p>
 *      The separation between {@code Graphic} and {@code VGraphic} also allows for
 *      customizing or manipulating the draw order of the graphics primitives,
 *      which is also important for 3D graphics.
 * </p>
 *
 * @param <C> type of local coordinates
 *
 * @author Elisha Peterson
 */
public class VGraphicRoot<C,G> extends VGraphicComposite<C,G> implements ChangeListener {

    /** Parent component upon which the graphics are drawn. */
    protected Component component;

    /** Stores the underlying visometry */
    protected final Visometry<C> visometry;
    /** Stores the processor used to translate coordinates */
    protected final VisometryProcessor<C> processor = new VisometryProcessor<C>();


    //
    // CONSTRUCTOR
    //

    /** Construct with visometry */
    public VGraphicRoot(Visometry<C> vis) {
        checkNotNull(vis);
        visometry = vis;
        visometry.addChangeListener(this);
        setUnconverted(true);
    }

    /**
     * Sets the component associated with the graphic tree.
     * @param c the component
     */
    void initComponent(Component c) {
        this.component = c;
    }

    //
    // PROPERTY PATTERNS
    //

    @Override
    public Visometry<C> getVisometry() {
        return visometry;
    }

    //
    // EVENT HANDLING
    //

    /**
     * Repaints when a conversion is needed
     * @param en the requestor
     */
    @Override
    public void conversionNeeded(VGraphic<C,G> en) {
        if (component != null) {
            component.repaint();
        }
    }

    @Override
    protected void fireConversionNeeded() {
        if (component != null) {
            component.repaint();
        }
    }

    /**
     * Updates all graphic entries needing conversion from local to window coordinates
     */
    public void reconvert()  {
        convert(visometry, processor);
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == visometry) {
            setUnconverted(true);
            component.repaint();
        }
    }

}
