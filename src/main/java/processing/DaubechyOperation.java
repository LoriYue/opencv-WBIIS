package processing;

import jwave.Transform;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.Wavelet;
import jwave.transforms.wavelets.daubechies.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import util.OpenCVUtil;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Driver;

public class DaubechyOperation {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    /**
     * Applies a specified daubechy wavelet operation
     * @param resizedMat
     * @param daubechyNumber
     * @return
     */
    public static Mat applyDaubechy(Mat resizedMat, int daubechyNumber) {
        if(daubechyNumber > 18) {
            daubechyNumber = 18;
            System.out.println("Adjusted daubechy number to 18");
        }
        if(daubechyNumber < 2) {
            daubechyNumber = 2;
            System.out.println("Adjusted daubechy number to 2");
        }

        switch (daubechyNumber) {
            case 2:
                return daubechy(resizedMat,new Daubechies2());
            case 3:
                return daubechy(resizedMat,new Daubechies3());
            case 4:
                return daubechy(resizedMat,new Daubechies4());
            case 5:
                return daubechy(resizedMat,new Daubechies5());
            case 6:
                return daubechy(resizedMat,new Daubechies6());
            case 7:
                return daubechy(resizedMat,new Daubechies7());
            case 8:
                return daubechy(resizedMat,new Daubechies8());
            case 9:
                return daubechy(resizedMat,new Daubechies9());
            case 10:
                return daubechy(resizedMat,new Daubechies10());
            case 11:
                return daubechy(resizedMat,new Daubechies11());
            case 12:
                return daubechy(resizedMat,new Daubechies12());
            case 13:
                return daubechy(resizedMat,new Daubechies13());
            case 14:
                return daubechy(resizedMat,new Daubechies14());
            case 15:
                return daubechy(resizedMat,new Daubechies15());
            case 16:
                System.out.println("Performing Daubechy 16...");
                return daubechy(resizedMat,new Daubechies16());
            case 17:
                return daubechy(resizedMat,new Daubechies17());
            case 18:
                return daubechy(resizedMat,new Daubechies18());

            default:
                return daubechy(resizedMat,new Daubechies8());
        }
    }

    /**
     * Performs a Forward FastWaveletTransform
     * @param mat
     * @param wavelet
     * @return
     */
    private static Mat daubechy(Mat mat, Wavelet wavelet) {
        Transform t = new Transform( new FastWaveletTransform(wavelet));
        double[][] forward = t.forward(OpenCVUtil.matToArray(mat));
        return OpenCVUtil.arrayToMat(forward, 128, 128, CvType.CV_8U);
    }


    public static Descriptor storeDescriptor(String filename, Mat mat, String descriptorDB) {
        //appendtofile
        String filename1 = descriptorDB + File.separator + "filename";
        OpenCVUtil.writeToFile(filename1, mat);
        return new Descriptor(filename, mat);
    }
}
