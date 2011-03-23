/*
 * TestGraphics.java
 * Created on Jan 18, 2011, 6:13:29 AM
 */



import org.bm.blaise.graphics.renderer.BasicShapeRenderer;
import org.bm.blaise.graphics.renderer.BasicPointRenderer;
import org.bm.blaise.graphics.renderer.BasicStrokeRenderer;
import org.bm.blaise.graphics.compound.ArrowEntry;
import org.bm.blaise.graphics.compound.LabeledPointEntry;
import org.bm.blaise.graphics.*;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.*;

/**
 *
 * @author Elisha
 */
public class TestGraphics extends javax.swing.JFrame {

    BasicShapeEntry e1;
    CompositeGraphicEntry e2;

    /** Creates new form TestGraphics */
    public TestGraphics() {
        initComponents();
        sc.getCache().addEntry(e1 = new BasicShapeEntry(
                new Ellipse2D.Double(50,75,20.0,30.0),
                new BasicShapeRenderer(Color.gray, Color.black)
                ));
        e1.setTooltip("my tooltip");
        e1.setMouseListener(new GraphicMouseListener.Adapter(){
            @Override public void mouseEntered(GraphicMouseEvent e) { e1.setVisibility(GraphicVisibility.Highlight); System.out.println("entered"); }
            @Override public void mouseExited(GraphicMouseEvent e) { e1.setVisibility(GraphicVisibility.Regular); System.out.println("exited"); }
            @Override public void mouseClicked(GraphicMouseEvent e) { System.out.println("clicked"); for(int i=0;i<50;i++) addShape(); }
        });
        sc.getCache().addEntry(e2 = new CompositeGraphicEntry());
    }

    static int i = 1;

    private void addShape() {
        AbstractGraphicEntry en;
        Shape s;
        Point2D.Double p1,p2;
        if (Math.random()<.2) {
            e2.addEntry(en = new BasicShapeEntry(
                s = new RoundRectangle2D.Double(100+200*Math.random(), 100+200*Math.random(), 200*Math.random(), 200*Math.random(), 10, 10),
                new BasicShapeRenderer(
                    new Color((float)Math.random(),(float)Math.random(),(float)Math.random()),
                    new Color((float)Math.random(),(float)Math.random(),(float)Math.random()),
                    (float)Math.random()
                )));
            en.setTooltip("<html><b>Shape</b> " + (i++) + ": <i>" + s + "</i>");
        } else if (Math.random()<.25) {
            e2.addEntry(en = new BasicShapeEntry(
                s = new Line2D.Double(200*Math.random(), 300+200*Math.random(), 100+200*Math.random(), 500+400*Math.random()),
                new BasicStrokeRenderer(
                    new Color((float)Math.random(),(float)Math.random(),(float)Math.random()),
                    (float)(12*Math.random())
                )));
            en.setTooltip("<html><b>Line</b> " + (i++) + ": <i>" + s + "</i>");
        } else if (Math.random()<.33) {
            e2.addEntry(en = new ArrowEntry(
                    p1 = new Point2D.Double(100+200*Math.random(), 100*Math.random()), p2 = new Point2D.Double(100+200*Math.random(), 100*Math.random())
                    ));
            en.setTooltip("<html><b>Arrow</b> " + (i++) + ": <i>" + p1 + ", " + p2 + "</i>");
        } else if (Math.random()<.5) {
            e2.addEntry(en = new LabeledPointEntry(
                p1 = new Point2D.Double(400+200*Math.random(),100+200*Math.random()),
                String.format("(%.2f,%.2f)", p1.x, p1.y),
                new BasicPointRenderer()
                    .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .thickness((float)(2*Math.random()))
                    .radius((int)(25*Math.random()))
                ));
            en.setTooltip("<html><b>Labeled Point</b> " + (i++) + ": <i> " + p1 + "</i>");
        } else {
            e2.addEntry(en = new BasicPointEntry(
                p1 = new Point2D.Double(400+200*Math.random(),300+200*Math.random()),
                new BasicPointRenderer()
                    .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .thickness((float)(2*Math.random()))
                    .radius((int)(25*Math.random()))
                ));
            en.setTooltip("<html><b>Point</b> " + (i++) + ": <i> " + p1 + "</i>");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sc = new org.bm.blaise.graphics.GraphicComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        org.jdesktop.layout.GroupLayout scLayout = new org.jdesktop.layout.GroupLayout(sc);
        sc.setLayout(scLayout);
        scLayout.setHorizontalGroup(
            scLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        scLayout.setVerticalGroup(
            scLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sc, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sc, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestGraphics().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.bm.blaise.graphics.GraphicComponent sc;
    // End of variables declaration//GEN-END:variables

}