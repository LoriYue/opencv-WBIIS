package util;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;

public class OpenCVUtil {
    public static final String DESCRIPTOR_PATH = "/home/martinomburajr/dev/martinomburajr/masters/icv/debauchies-wavelets/src/main/java/images";
    public static Mat arrayToMat(double[][] array, int height, int width, int matType)
    {
        Mat image = new Mat(height,width,matType);
        for (int i=0; i<height; i++)
        {
            for (int j=0; j<width; j++)
            {
                image.put(i,j,array[i][j]);
            }
        }
        return image;
    }

    public static double[][] matToArray(Mat frame)
    {
        double array[][] = new double[frame.height()][frame.width()];
        for (int i=0; i < frame.height(); i++)
        {
            for (int j=0; j < frame.width(); j++)
            {
                array[i][j] = frame.get(i,j)[0];
            }
        }
        return array;
    }

    public static File[] getFiles(String filepath) {
        File folder = new File(filepath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return listOfFiles;
    }

    public static String[] getFileNames(String filepath) {
        File folder = new File(filepath);
        File[] listOfFiles = folder.listFiles();
        String[] listOfFilenames = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                listOfFilenames[i] = listOfFiles[i].getName();
            }
        }
        return listOfFilenames;
    }

    public static void writeToFile(String filename, Mat mat) {
        Imgcodecs.imwrite(filename, mat);
    }

    public static void show(Mat mat, String windowName) {
        BufferedImage bufferedImage = Mat2BufferedImage(mat);
        displayImage(bufferedImage, windowName);
    }



    public static BufferedImage Mat2BufferedImage(Mat m) {
        // Fastest code
        // output can be assigned either to a BufferedImage or to an Image

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public static void displayImage(Image img2, String windowName) {
        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        ImageIcon icon=new ImageIcon(img2);
        JFrame frame=new JFrame(windowName);
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void appendFolderNameOnFile(String path) {
        File[] files = new File(path).listFiles();
        String parent = files[0].getParentFile().getName();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isFile()) {
                File file = new File( path + File.separator + parent+"_"+files[i].getName());
                files[i].renameTo( file);
            }
        }
    }

    public static void main(String[] args) {
        String path = "/home/martinomburajr/Pictures/test-images";
        //appendFolderNameOnFileLayer1(path);
    }

    private static void combineRandomFiles() {

    }

    private static void appendFolderNameOnFileLayer1(String path) {
        File[] files = new File(path).listFiles();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory()) {
                appendFolderNameOnFile(path + File.separator +  files[i].getName());
            }
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

