import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import postprocessing.EuclideanDistance;
import postprocessing.Query;
import postprocessing.StandardDeviation;
import preprocessing.ColorSplitter;
import processing.DaubechyColorSplitter;
import processing.DaubechyOperation;
import processing.Descriptor;
import util.OpenCVUtil;

import java.io.*;
import java.util.*;

import static processing.DaubechyOperation.applyDaubechy;

public class DriverClass {
    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static final String DESCRIPTOR_DB="/home/martinomburajr/dev/martinomburajr/masters/icv/debauchies-wavelets/src/main/java/descriptor_db/";
    public static final String IMAGE_PATH = "/home/martinomburajr/Pictures/test-images/test/";
//    public static final String IMAGE_PATH = "/home/martinomburajr/dev/martinomburajr/masters/icv/debauchies-wavelets/src/main/java/images/";

    public static final int IMAGE_SIZE=256;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Daubechies Wavelet Transform (DWT) using Java OpenCV " + Core.VERSION);

        boolean initFlag = true;
        int initialChoice = -1;
        while(initFlag) {
            System.out.println("Would you like to \n" +
                    "[0] - Process An Image \n" +
//                    "[1] - Load the Descriptors Into Memory & Query\n" +
                    "[1] - Query the InMem Descriptor Database");

            initialChoice = scanner.nextInt();
            if(initialChoice > 2 || initialChoice < 0) {
                System.out.println("Please enter the correct number");
                OpenCVUtil.clearScreen();
            }else{
                initFlag = false;
            }
        }

        switch (initialChoice) {
            case 0:
                processImage(scanner);
                break;
//            case 1:
//                break;
            case 1:
                int daubechyNumber = requestDaubechyNumber(scanner);
                List<DaubechyColorSplitter> inMemoryDescriptorDatabase = createInMemoryDescriptorDatabase(new File(IMAGE_PATH), daubechyNumber);


                queryImage(scanner,inMemoryDescriptorDatabase, daubechyNumber);
            default:
                break;
        }
    }
        
    
    private static void processImage(Scanner scanner) {
        System.out.println("Select an image to processAndShow below");
        String[] fileNames = OpenCVUtil.getFileNames(IMAGE_PATH);

        for (int i = 0; i < fileNames.length; i++) {
            System.out.println("[" + i + "] | " + fileNames[i]);
        }
        int choice = scanner.nextInt();

        if(choice > fileNames.length || choice < 0) {
            System.out.println("Please select a valid number");
        }else {
            int daubechyNumber = requestDaubechyNumber(scanner);
            System.out.println("Applying Daubechy" + daubechyNumber +" on the split color channels");
            process(fileNames[choice],  daubechyNumber, true);
        }
    }

    
    private static void queryImage(Scanner scanner, List<DaubechyColorSplitter> descriptors, int daubechyNumber) {
        boolean flag = true;
        while(flag) {
            flag = queryAndFindDistance(scanner, descriptors, daubechyNumber);
        }
    }

    private static boolean queryAndFindDistance(Scanner scanner, List<DaubechyColorSplitter> descriptors, int daubechyNumber) {
        String[] fileNames = OpenCVUtil.getFileNames(IMAGE_PATH);
        int choice = askImageToQuery(scanner, fileNames);
        System.out.println("\n\n\n\t\t\tYou selected: " + fileNames[choice]);

        int matches = scannerAskMatches(scanner);
        if(matches > fileNames.length) matches = 20;
        if(matches < 1) matches = 20;
        System.out.println("\t\tQuery Image: "+ fileNames[choice]);
        System.out.println("\t\tMatches: "+ matches + " matches");

        //Processes the Queryfile
        long startProcessing = System.currentTimeMillis();
        DaubechyColorSplitter processedMat = process(fileNames[choice], daubechyNumber);
        long endProcessing = System.currentTimeMillis();
        long elapsedProcessing = endProcessing - startProcessing;
        System.out.println("Processing: " + fileNames[choice] +"|\t Time: " + elapsedProcessing +"millis |\t ImgSize: "+ IMAGE_SIZE);

        //Performs the standard deviation
        long start = System.currentTimeMillis();
        Query.EuclidianDictionary[] euclidianDictionaries = Query.computeDistance(processedMat, descriptors);
        long end = System.currentTimeMillis();
        try {
            long elapsed = end - start;
            System.out.println("");
            System.out.println("Computing Euclidian Dictionary of: " + fileNames.length + "images |\t Time: " + elapsed +"millis |\t ImgSize: "+ IMAGE_SIZE);
            for (int i = 0; i < matches; i++) {
                System.out.println(i+1 + " > " + euclidianDictionaries[i].getDaubechyColorSplitter().getFileName() + "\t \t | Euclidean: " +  euclidianDictionaries[i].getEuclidian());
            }

            //show query image
            String originalPath = IMAGE_PATH + fileNames[choice];
            Mat imreadOriginal = Imgcodecs.imread(originalPath);
            OpenCVUtil.show(imreadOriginal, "Original: " + fileNames[choice]);

            for (int i = 0; i < matches; i++) {
                //Gets the name of the candidate image
                String name = euclidianDictionaries[i].getDaubechyColorSplitter().getFileName();

                //Gets the image path
                Mat imread = Imgcodecs.imread(IMAGE_PATH + name);
                OpenCVUtil.show(imread, "Rank " +i+ ": " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return askQueryAgain(scanner);
    }

    private static boolean askQueryAgain(Scanner scanner) {
        System.out.println("Would you like to query again? (y/n)");
        String s = scanner.next();
        if(s.toLowerCase().equals("y")) {
            return true;
        }else{
            return false;
        }
    }

    private static int askImageToQuery(Scanner scanner, String[] fileNames) {
        System.out.println("Select an image to query below");
        //User Interaction
        for (int i = 0; i < fileNames.length; i++) {
            System.out.println("[" + i + "] | " + fileNames[i]);
        }
        return scanner.nextInt();
    }

//    /**
//     * Process the image through the variety of stages and returns the {@link Mat}
//     * @param filename
//     */
//    public static Mat process(String filename, int daubechyNumber, int colorsplit) {
//        String filepath = IMAGE_PATH + filename;
//        Mat image = Imgcodecs.imread(filepath);
//        //Bilinear Interpolation
//        Imgproc.resize(image, image,new Size(IMAGE_SIZE,IMAGE_SIZE));
//
//        //Color Space
//        Mat[] split = ColorSplitter.split(image);
//        //DWT
//
//        Mat mat1 = applyDaubechy(split[0], daubechyNumber);
//        Mat mat2 = applyDaubechy(split[1], daubechyNumber);
//        Mat mat3 = applyDaubechy(split[2], daubechyNumber);
//
//        switch (colorsplit) {
//            case 1:
//                return mat1;
//            case 2:
//                return mat2;
//            case 3:
//                return mat3;
//            default:
//                return mat1;
//        }
//    }


//    /**
//     * Loads all the descriptors into memory given the parentDirectory path
//     * @param parentDirectory
//     * @return
//     * @throws IOException
//     */
//    public static LinkedHashMap<String, Descriptor> loadDescriptorsAsMap(File parentDirectory) throws IOException {
//        LinkedHashMap<String, Descriptor> descriptorHashMap = new LinkedHashMap<>();
//        if (parentDirectory.isDirectory()) {
//            File[] files = parentDirectory.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                BufferedReader bufferedReader = new BufferedReader(new FileReader(files[i]));
//                String line;
//                while((line = bufferedReader.readLine()) != null) {
//                    String fileName = files[i].getName();
//                    Descriptor descriptor = new Descriptor(fileName, Imgcodecs.imread(fileName));
//                    descriptorHashMap.put(fileName, descriptor);
//                }
//            }
//        }
//        System.out.println("All descriptors loaded successfully \n\n\n");
//        return descriptorHashMap;
//    }

//    /**
//     * Loads all the descriptors into memory given the parentDirectory path
//     * @param parentDirectory
//     * @return
//     * @throws IOException
//     */
//    public static List<Descriptor> loadDescriptorsAsList(File parentDirectory) throws IOException {
//        List<Descriptor> descriptorHashMap = new ArrayList<>();
//        if (parentDirectory.isDirectory()) {
//            File[] files = parentDirectory.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                BufferedReader bufferedReader = new BufferedReader(new FileReader(files[i]));
//                String line;
//                while((line = bufferedReader.readLine()) != null) {
//                    String fileName = files[i].getName();
//                    Descriptor descriptor = new Descriptor(fileName, Imgcodecs.imread(fileName));
//                    descriptorHashMap.add(descriptor);
//                }
//            }
//        }
//        System.out.println("All descriptors loaded successfully \n\n\n");
//        return descriptorHashMap;
//    }

    /**
     * (Re)creates the descriptor database given an image folder to processAndShow.
     * @param imageFolder
     * @return
     */
    public static List<DaubechyColorSplitter> createInMemoryDescriptorDatabase(File imageFolder, int daubechyNumber) {
        //delete file
        //write to file
        List<DaubechyColorSplitter> descriptors = new ArrayList<>();

        File[] files = imageFolder.listFiles();
        long startCompute = System.currentTimeMillis();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(file.isFile()) {
                //preprocessed
                String fileName = file.getName();
                DaubechyColorSplitter daubechyColorSplitter = process(fileName, daubechyNumber);
                descriptors.add(daubechyColorSplitter);
            }
        }
        long endCompute = System.currentTimeMillis();
        long elapsed = (endCompute-startCompute);

        System.out.println("Creating InMemDatabase: \t" + "ImgSize: " + IMAGE_SIZE);
        System.out.println("Processed: " + files.length +"files |\t Time: " + elapsed +"millis |\t ImgSize: "+ IMAGE_SIZE);
        return descriptors;
    }

    public static Mat process(String filename, int daubechyNumber, boolean showImages) {
        if(showImages) {
            return processAndShow(filename, daubechyNumber);
        }
        return processAndShow(filename, daubechyNumber);
    }

    public static DaubechyColorSplitter process(String filename, int daubechyNumber) {
        return processOnly(filename, daubechyNumber);
    }


    private static int scannerAskMatches(Scanner scanner) {
        System.out.println("How many matches would you like to return?");
        int matches = scanner.nextInt();
        return  matches;
    }

    private static int requestDaubechyNumber(Scanner scanner) {
        System.out.println("What number Daubechy Wavelet Transform [2-18]");
        return scanner.nextInt();
    }

//    public static Mat resizeKeepAspectRatio(Mat input, Size dstSize, Scalar bgcolor)
//    {
//        Mat output = new Mat();
//
//        double h1 = dstSize.width * (input.rows()/(double)input.cols());
//        double w2 = dstSize.height * (input.cols()/(double)input.rows());
//        if( h1 <= dstSize.height) {
//            Imgproc.resize( input, output,new Size(dstSize.width, h1));
//        } else {
//            Imgproc.resize( input, output, new Size(w2, dstSize.height));
//        }
//
//        int top = (int) ((dstSize.height-output.rows()) / 2);
//        int down = (int) ((dstSize.height-output.rows()+1) / 2);
//        int left = (int) ((dstSize.width - output.cols()) / 2);
//        int right = (int) ((dstSize.width - output.cols()+1) / 2);
//
//        Core.copyMakeBorder(output, output, top, down, left, right, Core.BORDER_CONSTANT, bgcolor );
//
//        return output;
//    }

    public static class ViewImage {
        private String windowName;
        private Mat mat;

        public ViewImage(String windowName, Mat mat) {
            this.windowName = windowName;
            this.mat = mat;
        }

        public String getWindowName() {
            return windowName;
        }

        public void setWindowName(String windowName) {
            this.windowName = windowName;
        }

        public Mat getMat() {
            return mat;
        }

        public void setMat(Mat mat) {
            this.mat = mat;
        }
    }

    private static Mat processAndShow(String filename, int daubechyNumber) {
        String filepath = IMAGE_PATH + filename;
        Mat image = Imgcodecs.imread(filepath);
        //Bilinear Interpolation
        Imgproc.resize(image, image,new Size(IMAGE_SIZE,IMAGE_SIZE));
        OpenCVUtil.show(image, "Original");
        //Color Space
        Mat[] split = ColorSplitter.split(image, IMAGE_SIZE);

        //DWT
        long start = System.currentTimeMillis();
        Mat mat = DaubechyOperation.applyDaubechy(image, daubechyNumber);
        long end = System.currentTimeMillis();
        long elapsed = end - start;

        System.out.println("Daubechy Operation (Original Image): \tTime: " +  elapsed + "millis\t" + "IMG_SIZE: " + IMAGE_SIZE);

        long startSplit = System.currentTimeMillis();
        for (int i = 0; i < split.length; i++) {
            Mat daubechyC = applyDaubechy(split[i], daubechyNumber);
        }
        long endSplit = System.currentTimeMillis();
        long elapsedSplit = endSplit - startSplit;
        System.out.println("Daubechy Operation (C1, C2, C3): \tTime: " +  elapsedSplit + "millis\t" + "IMG_SIZE: " + IMAGE_SIZE);

        for (int i = 0; i < split.length; i++) {
            OpenCVUtil.show(split[i], "C" + (i+1));
            Mat daubechyC = applyDaubechy(split[i], daubechyNumber);
            OpenCVUtil.show(daubechyC, "DWT C" + (i+1));
        }
        //Imgproc.resize(mat,mat,new Size(IMAGE_SIZE,IMAGE_SIZE));
        OpenCVUtil.show(mat, "DWT" + daubechyNumber + " - Original");
        return mat;
    }



    /**
     * Splits the colors into their coefficients c1,c2,c3 and applys daubechy wavelet transform
     * @param filename
     * @param daubechyNumber
     * @return
     */
    private static DaubechyColorSplitter processOnly(String filename, int daubechyNumber) {
        String filepath = IMAGE_PATH + filename;
        Mat image = Imgcodecs.imread(filepath);
        //Bilinear Interpolation
        Imgproc.resize(image, image,new Size(IMAGE_SIZE,IMAGE_SIZE));
        //Color Space
        Mat[] split = ColorSplitter.split(image, IMAGE_SIZE);
        //DWT

        Mat mat1 = applyDaubechy(split[0], daubechyNumber);
        Mat mat2 = applyDaubechy(split[1], daubechyNumber);
        Mat mat3 = applyDaubechy(split[2], daubechyNumber);
        return new DaubechyColorSplitter(filename, mat1, mat2, mat3);
    }
}


