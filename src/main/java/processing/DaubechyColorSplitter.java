package processing;

import org.opencv.core.Mat;

public class DaubechyColorSplitter {
    String fileName;
    Mat c1Daub;
    Mat c2Daub;
    Mat c3Daub;

    public DaubechyColorSplitter(Mat c1Daub, Mat c2Daub, Mat c3Daub) {
        this.c1Daub = c1Daub;
        this.c2Daub = c2Daub;
        this.c3Daub = c3Daub;
    }

    public DaubechyColorSplitter(String fileName, Mat c1Daub, Mat c2Daub, Mat c3Daub) {
        this.fileName = fileName;
        this.c1Daub = c1Daub;
        this.c2Daub = c2Daub;
        this.c3Daub = c3Daub;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Mat[] toArray() {
        return new Mat[]{this.c1Daub, this.c2Daub, this.c3Daub};
    }

    public Mat getC2Daub() {
        return c2Daub;
    }

    public void setC2Daub(Mat c2Daub) {
        this.c2Daub = c2Daub;
    }

    public Mat getC3Daub() {
        return c3Daub;
    }

    public void setC3Daub(Mat c3Daub) {
        this.c3Daub = c3Daub;
    }

    public Mat getC1Daub() {
        return c1Daub;
    }

    public void setC1Daub(Mat c1Daub) {
        this.c1Daub = c1Daub;
    }
}
