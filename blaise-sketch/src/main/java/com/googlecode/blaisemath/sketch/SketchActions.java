/**
 * BlaiseSketchActions.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.firestarter.PropertySheetDialog;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.GraphicUtils;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSets;
import com.googlecode.blaisemath.style.Marker;
import com.googlecode.blaisemath.style.editor.AttributeSetPropertyModel;
import com.googlecode.blaisemath.util.Points;
import com.googlecode.blaisemath.util.CoordinateBean;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JOptionPane;

/**
 * Utility library with various actions.
 * @author elisha
 */
public class SketchActions {

    private static DataFlavor AS_DATA_FLAVOR;
    private static DataFlavor DIM_DATA_FLAVOR;
    private static DataFlavor GRAPHIC_DATA_FLAVOR;
    static {
        try {
            AS_DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType 
                    + ";class=com.googlecode.blaisemath.style.AttributeSet");
            DIM_DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType 
                    + ";class=java.awt.Dimension");
            GRAPHIC_DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType 
                    + ";class=com.googlecode.blaisemath.graphics.core.Graphic");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE,
                    "Problem initializing data flavors.", ex);
        }
    }
    
    /** Predicate for those graphics that can be moved. */
    private static final Predicate<Graphic<Graphics2D>> MOVABLE_PREDICATE = new Predicate<Graphic<Graphics2D>>() {
        @Override
        public boolean apply(Graphic<Graphics2D> input) {
            if (!(input instanceof PrimitiveGraphicSupport)) {
                return false;
            }
            Object primitive = ((PrimitiveGraphicSupport) input).getPrimitive();
            return primitive instanceof Point2D
                || primitive instanceof Shape
                || primitive instanceof CoordinateBean;
        }
    };

    /** Get the coordinate of movable graphics */
    private static final Function<Graphic<Graphics2D>,Point2D> LOC_FUNCTION = new Function<Graphic<Graphics2D>,Point2D>() {
        @Nonnull
        @Override
        public Point2D apply(Graphic<Graphics2D> input) {
            checkArgument(input instanceof PrimitiveGraphicSupport);
            return getPrimitiveLocation((PrimitiveGraphicSupport) input);
        }
    };
    
    /** Compare x coordinates of graphics */
    private static final Comparator<Graphic<Graphics2D>> LOC_X_COMPARATOR = new Comparator<Graphic<Graphics2D>>() {
        @Override
        public int compare(Graphic<Graphics2D> o1, Graphic<Graphics2D> o2) {
            return (int) Math.signum(LOC_FUNCTION.apply(o1).getX() - LOC_FUNCTION.apply(o2).getX());
        }
    };
    
    /** Compare y coordinates of graphics */
    private static final Comparator<Graphic<Graphics2D>> LOC_Y_COMPARATOR = new Comparator<Graphic<Graphics2D>>() {
        @Override
        public int compare(Graphic<Graphics2D> o1, Graphic<Graphics2D> o2) {
            return (int) Math.signum(LOC_FUNCTION.apply(o1).getY() - LOC_FUNCTION.apply(o2).getY());
        }
    };
    
    // utility class
    private SketchActions() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="COPY/PASTE">

    /** 
     * Copies the supplied graphic object to the system clipboard.
     * @param sel the graphic to copy
     */
    public static void copy(Graphic<Graphics2D> sel) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Graphic<Graphics2D> copy = SketchGraphicUtils.copy(sel);
        GraphicTransferable transf = new GraphicTransferable(copy);
        clipboard.setContents(transf, transf);
    }
    
    public static boolean isClipboardGraphic() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clipboard.getContents(null);
        return tr.isDataFlavorSupported(GRAPHIC_DATA_FLAVOR);
    }
    
    public static boolean isClipboardDimension() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clipboard.getContents(null);
        return tr.isDataFlavorSupported(DIM_DATA_FLAVOR);
    }
    
    public static boolean isClipboardStyle() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clipboard.getContents(null);
        return tr.isDataFlavorSupported(AS_DATA_FLAVOR);
    }

    /**
     * Pastes a graphic from the system clipboard to the given canvas, at the given location.
     * @param canvas the canvas to paste onto
     * @param canvasLoc location for paste, in the graphic coordinate space
     */
    public static void paste(JGraphicComponent canvas, Point2D canvasLoc) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clipboard.getContents(null);
        if (tr.isDataFlavorSupported(GRAPHIC_DATA_FLAVOR)) {
            try {
                Graphic<Graphics2D> graphic = SketchGraphicUtils.copy((Graphic<Graphics2D>) tr.getTransferData(GRAPHIC_DATA_FLAVOR));
                if (graphic instanceof PrimitiveGraphicSupport) {
                    setPrimitiveLocation((PrimitiveGraphicSupport) graphic, canvasLoc);
                } else {
                    Logger.getLogger(SketchActions.class.getName()).log(Level.WARNING,
                            "Attempted to paste graphic at a specified location failed because the graphic was of type {0}", graphic.getClass());
                }
                canvas.addGraphic(graphic);
                SketchGraphicUtils.configureGraphicTree(graphic, true);
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /** 
     * Copy style of provided graphic.
     * @param gfc what to copy from
     */
    public static void copyStyle(Graphic<Graphics2D> gfc) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        AttributeSetTransferable transf = new AttributeSetTransferable(gfc.getStyle().copy());
        clipboard.setContents(transf, transf);
    }
    
    /**
     * Paste clipboard style onto given graphics. Works whether a graphic or a style
     * is copied onto the clipboard.
     * @param gfcs what to paste on
     */
    public static void pasteStyle(Iterable<Graphic<Graphics2D>> gfcs) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clipboard.getContents(null);
        AttributeSet pasteStyle = null;
        try {
            if (tr.isDataFlavorSupported(AS_DATA_FLAVOR)) {
                pasteStyle = (AttributeSet) tr.getTransferData(AS_DATA_FLAVOR);
            } else if (tr.isDataFlavorSupported(GRAPHIC_DATA_FLAVOR)) {
                pasteStyle = ((Graphic) tr.getTransferData(GRAPHIC_DATA_FLAVOR)).getStyle();
            }
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pasteStyle != null) {
            for (Graphic<Graphics2D> gfc : gfcs) {
                AttributeSet as = gfc.getStyle();
//                if (!(as instanceof ImmutableAttributeSet)) {
                    as.putAll(pasteStyle.getAttributeMap());
//                }
                gfc.getParent().graphicChanged(gfc);
            }
        }
    }
    
    /**
     * Copies the dimension of a graphic onto the clipboard.
     * @param gfc what to copy from
     */
    public static void copyDimension(Graphic<Graphics2D> gfc) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Rectangle2D bds = gfc.boundingBox();
        DimensionTransferable transf = new DimensionTransferable(new Dimension((int) bds.getWidth(), (int) bds.getHeight()));
        clipboard.setContents(transf, transf);
    }
    
    /**
     * Paste the dimension of a graphic onto all targets.
     * @param gfcs what to paste on
     */
    public static void pasteDimension(Iterable<Graphic<Graphics2D>> gfcs) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tr = clipboard.getContents(null);
        Dimension pasteDim = null;
        try {
            if (tr.isDataFlavorSupported(DIM_DATA_FLAVOR)) {
                pasteDim = (Dimension) tr.getTransferData(DIM_DATA_FLAVOR);
            } else if (tr.isDataFlavorSupported(GRAPHIC_DATA_FLAVOR)) {
                Rectangle2D bds = ((Graphic) tr.getTransferData(GRAPHIC_DATA_FLAVOR)).boundingBox();
                pasteDim = new Dimension((int) bds.getWidth(), (int) bds.getHeight());
            }
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pasteDim != null) {
            for (Graphic<Graphics2D> gfc : gfcs) {
                if (gfc instanceof PrimitiveGraphicSupport) {
                    PrimitiveGraphicSupport pgs = (PrimitiveGraphicSupport) gfc;
                    if (pgs.getPrimitive() instanceof RectangularShape) {
                        RectangularShape rsh = (RectangularShape) pgs.getPrimitive();
                        rsh.setFrame(rsh.getX(), rsh.getY(), pasteDim.getWidth(), pasteDim.getHeight());
                        gfc.getParent().graphicChanged(gfc);
                    }
                }
            }
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="EDITING">

    /**
     * Edit the graphic's properties with the given frame as root for the dialog
     * @param frame root for dialog
     * @param src graphic to edit
     */
    public static void editGraphic(@Nullable Frame frame, Graphic<Graphics2D> src) {
        PropertySheetDialog.show(frame, true, src);
    }


    /**
     * Edits the style attributes of the given graphic. Only supports attributes that
     * the graphic already has.
     * @param frame root for dialog
     * @param gr graphics to add attribute to
     */
    public static void editGraphicStyle(@Nullable Frame frame, Graphic<Graphics2D> gr) {
        AttributeSet style = gr.getStyle();
        Map<String,Class<?>> styleClassMap = Maps.newLinkedHashMap();
        for (String k : style.getAllAttributes()) {
            Object sty = style.get(k);
            if (sty instanceof Marker) {
                styleClassMap.put(k, Marker.class);
            } else if (sty != null) {
                styleClassMap.put(k, sty.getClass());
            }
        }
        AttributeSetPropertyModel pModel = new AttributeSetPropertyModel(style, styleClassMap);
        PropertySheetDialog.show(frame, true, style, pModel);
    }

    /**
     * Adds a general attribute to the given graphics.
     * @param parent parent component for editing
     * @param gr graphic to add attribute to
     */
    public static void addAttribute(@Nullable Component parent, Graphic<Graphics2D> gr) {
        addAttribute(parent, Collections.singleton(gr));
    }

    /**
     * Adds a general attribute to the given graphics.
     * @param parent parent component for editing
     * @param gr graphics to add attribute to
     * TODO - support values of other types
     */
    public static void addAttribute(@Nullable Component parent, Iterable<Graphic<Graphics2D>> gr) {
        String name = JOptionPane.showInputDialog(parent, "Enter attribute name:");
        if (Strings.isNullOrEmpty(name)) {
            return;            
        }
        String value = JOptionPane.showInputDialog(parent, "Enter value for attribute "+name+":");
        if (Strings.isNullOrEmpty(value)) {
            return;
        }
        for (Graphic<Graphics2D> g : gr) {
            g.getStyle().put(name, AttributeSets.valueFromString(value));
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="REMOVING">

    /**
     * Delete the given graphic, and also deselect if it is selected.
     * @param src the gfc to delete
     * @param sel selection model
     */
    public static void deleteGraphic(SetSelectionModel<Graphic<Graphics2D>> sel, Graphic<Graphics2D> src) {
        sel.deselect(src);
        GraphicComposite gc = src.getParent();
        if (gc != null) {
            gc.removeGraphic(src);
        } else {
            src.setParent(null);
        }
    }
    
    /**
     * Delete the graphics currently selected.
     * @param sel selection model
     */
    public static void deleteSelected(SetSelectionModel<Graphic<Graphics2D>> sel) {
        Set<Graphic<Graphics2D>> selection = sel.getSelection();
        for (Graphic g : selection) {
            GraphicComposite gc = g.getParent();
            if (gc != null) {
                gc.removeGraphics(selection);
            }
        }
        sel.setSelection(Collections.EMPTY_SET);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LOCKING">
    
    public static void setLockPosition(Set<Graphic<Graphics2D>> gfcs, boolean lock) {
        for (Graphic<Graphics2D> g : gfcs) {
            setLockPosition(g, lock);
        }
    }
    
    public static void setLockPosition(Graphic<Graphics2D> toLock, boolean lock) {
        if (toLock instanceof PrimitiveGraphicSupport) {
            ((PrimitiveGraphicSupport) toLock).setDragEnabled(!lock);
        } else {
            logUnsupported("Position locking", toLock);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="GROUPING">
    
    /**
     * Group the selected set of graphics within the selected composite.
     * @param sel the elements to group
     * @param composite the composite graphic containing the elements
     */
    public static void groupSelected(GraphicComposite<Graphics2D> composite, SetSelectionModel<Graphic<Graphics2D>> sel) {
        List<Graphic<Graphics2D>> selection = GraphicUtils.zOrderSort(sel.getSelection());
        
        // try to reuse the existing parent graphic if possible
        Set<GraphicComposite> parents = Sets.newHashSet();
        for (Graphic g : selection) {
            parents.add(g.getParent());
        }
        parents.remove(null);
        if (parents.isEmpty()) {
            throw new IllegalStateException();
        } else if (parents.size() == 1) {
            composite = Iterables.getFirst(parents, null);
        }
        
        // create the new group and add it to the new parent
        GraphicComposite<Graphics2D> groupGraphic = new GraphicComposite<Graphics2D>();
        SketchGraphicUtils.configureGraphic(groupGraphic);
        groupGraphic.addGraphics(selection);
        composite.addGraphic(groupGraphic);
        
        sel.setSelection(Sets.<Graphic<Graphics2D>>newHashSet(groupGraphic));
    }
    
    /**
     * Ungroup the items in the provided selection.
     * @param sel the selection
     */
    public static void ungroupSelected(SetSelectionModel<Graphic<Graphics2D>> sel) {
        Set<Graphic<Graphics2D>> selection = sel.getSelection();
        checkState(selection.size() == 1);
        Graphic<Graphics2D> gfc = Iterables.getFirst(selection, null);
        checkState(gfc instanceof GraphicComposite);
        GraphicComposite<Graphics2D> gc = (GraphicComposite<Graphics2D>) gfc;
        Iterable<Graphic<Graphics2D>> childGfcs = gc.getGraphics();
        GraphicComposite<Graphics2D> par = gc.getParent();
        par.removeGraphic(gc);
        par.addGraphics(Lists.newArrayList(childGfcs));
        
        sel.setSelection(Sets.newHashSet(childGfcs));
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ARRANGING">

    public static void moveForward(Set<Graphic<Graphics2D>> selection) {
        for (Graphic<Graphics2D> g : selection) {
            moveForward(g);
        }
    }

    public static void moveForward(Graphic<Graphics2D> gr) {
        GraphicComposite<Graphics2D> parent = gr.getParent();
        List<Graphic<Graphics2D>> gfcs = Lists.newArrayList(parent.getGraphics());
        int idx = gfcs.indexOf(gr);
        if (idx != gfcs.size() - 1) {
            gfcs.remove(gr);
            gfcs.add(idx+1, gr);
        }
        parent.replaceGraphics(Lists.newArrayList(parent.getGraphics()), gfcs);
    }

    public static void moveBackward(Set<Graphic<Graphics2D>> selection) {
        for (Graphic<Graphics2D> g : selection) {
            moveBackward(g);
        }
    }

    public static void moveBackward(Graphic<Graphics2D> gr) {
        GraphicComposite<Graphics2D> parent = gr.getParent();
        List<Graphic<Graphics2D>> gfcs = Lists.newArrayList(parent.getGraphics());
        int idx = gfcs.indexOf(gr);
        if (idx != 0) {
            gfcs.remove(gr);
            gfcs.add(idx-1, gr);
        }
        parent.replaceGraphics(Lists.newArrayList(parent.getGraphics()), gfcs);
    }

    public static void moveToFront(Set<Graphic<Graphics2D>> selection) {
        for (Graphic<Graphics2D> g : selection) {
            moveToFront(g);
        }
    }

    public static void moveToFront(Graphic<Graphics2D> gr) {
        GraphicComposite<Graphics2D> parent = gr.getParent();
        List<Graphic<Graphics2D>> gfcs = Lists.newArrayList(parent.getGraphics());
        int idx = gfcs.indexOf(gr);
        if (idx != gfcs.size() - 1) {
            gfcs.remove(gr);
            gfcs.add(gfcs.size(), gr);
        }
        parent.replaceGraphics(Lists.newArrayList(parent.getGraphics()), gfcs);
    }

    public static void moveToBack(Set<Graphic<Graphics2D>> selection) {
        for (Graphic<Graphics2D> g : selection) {
            moveToBack(g);
        }
    }

    public static void moveToBack(Graphic<Graphics2D> gr) {
        GraphicComposite<Graphics2D> parent = gr.getParent();
        List<Graphic<Graphics2D>> gfcs = Lists.newArrayList(parent.getGraphics());
        int idx = gfcs.indexOf(gr);
        if (idx != 0) {
            gfcs.remove(gr);
            gfcs.add(0, gr);
        }
        parent.replaceGraphics(Lists.newArrayList(parent.getGraphics()), gfcs);
    }
    
    @Nonnull
    private static Point2D getPrimitiveLocation(PrimitiveGraphicSupport<?,Graphics2D> gr) {
        Object prim = gr.getPrimitive();
        if (prim instanceof Point2D) {
            return (Point2D) prim;
        } else if (prim instanceof CoordinateBean) {
            return ((CoordinateBean<Point2D>) prim).getPoint();
        } else if (prim instanceof RectangularShape) {
            return new Point2D.Double(((RectangularShape) prim).getMinX(), ((RectangularShape) prim).getMinY());
        } else if (prim instanceof Shape) {
            return new Point2D.Double(((Shape) prim).getBounds2D().getMinX(), ((Shape) prim).getBounds2D().getMinY());
        } else {
            throw new IllegalStateException();
        }
    }
    
    private static <O> void setPrimitiveLocation(PrimitiveGraphicSupport<O,Graphics2D> gr, Point2D loc) {
        Object prim = gr.getPrimitive();
        if (prim instanceof Point2D) {
            ((Point2D)prim).setLocation(loc);
            // TODO - fire event recognizing change
        } else if (prim instanceof CoordinateBean) {
            ((CoordinateBean<Point2D>)prim).setPoint(loc);
            // TODO - fire event recognizing change
        } else if (prim instanceof RectangularShape) {
            RectangularShape rsh = (RectangularShape) prim;
            rsh.setFrame(loc.getX(), loc.getY(), rsh.getWidth(), rsh.getHeight());
            // TODO - fire event recognizing change
        } else if (prim instanceof Shape) {
            Point2D loc0 = getPrimitiveLocation(gr);
            AffineTransform at = new AffineTransform();
            at.translate(loc.getX()-loc0.getX(), loc.getY()-loc0.getY());
            gr.setPrimitive((O) at.createTransformedShape((Shape) prim));
        } else {
            throw new IllegalStateException();
        }
    }
    
    public static void alignHorizontal(Set<Graphic<Graphics2D>> selection) {
        Iterable<Graphic<Graphics2D>> sub = Iterables.filter(selection, MOVABLE_PREDICATE);
        if (Iterables.size(sub) < 2) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.INFO,
                    "Cannot align with fewer than 2 movable predicates.");
            return;
        }
        ImmutableMap<Graphic<Graphics2D>,Point2D> locs = Maps.toMap(sub, LOC_FUNCTION);
        double yAvg = Points.average(locs.values()).getY();
        for (Graphic<Graphics2D> gr : sub) {
            setPrimitiveLocation((PrimitiveGraphicSupport<?, Graphics2D>) gr, new Point2D.Double(locs.get(gr).getX(), yAvg));
        }
    }

    public static void alignVertical(Set<Graphic<Graphics2D>> selection) {
        Iterable<Graphic<Graphics2D>> sub = Iterables.filter(selection, MOVABLE_PREDICATE);
        if (Iterables.size(sub) < 2) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.INFO,
                    "Cannot align with fewer than 2 movable predicates.");
            return;
        }
        ImmutableMap<Graphic<Graphics2D>,Point2D> locs = Maps.toMap(sub, LOC_FUNCTION);
        double xAvg = Points.average(locs.values()).getY();
        for (Graphic<Graphics2D> gr : sub) {
            setPrimitiveLocation((PrimitiveGraphicSupport<?, Graphics2D>) gr, new Point2D.Double(xAvg, locs.get(gr).getY()));
        }
    }

    public static void distributeHorizontal(Set<Graphic<Graphics2D>> selection) {
        List<Graphic<Graphics2D>> sub = Lists.newArrayList(Iterables.filter(selection, MOVABLE_PREDICATE));
        int count = Iterables.size(sub);
        if (count < 2) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.INFO,
                    "Cannot distribute with fewer than 2 movable predicates.");
            return;
        }
        Collections.sort(sub, LOC_X_COMPARATOR);
        double minx = LOC_FUNCTION.apply(sub.get(0)).getX();
        double maxx = LOC_FUNCTION.apply(sub.get(sub.size()-1)).getX();
        double dx = (maxx-minx)/(count-1);
        double gx = minx;
        for (Graphic<Graphics2D> gr : sub) {
            setPrimitiveLocation((PrimitiveGraphicSupport<?, Graphics2D>) gr, new Point2D.Double(gx, LOC_FUNCTION.apply(gr).getY()));
            gx += dx;
        }
    }

    public static void distributeVertical(Set<Graphic<Graphics2D>> selection) {
        List<Graphic<Graphics2D>> sub = Lists.newArrayList(Iterables.filter(selection, MOVABLE_PREDICATE));
        int count = Iterables.size(sub);
        if (count < 2) {
            Logger.getLogger(SketchActions.class.getName()).log(Level.INFO,
                    "Cannot distribute with fewer than 2 movable predicates.");
            return;
        }
        Collections.sort(sub, LOC_Y_COMPARATOR);
        double miny = LOC_FUNCTION.apply(sub.get(0)).getY();
        double maxy = LOC_FUNCTION.apply(sub.get(sub.size()-1)).getY();
        double dy = (maxy-miny)/(count-1);
        double gy = miny;
        for (Graphic<Graphics2D> gr : sub) {
            setPrimitiveLocation((PrimitiveGraphicSupport<?, Graphics2D>) gr, new Point2D.Double(LOC_FUNCTION.apply(gr).getX(), gy));
            gy += dy;
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="UTILITY METHODS">
    
    private static void logUnsupported(String op, Graphic gfc) {
        Logger.getLogger(SketchActions.class.getName()).log(Level.WARNING,
                "{0} not supported for graphic of type {1}", new Object[]{op, gfc.getClass()});
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="HELPER CLASSES">
        
    private static class TypedTransferable<C> implements Transferable, ClipboardOwner {
        private final C object;
        private final DataFlavor flavor;
        private TypedTransferable(C object, DataFlavor flavor) {
            this.object = object;
            this.flavor = flavor;
        }
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { flavor };
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(this.flavor);
        }
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (flavor.equals(this.flavor)) {
                return object;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    }
        
    private static class AttributeSetTransferable extends TypedTransferable<AttributeSet> {
        private AttributeSetTransferable(AttributeSet object) {
            super(object, AS_DATA_FLAVOR);
        }
    }
        
    private static class DimensionTransferable extends TypedTransferable<Dimension> {
        private DimensionTransferable(Dimension object) {
            super(object, DIM_DATA_FLAVOR);
        }
    }
    
    private static class GraphicTransferable extends TypedTransferable<Graphic> {
        private GraphicTransferable(Graphic object) {
            super(object, GRAPHIC_DATA_FLAVOR);
        }
    }
    
    //</editor-fold>
    
}
