package processing;

import org.opencv.core.Mat;

/**
 * Holds information for a filename and its specific descriptor.
 */
public class Descriptor {
    private String filename;
    private Mat mat;

    public Descriptor(String filename, Mat mat) {
        this.filename = filename;
        this.mat = mat;
    }

    public Descriptor() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }
}
