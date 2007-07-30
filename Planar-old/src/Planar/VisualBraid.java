package Planar;

import Interface.BPlottable;
import PlanarAlgebra.FiniteGridGeometry;
import PlanarAlgebra.PermutationElement;
import PlanarAlgebra.PermutationGroup;
import PlanarAlgebra.PermutationModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

/**
 * <b>VisualPermutation.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 9, 2007, 10:12 AM</i><br><br>
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class VisualBraid extends JComponent implements BPlottable{
    // basic parameters
    private Point click1,click2;
    private FiniteGridGeometry geo;
    private PermutationModel p;
    private PermutationElement pOld;
    private int pOldLength;
    
    // visual style parameters
    private final Color[] cycleColors={
        Color.DARK_GRAY,
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.ORANGE,
        Color.PINK,
        Color.CYAN,
        Color.MAGENTA,
        Color.GRAY};
    private boolean colorCycles=true;
    private boolean directed=false;
    private boolean thick=false;
    
    /** Constructor: creates a new instance of VisualPermutation */
    public VisualBraid(){super();initializeModels();}
    public VisualBraid(JPanel panel){super();initializeModels(panel);}
    
    public void initializeModels(JPanel panel){
        geo=new FiniteGridGeometry(panel,20,5);
        p=new PermutationModel();
    }
    public void initializeModels(){p=new PermutationModel();geo=new FiniteGridGeometry(new JPanel(), 20,5);}
    
    public void setX(int wx){}
    public void setY(int wy){}
    
    public void setGeometry(FiniteGridGeometry geo){this.geo=geo;}
    public void setModel(PermutationModel p){this.p=p;}
    public PermutationModel getModel(){return p;}
    
    public void stateChanged(ChangeEvent e){}
    public void paintComponent(Graphics grb){
        Graphics2D gr=(Graphics2D)grb;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if(!colorCycles){
            gr.setColor(cycleColors[1]);
            Point[][] pp=p.getValue().getPairs();
            for(int i=0;i<p.getLength();i++){
                gr.draw(new Line2D.Double(geo.toWindow(pp[i][0]),geo.toWindow(pp[i][1])));
            }
        } else {
            Vector<Vector<Point[]>> ppp=p.getValue().getCyclePairs();
            for(int i=0;i<ppp.size();i++){
                gr.setColor(cycleColors[i]);
                for(Point[] p:ppp.get(i)){
                    gr.draw(new Line2D.Double(geo.toWindow(p[0]),geo.toWindow(p[1])));
                }
            }
        }
    }
    
    // mouse events
    public boolean clicked(MouseEvent e){return false;}
    public void mouseClicked(MouseEvent e){
        //     if(click1==null){click1=geo.clicked(e);}
    }
    public void mousePressed(MouseEvent e){
        click1=geo.closest(e);
        if(click1!=null){pOld=p.getValue();pOldLength=pOld.getN();}
    }
    public void mouseReleased(MouseEvent e){
        click1=null;click2=null;
        /**        if(click1!=null){
         * click2=geo.clicked(e);
         * if(click2!=null){
         * if((click1.y==0)&&(click2.y==0)){ // both bottom clicks... compose with swap on the left!
         * PermutationElement pNew=PermutationGroup.getSwap(click1.x,click2.x,pOldLength);
         * pNew.composeOnRightWith(pOld);
         * p.setValue(pNew);
         * } else if((click1.y==1)&&(click2.y==1)){ // both top clicks... compose with swap on the right!
         * PermutationElement pNew=PermutationGroup.getSwap(click1.x,click2.x,pOldLength);
         * pNew.composeOnLeftWith(pOld);
         * p.setValue(pNew);
         * }
         * click2=null;
         * }
         * click1=null;
         * }**/
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseDragged(MouseEvent e){
        if(click1!=null){
            click2=geo.closest(e);
            if(click2!=null){
                if((click1.y==0)&&(click2.y==0)){ // both bottom clicks... compose with swap on the left!
                    PermutationElement pNew=PermutationGroup.getSwap(click1.x,click2.x,pOldLength);
                    pNew.composeOnRightWith(pOld);
                    p.setValue(pNew);
                } else if((click1.y==1)&&(click2.y==1)){ // both top clicks... compose with swap on the right!
                    PermutationElement pNew=PermutationGroup.getSwap(click1.x,click2.x,pOldLength);
                    pNew.composeOnLeftWith(pOld);
                    p.setValue(pNew);
                }
            }
        }
    }
    public void mouseMoved(MouseEvent e){}

    public double getGeoX() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getGeoY() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setX(double wx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setY(double wy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void paintComponent(Graphics2D g, AffineTransform at) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
