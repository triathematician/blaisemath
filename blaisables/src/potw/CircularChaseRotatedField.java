/*
 * CircularChaseRotatedField.java
 *
 * Created on March 20, 2008, 10:10 AM
 */

package potw;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.BoundedFunction;
import scio.function.FunctionValueException;
import sequor.model.FunctionTreeModel;
import specto.euclidean2.DESolution2D;
import specto.euclidean2.Parametric2D;
import specto.euclidean2.VectorFieldTimed2D;
import specto.euclidean3.DESolution3D;
import specto.euclidean3.ParametricCurve3D;

/**
 *
 * @author  ae3263
 */
public class CircularChaseRotatedField extends javax.swing.JApplet {
    
    /** Initializes the applet CircularChaseRotatedField */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        initComponents();
                       // FunctionTreeRoot vfx = new FunctionTreeRoot(Parser.parseExpression("pi*y+5*pi*(10-x)/sqrt(y^2+(10-x)^2)"));
                       // FunctionTreeRoot vfy = new FunctionTreeRoot(Parser.parseExpression("-pi*x-5*pi*y/sqrt(y^2+(10-x)^2)"));
                       // FunctionTreeRoot vfz = new FunctionTreeRoot(Parser.parseExpression("1"));
                        FunctionTreeModel ftm1 = new FunctionTreeModel("10*cos(pi*t)", "t");
                        FunctionTreeModel ftm2 = new FunctionTreeModel("10*sin(pi*t)", "t");
                        FunctionTreeModel ftm3 = new FunctionTreeModel("t", "t");
                        Parametric2D curve1 = new Parametric2D(ftm1, ftm2);
                        ParametricCurve3D curve2 = new ParametricCurve3D(ftm1, ftm2, ftm3);
                        
                        vectorField2D1.setFunction(new BoundedFunction<R2,R2>() {
                            public R2 getValue(R2 pt) throws FunctionValueException {
                                return new R2(pt.y,-pt.x).times(Math.PI).plus(new R2(10-pt.x,-pt.y).normalized().times(5*Math.PI));
                            }
                            public Vector<R2> getValue(Vector<R2> pts) throws FunctionValueException {
                                Vector<R2> result = new Vector<R2>();
                                for (R2 pt : pts) {
                                    result.add(new R2(pt.y,-pt.x).times(Math.PI).plus(new R2(10-pt.x,-pt.y).normalized().times(5*Math.PI)));
                                }
                                return result;
                            }
                            public R2 minValue(){return new R2(-5.0,-5.0);}
                            public R2 maxValue(){return new R2(5.0,5.0);}
                        });
                        vectorField3D1.setFunction(new BoundedFunction<R3,R3>() {
                            public R3 getValue(R3 pt) throws FunctionValueException {
                                return new R3(10*Math.cos(Math.PI*pt.z)-pt.x,10*Math.sin(Math.PI*pt.z)-pt.y,0).normalized().times(5*Math.PI).plus(0,0,1);
                            }
                            public Vector<R3> getValue(Vector<R3> pts) throws FunctionValueException {
                                Vector<R3> result = new Vector<R3>();
                                for (R3 pt : pts) {
                                    result.add(new R3(10*Math.cos(Math.PI*pt.z)-pt.x,10*Math.sin(Math.PI*pt.z)-pt.y,0).normalized().times(5*Math.PI).plus(0,0,1));
                                }
                                return result;
                            }
                            public R3 minValue(){return new R3(-5.0,-5.0,-5.0);}
                            public R3 maxValue(){return new R3(5.0,5.0,5.0);}
                        });  
                        VectorFieldTimed2D vft = new VectorFieldTimed2D(new BoundedFunction<R3,R2>(){
                            public R2 getValue(R3 pt) throws FunctionValueException {
                                return new R2(10*Math.cos(Math.PI*pt.z)-pt.x,10*Math.sin(Math.PI*pt.z)-pt.y).normalized();
                            }
                            public Vector<R2> getValue(Vector<R3> pts) throws FunctionValueException {
                                Vector<R2> result = new Vector<R2>();
                                for (R3 pt : pts) {
                                    result.add(new R2(10*Math.cos(Math.PI*pt.z)-pt.x,10*Math.sin(Math.PI*pt.z)-pt.y).normalized());
                                }
                                return result;
                            }
                            public R2 minValue(){return new R2(-5.0,-5.0);}
                            public R2 maxValue(){return new R2(5.0,5.0);}
                        });
                        changingVectorField.add(vft);
                        changingVectorField.add(curve1);
                        changingVectorField.getVisometry().setDesiredBounds(-15.0,-15.0,15.0,15.0);

                        staticVectorField.add(vectorField2D1);    
                        staticVectorField.add(new DESolution2D(vectorField2D1));
                        staticVectorField.add(new DESolution2D(vectorField2D1));
                        
                        threeDVectorField.getVisometry().setSceneSize(15.0);
                        threeDVectorField.add(vectorField3D1);
                        threeDVectorField.add(curve2);
                        threeDVectorField.add(new DESolution3D(vectorField3D1,-5,5,0));
                        threeDVectorField.add(new DESolution3D(vectorField3D1,10,-5,0));
                    } catch (Exception ex) {
                        Logger.getLogger(CircularChaseRotatedField.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parametric2D1 = new specto.euclidean2.Parametric2D();
        vectorField2D1 = new specto.euclidean2.VectorField2D();
        vectorField3D1 = new specto.euclidean3.VectorField3D();
        parametricCurve3D1 = new specto.euclidean3.ParametricCurve3D();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        changingVectorField = new specto.euclidean2.Plot2D();
        threeDVectorField = new specto.euclidean3.Plot3D();
        staticVectorField = new specto.euclidean2.Plot2D();

        changingVectorField.setAxisStyle(1);

        javax.swing.GroupLayout changingVectorFieldLayout = new javax.swing.GroupLayout(changingVectorField);
        changingVectorField.setLayout(changingVectorFieldLayout);
        changingVectorFieldLayout.setHorizontalGroup(
            changingVectorFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        changingVectorFieldLayout.setVerticalGroup(
            changingVectorFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Original 2D", changingVectorField);

        threeDVectorField.setZLabel("t");
        threeDVectorField.setAxisStyle(3);

        javax.swing.GroupLayout threeDVectorFieldLayout = new javax.swing.GroupLayout(threeDVectorField);
        threeDVectorField.setLayout(threeDVectorFieldLayout);
        threeDVectorFieldLayout.setHorizontalGroup(
            threeDVectorFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        threeDVectorFieldLayout.setVerticalGroup(
            threeDVectorFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("3D", threeDVectorField);

        staticVectorField.setAxisStyle(1);

        javax.swing.GroupLayout staticVectorFieldLayout = new javax.swing.GroupLayout(staticVectorField);
        staticVectorField.setLayout(staticVectorFieldLayout);
        staticVectorFieldLayout.setHorizontalGroup(
            staticVectorFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
        );
        staticVectorFieldLayout.setVerticalGroup(
            staticVectorFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Static 2D", staticVectorField);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private specto.euclidean2.Plot2D changingVectorField;
    private javax.swing.JTabbedPane jTabbedPane1;
    private specto.euclidean2.Parametric2D parametric2D1;
    private specto.euclidean3.ParametricCurve3D parametricCurve3D1;
    private specto.euclidean2.Plot2D staticVectorField;
    private specto.euclidean3.Plot3D threeDVectorField;
    private specto.euclidean2.VectorField2D vectorField2D1;
    private specto.euclidean3.VectorField3D vectorField3D1;
    // End of variables declaration//GEN-END:variables
    
}
