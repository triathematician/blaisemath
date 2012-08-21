/*
 * Plot2D.java
 * Created on Sep 14, 2007, 8:12:44 AM
 */

package specto.euclidean2;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.TransferHandler;
import specto.PlotPanel;

/**
 * The primary 2D plot window which should be used in applications. Will have support
 * for drawing grid, axes, and labels, plus animation and event handling.
 * @author Elisha Peterson
 */
public class Plot2D extends PlotPanel<Euclidean2> {
    
    // NATIVE OBJECTS
    
    /** Axes object */
    Axes2D axes;
    /** Grid object */
    StandardGrid2D grid;
    
    /** Customized transferrer */
    class MyTransferHandler extends TransferHandler {
        public MyTransferHandler(){}

        @Override
        public boolean canImport(TransferSupport support) {    // for demo purposes, we'll only support drops and not clipboard paste
            return support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {    // if we can't handle the import, return so
            if (!canImport(support)) { return false; }
            // fetch the data, and bail if it fails cos(x^2)
            String data;
            try {
                data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }     
            // load as function
            Function2D result = new Function2D(data);
            add(result);
            repaint();
            
            return true;
        }        
    }
    
    // CONSTRUCTOR
    
    /** Default constructor */
    public Plot2D(){
        super(new Euclidean2());
        grid=new StandardGrid2D();
        add(grid);
        axes=new Axes2D();
        add(axes);
        initDropping();
    }
    
    public void initDropping(){
        setTransferHandler(new MyTransferHandler());
    }
    
    // BEAN PATTERNS

    public String getXLabel() { return axes.getXLabel(); }
    public void setXLabel(String xLabel) { axes.setXLabel(xLabel); }
    public String getYLabel() { return axes.getYLabel(); }
    public void setYLabel(String yLabel) { axes.setYLabel(yLabel); }

    /** Returns axis style. */
    public int getAxisStyle(){
        return axes.style.getValue();
    }
    
    /** Sets axis style. */
    public void setAxisStyle(int newValue){
        axes.style.setValue(newValue);
    }
    
    /** Adjust axis visibility. */
    public void setAxisVisible(boolean newValue){axes.setVisible(false);}
    /** Gets axis visibility. */
    public boolean isAxisVisible(){return axes.isVisible();}
    
    /** Adjust grid visibility. */
    public void setGridVisible(boolean newValue){grid.setVisible(false);}
    /** Gets axis visibility. */
    public boolean isGridVisible(){return grid.isVisible();}

    /** Adjusts aspect ratio. */
    public void setAspectRatio(double newValue){ visometry.setAspectRatio(newValue); }
    /** Returns aspect ratio. */
    public double getAspectRatio(){ return visometry.getAspectRatio(); }

    
    // OVERRIDES
    
    @Override
    public void clearPlottables() {
        super.clearPlottables();
        add(axes);
        add(grid);
    } 
}
