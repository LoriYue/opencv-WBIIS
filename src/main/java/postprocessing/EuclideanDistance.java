package postprocessing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import processing.DaubechyColorSplitter;

public class EuclideanDistance  {
    public static final int QUADRANT_SIZE=8;
    /**
     * Calculates the euclidian distance
     * @param query
     * @param db
     * @return
     */
    public static double compute(DaubechyColorSplitter query, DaubechyColorSplitter db) {
        Mat[] queryArr = query.toArray();
        Mat[] dbArr = db.toArray();
        double w11 = 0,w12 = 0 ,w21 = 0,w22 = 0;
        for (int i = 0; i < queryArr.length; i++) {
            Mat queryQuadrant = computeQuadrant(queryArr[i],1);
            Mat dbQuadrant = computeQuadrant(dbArr[i], 1);
            w11 = w11 + computexx(queryQuadrant, dbQuadrant);
        }
        for (int i = 0; i < queryArr.length; i++) {
            Mat queryQuadrant = computeQuadrant(queryArr[i],2);
            Mat dbQuadrant = computeQuadrant(dbArr[i], 2);
            w12 = w12 + computexx(queryQuadrant, dbQuadrant);
        }
        for (int i = 0; i < queryArr.length; i++) {
            Mat queryQuadrant = computeQuadrant(queryArr[i],3);
            Mat dbQuadrant = computeQuadrant(dbArr[i], 3);
            w21 = w21 + computexx(queryQuadrant, dbQuadrant);
        }
        for (int i = 0; i < queryArr.length; i++) {
            Mat queryQuadrant = computeQuadrant(queryArr[i],4);
            Mat dbQuadrant = computeQuadrant(dbArr[i], 4);
            w22 = w22 + computexx(queryQuadrant, dbQuadrant);
        }
        return w11 + w12 + w21 + w22;
    }

    /**
     * Computes the w1 part of the already extracted Daubechy 8,8 upper-left cell
     * @param query
     * @param db
     * @return
     */
    private static double computexx(Mat query, Mat db) {
        Mat response = new Mat();
        Core.absdiff(query, db, response);
        Scalar scalar = Core.sumElems(response);
        return scalar.val[0];
    }

    public static Mat computeQuadrant(Mat mat, int quadrant) {
        switch (quadrant) {
            case 1:
                Rect roi1 = new Rect(0, 0, QUADRANT_SIZE, QUADRANT_SIZE);
                return new Mat(mat, roi1);
            case 2:
                Rect roi2 = new Rect(QUADRANT_SIZE, 0, QUADRANT_SIZE, QUADRANT_SIZE);
                return new Mat(mat, roi2);
            case 3:
                Rect roi3 = new Rect(0, QUADRANT_SIZE, QUADRANT_SIZE, QUADRANT_SIZE);
                return new Mat(mat, roi3);
            case 4:
                Rect roi4 = new Rect(QUADRANT_SIZE, QUADRANT_SIZE, QUADRANT_SIZE, QUADRANT_SIZE);
                return new Mat(mat, roi4);
            default:
                Rect roi11 = new Rect(0, 0, QUADRANT_SIZE, QUADRANT_SIZE);
                return new Mat(mat, roi11);
        }
//        Rect rect = new Rect(x, y, width, height);
    }
}
