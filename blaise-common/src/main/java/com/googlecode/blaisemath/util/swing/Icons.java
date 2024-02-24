package com.googlecode.blaisemath.util.swing;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2024 Elisha Peterson
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

import com.googlecode.blaisemath.util.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;

/**
 * Utilities for creating icons.
 * 
 * @author Elisha Peterson
 */
public class Icons {

    // utility class
    private Icons() {
    }
    
    /**
     * Create icon that composites one over another.
     * @param icons array of icons, in the order they will be drawn
     * @return composite icon
     */
    public static Icon composite(Icon... icons) {
        return new CompositeIcon(icons);
    }

    /**
     * Create an icon by joining several horizontally.
     * @param icons the icons
     * @return joined icon
     */
    public static Icon join(Icon... icons) {
        return new JoinIcon(icons);
    }
    
    /**
     * Create an icon with a letter (or text string), a color, and a size.
     * The icon displays a solid circle overlaid with the letter, using varying
     * shades of the provided color.
     * @param letter the icon letter/text
     * @param color the color
     * @param size the icon size
     * @return icon
     */
    public static Icon letterIcon(String letter, Color color, int size) {
        return new LetterIcon(letter, color, size);
    }
    
    //region INNER CLASSES
    
    /** An icon that joins several other icons together on top of each other. */
    private static final class CompositeIcon implements Icon {

        private final Icon[] icons;

        private CompositeIcon(Icon... icons) {
            this.icons = icons;
        }

        @Override
        public int getIconWidth() {
            int max = 0;
            for (Icon i : icons) {
                max = Math.max(max, i.getIconWidth());
            }
            return max;
        }

        @Override
        public int getIconHeight() {
            int max = 0;
            for (Icon i : icons) {
                max = Math.max(max, i.getIconHeight());
            }
            return max;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            for (Icon i : icons) {
                i.paintIcon(c, g, x, y);
            }
        }
    }
    
    /** An icon that joins several other icons together horizontally. */
    private static final class JoinIcon implements Icon {

        private final Icon[] icons;

        private JoinIcon(Icon... icons) {
            this.icons = icons;
        }

        @Override
        public int getIconWidth() {
            int sum = 0;
            for (Icon i : icons) {
                sum += i.getIconWidth();
            }
            return sum;
        }

        @Override
        public int getIconHeight() {
            int max = 0;
            for (Icon i : icons) {
                max = Math.max(max, i.getIconHeight());
            }
            return max;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int xp = x;
            for (Icon i : icons) {
                i.paintIcon(c, g, xp, y);
                xp += i.getIconWidth();
            }
        }
    }

    /** An icon that displays a text string against a background shape */
    private static final class LetterIcon extends SquareIcon {

        private static final String FONTNAME = "Dialog";

        private final String letter;
        private final Color color;

        private LetterIcon(String d, Color c, int size) {
            super(size);
            this.letter = d;
            this.color = c;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Colors.alpha(Colors.lighterThan(color), 128));
            g2.fill(new Ellipse2D.Double(x + 1, y + 1, size - 2, size - 2));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new Ellipse2D.Double(x + 1, y + 1, size - 2, size - 2));
            g2.setFont(new Font(FONTNAME, Font.BOLD, size - 5));
            g2.setColor(color);
            String lett = letter;
            Rectangle2D bds = g2.getFontMetrics().getStringBounds(lett, g);
            LineMetrics lm = g2.getFontMetrics().getLineMetrics(lett, g);
            g2.drawString(lett,
                    x + .5f * size - .5f * (float) bds.getWidth(),
                    y + .5f * (size + 1) + .5f * (float) bds.getHeight()
                    + .5f * (lm.getAscent() + lm.getDescent()) - lm.getAscent());
        }
    }
    
    //endregion
}
