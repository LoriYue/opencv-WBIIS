package preprocessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ColorSplitter {
    private Mat c1;
    private Mat c2;
    private Mat c3;


    public ColorSplitter(Mat m, int size) {
        splitAndSet(m, size);
    }

    public ColorSplitter() {
    }

    /**
     * Returns an Array of size 3, denoting c1,c2,c3 respectively.
     * @param m
     * @return
     */
    public static Mat[] split(Mat m, int size) {
        Mat cArr[] = new Mat[3];
        Mat c1 = new Mat(), c2 = new Mat(), c3 = new Mat();
        Mat c1TempDest = new Mat(),c1TempDest2 = new Mat(), c2TempDest = new Mat();
        Mat max=new Mat(size, size, CvType.CV_8UC1, new Scalar(255));
        List<Mat> lRgb = new ArrayList<Mat>(3);

        Core.split(m, lRgb);
        Mat R = lRgb.get(0);
        Mat G = lRgb.get(1);
        Mat B = lRgb.get(2);

        //C1
        Core.add(R,G,c1TempDest);
        Core.add(c1TempDest, B, c1TempDest2);
        Core.divide(c1TempDest2,new Scalar(3),c1);
        cArr[0] = c1;

        //C2
        Mat maxMinusB = new Mat(), maxMinusBPlusR= new Mat();
        Core.subtract(max,B,maxMinusB);
        Core.add(maxMinusB,R,maxMinusBPlusR);
        Core.divide(maxMinusBPlusR,new Scalar(2),c2);
        cArr[1] = c2;

        //C3
        Mat maxMinusG = new Mat() , maxMinusGTimes2 = new Mat(), maxMinusGTimes2PlusR = new Mat(), maxMinusGTimes2PlusRPlusB = new Mat();
        Core.subtract(max,G, maxMinusG);
        Core.multiply(maxMinusG, new Scalar(2), maxMinusGTimes2);
        Core.add(maxMinusGTimes2, R, maxMinusGTimes2PlusR);
        Core.add(maxMinusGTimes2PlusR, B, maxMinusGTimes2PlusRPlusB);
        Core.divide(maxMinusGTimes2PlusRPlusB, new Scalar(4),  c3);
        cArr[2] = c3;
        return cArr;
    }

    public Mat[] splitAndSet(Mat m, int size) {
        Mat cArr[] = new Mat[3];
        Mat c1 = new Mat(), c2 = new Mat(), c3 = new Mat();
        Mat c1TempDest = new Mat(),c1TempDest2 = new Mat(), c2TempDest = new Mat();
        Mat max=new Mat(size, size, CvType.CV_8UC1, new Scalar(255));
        List<Mat> lRgb = new ArrayList<Mat>(3);

        Core.split(m, lRgb);
        Mat R = lRgb.get(0);
        Mat G = lRgb.get(1);
        Mat B = lRgb.get(2);

        //C1
        Core.add(R,G,c1TempDest);
        Core.add(c1TempDest, B, c1TempDest2);
        Core.divide(c1TempDest2,new Scalar(3),c1);
        cArr[0] = c1;
        this.c1 = c1;

        //C2
        Mat maxMinusB = new Mat(), maxMinusBPlusR= new Mat();
        Core.subtract(max,B,maxMinusB);
        Core.add(maxMinusB,R,maxMinusBPlusR);
        Core.divide(maxMinusBPlusR,new Scalar(2),c2);
        cArr[1] = c2;
        this.c2 = c2;

        //C3
        Mat maxMinusG = new Mat() , maxMinusGTimes2 = new Mat(), maxMinusGTimes2PlusR = new Mat(), maxMinusGTimes2PlusRPlusB = new Mat();
        Core.subtract(max,G, maxMinusG);
        Core.multiply(maxMinusG, new Scalar(2), maxMinusGTimes2);
        Core.add(maxMinusGTimes2, R, maxMinusGTimes2PlusR);
        Core.add(maxMinusGTimes2PlusR, B, maxMinusGTimes2PlusRPlusB);
        Core.divide(maxMinusGTimes2PlusRPlusB, new Scalar(4),  c3);
        cArr[2] = c3;
        this.c3 = c3;

        return cArr;
    }

    public Mat getC1() {
        return c1;
    }

    public Mat getC2() {
        return c2;
    }

    public Mat getC3() {
        return c3;
    }

}
