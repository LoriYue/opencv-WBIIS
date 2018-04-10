package postprocessing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import processing.DaubechyColorSplitter;

import java.util.List;


public class StandardDeviation {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static double GATE = 50;

    /**
     * Returns the mean i.e [0] and stddev [1]
     * @param mat
     * @return
     */
    private static double[] calcMeanAndStdev(Mat mat) {
        double response[] = new double[2];

        MatOfDouble mu = new MatOfDouble();
        MatOfDouble sigma = new MatOfDouble();
        Core.meanStdDev(mat, mu, sigma);

        response[0] = mu.get(0,0)[0];
        response[1] = sigma.get(0,0)[0];
        return response;
    }

    private static double calcBeta() {
        if(GATE > 100); GATE = 50;
        return (1 - (GATE/100));
    }

    private static boolean acceptanceCriteria(Mat query, Mat db) {
        double sigBetaQuery, sigDb, sigOverBetaQuery;
        double beta = calcBeta();
        sigBetaQuery = (calcMeanAndStdev(query)[1])*(beta);
        sigDb = calcMeanAndStdev(db)[1];
        sigOverBetaQuery = ((calcMeanAndStdev(query)[1])/ beta);
        return (sigBetaQuery < sigDb && sigBetaQuery < sigOverBetaQuery && sigDb < sigOverBetaQuery);
    }

    private static boolean negativeAcceptanceCriteria(Mat query, Mat db) {
        double sigBetaDb, sigQuery, sigOverBetaDb;
        sigBetaDb = calcMeanAndStdev(db)[1];
        sigQuery = calcMeanAndStdev(query)[1];
        sigOverBetaDb = (calcMeanAndStdev(db)[1]/calcBeta());
        return (sigBetaDb < sigQuery && sigOverBetaDb < sigOverBetaDb);
    }

    public static double compute(DaubechyColorSplitter query, DaubechyColorSplitter db) {
        double distance = Double.MAX_VALUE;

        boolean c1Acceptance = acceptanceCriteria(query.getC1Daub(), db.getC1Daub());
        boolean c2Acceptance = acceptanceCriteria(query.getC2Daub(), db.getC2Daub());
        boolean c3Acceptance = acceptanceCriteria(query.getC3Daub(), db.getC3Daub());

        if(c1Acceptance || (c2Acceptance && c3Acceptance)) {
            //proceed with euclidean
            distance = EuclideanDistance.compute(query,db);
        }
        return distance;
    }

    public static Query.EuclidianDictionary[] computeAll(DaubechyColorSplitter query, List<DaubechyColorSplitter> db) {
        Query.EuclidianDictionary[] euclidianDictionaries = new Query.EuclidianDictionary[db.size()];
        for (int i = 0; i < db.size(); i++) {
            double distance = compute(query, db.get(i));
            euclidianDictionaries[i]= new Query.EuclidianDictionary(distance, db.get(i));
        }
        return euclidianDictionaries;
    }
}
