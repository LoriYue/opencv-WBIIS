package processing;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Daubechy4Output {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private Mat src;
    private Mat tl;
    private Mat tr;
    private Mat bl;
    private Mat br;

    public Daubechy4Output(Mat src) {
        this.src = src;
        //Mat daubechies4 = DaubechyOperation.applyDaubechies4(src);
    }

    public Mat concatDaubechy() {
        Mat tltr = new Mat(), tltrbl = new Mat(), tltrblbr= new Mat();
        Core.add(tl, tr, tltr);
        Core.add(tltr, bl, tltrbl);
        Core.add(tltrbl, br, tltrblbr);
        return tltrblbr;
    }
}
