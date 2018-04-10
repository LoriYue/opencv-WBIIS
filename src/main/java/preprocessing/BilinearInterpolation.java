package preprocessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BilinearInterpolation {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static Mat apply(Mat src) {
        Mat resized = new Mat(128,128, CvType.CV_8U);
        Imgproc.resize(src, resized,new Size(128,128),0,0,Imgproc.INTER_CUBIC);
        return resized;
    }
}
