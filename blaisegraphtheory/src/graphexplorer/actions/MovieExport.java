package graphexplorer.actions;

/* This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;


import quicktime.QTException;
import quicktime.QTSession;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.QDConstants;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.image.CSequence;
import quicktime.std.image.CodecComponent;
import quicktime.std.image.CompressedFrameInfo;
import quicktime.std.image.ImageDescription;
import quicktime.std.image.QTImage;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.VideoMedia;
import quicktime.util.QTHandle;
import quicktime.util.RawEncodedImage;
import visometry.PlotComponent;

/**
 * Exports animation as a QuickTime movie, using the QTJava library. Much of the
 * code on this page is based on an example from a bug report submitted to
 * http://lists.apple.com/archives/quicktime-java/2006/Apr/msg00074.html Also I
 * also worked from an example by Wayne Rasband from the imageJ plugin website
 * at http://rsb.info.nih.gov/ij/plugins/download/QuickTime_Writer.java
 *
 * @author skyebend
 * @author Elisha Peterson
 */
public class MovieExport {

    /**
     *
     */
    private static final long serialVersionUID = -3443808061066683501L;
    /**
     * key for value giving path (including file name) where movie should be saved.
     */
    public static final String OUTPUT_PATH = "OUTPUT_PATH";
    /**
     * key for the file type to be used for movie export
     */
    public static final String FILE_TYPE = "FILE_TYPE";
    /**
     * key for string containing names of additional format specific paramters
     */
    public static final String FORMAT_PARAMS = "FORMAT_PARAMS";
    /**
     * file type value indicating a QuickTime movie.
     * Note: only works on macs and pcs with quicktime installed
     */
    public static final String QUICKTIME_FILE_TYPE = "mov";
    /**
     * file type value indicating a QuickTime movie.  Note: not useable on linux
     */
    public static final String FLASH_FILE_TYPE = "swf";
    private File file;
    public static final String SUFFIX = "mov";
    private QDRect bounds;
    private QDGraphics graphics;
    private QTFile movFile;
    private QTHandle imageHandle; // pointer to block of memory? (c wrapper)
    private RawEncodedImage compressedImage;
    private Movie movie;
    private VideoMedia videoMedia;
    private Track videoTrack;
    private CSequence seq;
    private ImageDescription imgDesc;
    public static final int KEY_FRAME_RATE = 15; //30;
    public static final int TIME_SCALE = 600;
    public static final String CODEC_PARAM_NAME = "CODEC";
    public static final String[] codecs = {"Cinepak", "Animation", "H.263",
        "Sorenson", "Sorenson 3", "MPEG-4"};
    public static final int[] codecTypes = {StdQTConstants.kCinepakCodecType,
        StdQTConstants.kAnimationCodecType, StdQTConstants.kH263CodecType,
        StdQTConstants.kSorensonCodecType, 0x53565133, 0x6d703476};
    private String codec = "Animation"; // string representing current state
    public static final String[] qualityStrings = {"Low", "Normal", "High",
        "Maximum"};
    public static final int[] qualityConstants = {
        StdQTConstants.codecLowQuality, StdQTConstants.codecNormalQuality,
        StdQTConstants.codecHighQuality, StdQTConstants.codecMaxQuality};
    private String quality = "Low"; // string for current quality
    private int rate = 30; // TODO: read qt fps from engine
    private BufferedImage bufferedImage;
    private Graphics2D graphics2D;
    private PlotComponent canvas;
    private boolean isExporting = false;
    // private int[] pixels;
    private int currentFrame = 1;
    private int codecQuality = StdQTConstants.codecLowQuality;
    private int codecType = StdQTConstants.kAnimationCodecType;

    public MovieExport(File file) {
        this.file = file;
    }

    public void setupMovie(PlotComponent canvas, int frames) throws Exception {
        this.canvas = canvas;
        isExporting = true;
        QTSession.open();
        // Set up a Quicktime graphics world
        bounds = new QDRect(canvas.getWidth(), canvas.getHeight());
        // Workaround suggested from Simon Goldrei to deal with endian order
        // bug on intel macs
        if (quicktime.util.EndianOrder.isNativeLittleEndian()) {
            graphics = new QDGraphics(QDConstants.k32BGRAPixelFormat, bounds);
        } else {
            graphics = new QDGraphics(QDGraphics.kDefaultPixelFormat, bounds);
        }
        // Get the raw image from the QT graphics world
        RawEncodedImage image = graphics.getPixMap().getPixelData();
        int scanLength = image.getRowBytes() >>> 2;
        int scanHeight = image.getSize() / image.getRowBytes();

        // Set up a Java graphics context based upaon an array as the storage
        // for a buffered image

        bufferedImage = new BufferedImage(scanLength, scanHeight,
                BufferedImage.TYPE_INT_RGB);
        // create a java graphcs based on the QT image
        graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(canvas.getBackground());
        // TODO - make this optional ??
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // setup QT file io
        movFile = new QTFile(file);
        try {
            movie = Movie.createMovieFile(movFile, StdQTConstants.kMoviePlayer,
                    StdQTConstants.createMovieFileDeleteCurFile
                    | StdQTConstants.createMovieFileDontCreateResFile);
        } catch (StdQTException e) {
            String error = e.errorCodeToString();
            // trap the file busy error
            if (e.errorCode() == -47) {
                error = "Output file is busy, is it open in another application?";
            }
            throw new Exception(error, e);
        }
        int timeScale = TIME_SCALE; // 100 units per second
        videoTrack = movie.addTrack(canvas.getWidth(), canvas.getHeight(), 0);
        videoMedia = new VideoMedia(videoTrack, timeScale);
        videoMedia.beginEdits();
        // ImageDescription imgDesc2 = new ImageDescription(
        // QDConstants.k32ARGBPixelFormat);
        // imgDesc2.setWidth(canvas.getWidth());
        // imgDesc2.setHeight(canvas.getHeight());
        // QDGraphics gw = new QDGraphics(imgDesc2, 0);
        // figure out how much memory for each frame
        int rawImageSize = QTImage.getMaxCompressionSize(graphics, bounds,
                graphics.getPixMap().getPixelSize(), codecQuality, codecType,
                CodecComponent.anyCodec);
        // allocate the memory and lock it
        imageHandle = new QTHandle(rawImageSize, true);
        imageHandle.lock();
        // create a pointer o it
        compressedImage = RawEncodedImage.fromQTHandle(imageHandle);
        // create a "compressed image sequence" around the graphics
        seq = new CSequence(graphics, bounds, graphics.getPixMap().getPixelSize(),
                codecType, CodecComponent.bestCompressionCodec, //.bestFidelityCodec,
                codecQuality, codecQuality, KEY_FRAME_RATE, null, 0);
        imgDesc = seq.getDescription();

    }

    public void captureImage() {
        graphics2D.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        // render the graphics to the QTGraphics
        canvas.renderTo(graphics2D);
        try {
            RawEncodedImage pixelData = graphics.getPixMap().getPixelData();
            int intsPerRow = pixelData.getRowBytes() / 4;
            // need to copy the pixel data from the java to QuickDraw graphics
            // OR could implement as quickdraw graphics renderer..

            PixelGrabber grabber = new PixelGrabber(bufferedImage, 0, 0,
                    bufferedImage.getWidth(), bufferedImage.getHeight(), false);
            try {
                grabber.grabPixels();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
            int[] javaPixels = (int[]) grabber.getPixels();

            pixelData.copyFromArray(0, javaPixels, 0, intsPerRow
                    * bufferedImage.getHeight());

            CompressedFrameInfo cfInfo = seq.compressFrame(graphics, bounds,
                    StdQTConstants.codecFlagUpdatePrevious, compressedImage);
            // decide if we need a keyframe,
            // see developer.apple.com/qa/qtmcc/qtmcc20.html
            boolean syncSample = cfInfo.getSimilarity() == 0;
            videoMedia.addSample(imageHandle, 0, cfInfo.getDataSize(), rate,
                    imgDesc, 1, syncSample ? 0
                    : StdQTConstants.mediaSampleNotSync);
        } catch (StdQTException e) {
            System.out.println("MovieExport error: " + e.getMessage());
        }

        // debug
        //System.out.println("\t saved QT frame " + currentFrame);
        currentFrame++;

    }

    public void finishMovie() {
        try {
            videoMedia.endEdits();
            videoTrack.insertMedia(0, 0, videoMedia.getDuration(), 1);
            OpenMovieFile omf = OpenMovieFile.asWrite(movFile);
            movie.addResource(omf, StdQTConstants.movieInDataForkResID, movFile.getName());
            //MovExportSettingsDialog set = new MovExportSettingsDialog(null,this);
            //set.showDialog();

        } catch (StdQTException e) {
            System.out.println("MovieExport error: " + e.getMessage());
        } catch (QTException e) {
            System.out.println("MovieExport error: " + e.getMessage());
        }
        QTSession.close();
        isExporting = false;
        // debug
        System.out.println("MovieExport log: "
                + "QTMovie export finished to file " + file
                + "\n  Codec:" + codec + " Quality:" + quality);
        System.out.println("MovieExport status: "
                + "Finished exporting QT movie");
    }

    public boolean isExporting() {
        return isExporting;
    }

    public String getCodec() {
        return codec;
    }

    /**
     * tries to match the codec name to one of the defined codecs. defaults to
     * animtion if no match
     *
     * @author skyebend
     * @param codec
     */
    public void setCodec(String codec) {
        this.codec = "Animation";
        codecType = StdQTConstants.kAnimationCodecType;
        for (int i = 0; i < codecs.length; i++) {
            if (codec.equals(codecs[i])) {
                codecType = codecTypes[i];
                this.codec = codecs[i];
            }
        }
    }

    public String getCodecQuality() {
        return quality;
    }

    public void setCodecQuality(String codecQuality) {
        this.codecQuality = StdQTConstants.codecHighQuality;
        quality = "High";
        for (int i = 0; i < qualityStrings.length; i++) {
            if (quality.equals(qualityStrings[i])) {
                this.codecQuality = qualityConstants[i];
            }
            quality = qualityStrings[i];
        }
    }

    public int getCodecType() {
        return codecType;
    }

    public void setCodecType(int codecType) {
        this.codecType = codecType;
        codec = "unknown:" + codecType;
    }

    public void configure() {
        //check that it is the right kind of settings
        if (FILE_TYPE.equals(SUFFIX)) {
            setCodec(CODEC_PARAM_NAME);
        } else {
            System.out.println("Settings are not appropriate for QuickTime movie maker");
        }
    }
    /**
     * This could be used to get codecs to export MP4, AVI, or DV ...
     * returns a list of installed codecs that could be used to export this
     * movie From Chris Adamson's example at
     * http://www.onjava.com/pub/a/onjava/2003/02/19/qt_file_format.html?page=2
     *
     * @author skyebend
     * @return vector of ComponentIdentifiers
     */
//	public Vector getExportFormats() {
//		// build up a list of exporters and let user choose one
//		Vector compIdentifiers = new Vector();
//		ComponentIdentifier ci = null;
//		ComponentDescription cd = new ComponentDescription(
//				StdQTConstants.movieExportType);
//
//		try {
//			while ((ci = ComponentIdentifier.find(ci, cd)) != null) {
//				// check to see that the movie can be exported
//				// with this component (this throws some obnoxious
//				// exceptions, maybe a bit expensive?)
//				try {
//					MovieExporter exporter = new MovieExporter(ci);
//					if (exporter.validate(movie, null))
//						compIdentifiers.addElement(ci);
//					System.out.println("comp:"+ci);
//					System.out.println("\t"+ci.getInfo().getInformationString());
//				} catch (StdQTException expE) {
//				} // ow!
//			}
//		} catch (QTException e) {
//
//			e.printStackTrace();
//		}
//		return compIdentifiers;
//	}
}
