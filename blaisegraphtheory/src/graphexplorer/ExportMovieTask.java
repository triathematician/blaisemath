package graphexplorer;

import org.bm.blaise.scio.graph.layout.EnergyLayout;

/**
 * task for managing the exporting of the movie
 * @author skyebend
 *
 */
public class ExportMovieTask {

    // initial number of layout frames
    private static final int _INITIAL_LAYOUT_FRAMES = 15;
    // number of layout frames per slice
    private static final int _INTERP_FRAMES = 5;
    // number of steps per frame
    private static final int _STEPS_PER_FRAME = 4;

    // time to wait while each frame is rendered
    private static final int _FRAME_DELAY = 10;
    
    private GraphExplorerInterface main;
    private LongitudinalGraphPanel lg;
    private MovieExport maker;

    private String status = "preparing to start...";
    private boolean stop = false;
    private int curSl = 0;

    public ExportMovieTask(GraphExplorerInterface main, LongitudinalGraphPanel lg, MovieExport maker) {
        this.main = main;
        this.lg = lg;
        this.maker = maker;
    }

    public void stop() { stop = true; }
    public boolean isDone() { return stop; }

    private void reportStatus() {
        System.out.println("ExportMovieTask: " + status);
    }

    /**
     * Captures a number of steps for currently active layout.
     */
    void captureLayoutSteps(int nFrames) {
        for (int i = 0; i < nFrames; i++) {
            for (int j = 0; j < _STEPS_PER_FRAME; j++)
                main.iterateLayout();
            maker.captureImage();
            try { Thread.sleep(_FRAME_DELAY); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void run() {
        try {
            int endIndex = lg.getNumSlices();
            int numFrames = endIndex * _INTERP_FRAMES;
            maker.setupMovie(((LongitudinalGraphPanel)main.activePanel()).plot, numFrames);
            main.initLayout(new EnergyLayout(main.activeGraph(), main.getActivePoints()));
            curSl = 0;
            lg.setActiveSliceIndex(curSl);
            captureLayoutSteps(_INITIAL_LAYOUT_FRAMES);
            maker.captureImage();
            //movie export loop
            while (curSl < endIndex - 1) {
                curSl++;
                status = "exporting slice " + curSl;
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
    }
}
