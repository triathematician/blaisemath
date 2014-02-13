/**
 * ContextMenuInitializer.java
 * Created Aug 25, 2012
 */
package org.blaise.util;

import java.awt.geom.Point2D;
import java.util.Set;
import javax.annotation.Nullable;
import javax.swing.JPopupMenu;

/**
 * Provides a method that can be used to initialize (add actions to) a {@link JPopupMenu}.
 * The initializer will be provided as argument the <i>source</i> object/graphic
 * that is creating the menu, the <i>location</i> where it is being shown,
 * an optional <i>focus</i> object describing a more specific target for the menu,
 * and an optional <i>selection</i> of objects.
 *
 * @param <S> focus object type for menu
 * 
 * @author Elisha
 */
public interface ContextMenuInitializer<S> {

    /**
     * Initialize the context menu by adding any actions appropriate for the given parameters.
     * @param popup context menu
     * @param src source for context menu
     * @param point mouse location
     * @param focus object of focus
     * @param selection current selection (null's okay)
     */
    void initContextMenu(JPopupMenu popup, S src, Point2D point, @Nullable Object focus, @Nullable Set selection);

}
