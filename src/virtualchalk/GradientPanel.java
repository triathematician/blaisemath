/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package virtualchalk;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author ae3263
 */
public class GradientPanel extends JPanel {

    Color color1 = Color.WHITE;
    Color color2 = Color.LIGHT_GRAY;

    public Color getColor1() {
        return color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }    

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        g2.setPaint(new GradientPaint(0, 0, color1, 0, height, color2));
        g2.fillRect(0, 0, width, height);
    }

}
