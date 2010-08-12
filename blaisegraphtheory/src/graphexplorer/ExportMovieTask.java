package graphexplorer;



import javax.swing.ProgressMonitor;
import org.bm.blaise.scio.graph.layout.SpringLayout;

/**
 * task for managing the exporting of the movie
 * @author skyebend
 *
 */
public class ExportMovieTask implements Runnable {

    // initial number of layout frames
    private static final int _INITIAL_LAYOUT_FRAMES = 15;
    // number of layout frames per slice
    private static final int _INTERP_FRAMES = 5;
    // number of steps per frame
    private static final int _STEPS_PER_FRAME = 4;

    // time to wait while each frame is rendered
    private static final int _FRAME_DELAY = 10;

    private MovieExport maker;
    private LongitudinalGraphPanel lg;
    transient private GraphController gc;    
    
    private ProgressMonitor pm;

    private String status = "preparing to start...";
    private boolean stop = false;
    private int curSl = 0;
    private int maxSl = 0;

    public ExportMovieTask(LongitudinalGraphPanel lg, MovieExport maker, ProgressMonitor pm) {
        this.lg = lg;
        this.gc = lg.gc;
        this.maker = maker;
        this.pm = pm;
    }

    public void stop() { stop = true; }
    public boolean isDone() { return stop; }
    public int getTaskLength() { return maxSl; }

    private void reportStatus() {
        if (pm == null)
            gc.reportStatus("ExportMovieTask: " + status);
        else {
            pm.setProgress(curSl);
            pm.setNote(status);
        }
    }

    /**
     * Captures a number of steps for currently active layout.
     */
    void captureLayoutSteps(int nFrames) {
        for (int i = 0; i < nFrames; i++) {
            for (int j = 0; j < _STEPS_PER_FRAME; j++)
                gc.stepLayout();
            maker.captureImage();
            try { Thread.sleep(_FRAME_DELAY); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void run() {
        lg.hidePlot();
        try {
            int endIndex = lg.getNumSlices();
            int numFrames = endIndex * _INTERP_FRAMES;
            maker.setupMovie(lg.plot, numFrames);
            gc.setIterativeLayout(new SpringLayout(gc.getPositions()));
            curSl = 0;
            lg.setActiveSliceIndex(curSl);
            captureLayoutSteps(_INITIAL_LAYOUT_FRAMES);
            maker.captureImage();
            //movie export loop
            maxSl = endIndex - 1;
            if (pm != null)
                pm.setMaximum(maxSl);
            while (curSl < maxSl) {
                curSl++;
                status = "Exporting slice " + curSl;
                reportStatus();
                lg.setActiveSliceIndex(curSl);
                captureLayoutSteps(_INTERP_FRAMES);
                if (stop) break;
            }
            maker.finishMovie();
            maker = null;
            stop = true;
            status = "Movie export finished";
            reportStatus();
        } catch (Exception e) {
            status = "Movie export error: " + e;
            e.printStackTrace();
            stop = true;
            reportStatus();
            maker.finishMovie();
            maker = null;
        }
        lg.showPlot();
    }
}
