package postprocessing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import processing.DaubechyColorSplitter;
import processing.Descriptor;

import java.util.*;
import java.util.concurrent.Callable;

public class Query {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static double getEuclidian(Mat query, Mat db) {
        return Core.norm(query, db);
    }

    /**
     * Takes in a query Mat computes its descriptor, takes in a list of daubechyColorSplitters that have been loaded into memory. Performs a euclidian operator on them.
     * @param query
     * @param daubechyColorSplitters
     * @return
     */
    public static EuclidianDictionary[] computeDistance(DaubechyColorSplitter query, List<DaubechyColorSplitter> daubechyColorSplitters) {
            //compute descriptor of query
        EuclidianDictionary euclidianDictionary[] = StandardDeviation.computeAll(query, daubechyColorSplitters);

        //perform euclidian on query and each descriptor element
            Arrays.sort(euclidianDictionary, (o1, o2) -> {
                if(o1.euclidian < o2.euclidian) {
                    return -1;
                }else if(o2.euclidian > o1.euclidian) {
                    return 1;
                }else{
                    return 0;
                }
            });
            return euclidianDictionary;
    }


    /**
     * TODO: Potential Memory Leak Area
     */
    public static class EuclidianDictionary {
        private Double euclidian;
        private Descriptor descriptor;
        private DaubechyColorSplitter daubechyColorSplitter;

        public EuclidianDictionary(Double euclidian, DaubechyColorSplitter daubechyColorSplitter) {
            this.euclidian = euclidian;
            this.daubechyColorSplitter = daubechyColorSplitter;
        }

        public EuclidianDictionary(Double euclidian, Descriptor descriptor) {
            this.euclidian = euclidian;
            this.descriptor = descriptor;
        }

        public DaubechyColorSplitter getDaubechyColorSplitter() {
            return daubechyColorSplitter;
        }

        public void setDaubechyColorSplitter(DaubechyColorSplitter daubechyColorSplitter) {
            this.daubechyColorSplitter = daubechyColorSplitter;
        }

        public Double getEuclidian() {
            return euclidian;
        }

        public void setEuclidian(Double euclidian) {
            this.euclidian = euclidian;
        }

        public Descriptor getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(Descriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public boolean equals(Object obj) {
            return ((EuclidianDictionary) obj).euclidian < this.euclidian;
        }
    }
}
