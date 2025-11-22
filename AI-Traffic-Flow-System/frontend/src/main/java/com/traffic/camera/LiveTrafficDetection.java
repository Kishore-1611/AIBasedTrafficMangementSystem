//
//package com.traffic.camera;
//
//import net.sourceforge.tess4j.Tesseract;
//import org.opencv.core.*;
//import org.opencv.dnn.Dnn;
//import org.opencv.dnn.Net;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.utils.Converters;
//import org.opencv.videoio.VideoCapture;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.time.LocalDateTime;
//import java.util.*;
//import org.opencv.core.Point;
//import java.util.List;
//
//public class LiveTrafficDetection {
//    private static boolean isRunning = false;
//
//    private static JLabel vehicleCountLabel;
//    private static JLabel statusLabel;
//    private static JLabel videoLabel;
//    private static JTextArea plateLogArea;
//    private static JPanel signalPanel;
//    private static JTextField currentLocationField;
//    private static volatile String location = "Unknown";
//
//    public static void setVehicleCountLabel(JLabel label) { vehicleCountLabel = label; }
//    public static void setStatusLabel(JLabel label) { statusLabel = label; }
//    public static void setVideoLabel(JLabel label) { videoLabel = label; }
//    public static void setPlateLogArea(JTextArea area) { plateLogArea = area; }
//    public static void setSignalPanel(JPanel panel) { signalPanel = panel; }
//    public static void setCurrentLocationField(JTextField field) { currentLocationField = field; }
//
//    public static void setDetectedLocation(String loc) {
//        if (loc != null && !loc.isBlank()) location = loc;
//    }
//
//    public static void startCamera() {
//        isRunning = true;
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        VideoCapture camera = new VideoCapture(0);
//        if (!camera.isOpened()) {
//            showStatus("Camera not found.");
//            return;
//        }
//        showStatus("Camera Initializing...");
//
//        Net vehicleNet = Dnn.readNetFromDarknet("models/yolov4.cfg", "models/yolov4.weights");
//        Net plateNet = Dnn.readNetFromDarknet("models/plate-yolov4.cfg", "models/plate-yolov4.weights");
//
//        List<String> vehicleClasses = loadClassNames("models/coco.names");
//        List<String> plateClasses = loadClassNames("models/plate.names");
//        Set<String> vehicleTypes = Set.of("car", "bus", "motorbike", "truck");
//
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("tessdata");
//        tesseract.setLanguage("eng");
//        tesseract.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
//
//        Mat frame = new Mat();
//        while (isRunning && camera.read(frame)) {
//            Imgproc.resize(frame, frame, new Size(640, 480));
//            List<Map<String, String>> vehiclesToSend = new ArrayList<>();
//
//            List<Detection> vehicleDetections = detectObjects(frame, vehicleNet, vehicleClasses, vehicleTypes);
//            for (Detection det : vehicleDetections) {
//                Imgproc.rectangle(frame, det.box, new Scalar(0, 255, 0), 2);
//                Imgproc.putText(frame, det.label, new Point(det.box.x, det.box.y - 5),
//                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(255, 0, 0), 2);
//
//                String numberPlate = "";
//                try {
//                    Mat vehicleROI = new Mat(frame, det.box);
//                    List<Detection> plates = detectObjects(vehicleROI, plateNet, plateClasses, Set.of("plate"));
//                    if (!plates.isEmpty()) {
//                        Rect pbox = plates.get(0).box;
//                        Mat plateImg = new Mat(vehicleROI, pbox);
//                        Imgproc.cvtColor(plateImg, plateImg, Imgproc.COLOR_BGR2GRAY);
//                        Imgproc.bilateralFilter(plateImg, plateImg, 11, 17, 17);
//                        Imgproc.adaptiveThreshold(plateImg, plateImg, 255,
//                                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
//                        Imgcodecs.imwrite("ocr_input.jpg", plateImg);
//
//                        numberPlate = tesseract.doOCR(new File("ocr_input.jpg"))
//                                .replaceAll("[^A-Z0-9]", "").trim();
//
//                        if (!numberPlate.isEmpty() && plateLogArea != null) {
//                            String finalPlate = numberPlate;
//                            SwingUtilities.invokeLater(() -> plateLogArea.append(finalPlate + "\n"));
//                        }
//
//                        System.out.println("🔍 OCR Result: " + numberPlate);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//                Map<String, String> data = new HashMap<>();
//                data.put("vehicleType", det.label);
//                data.put("numberPlate", numberPlate);
//                data.put("timestamp", LocalDateTime.now().toString());
//                String liveLocation = currentLocationField != null ? currentLocationField.getText().trim() : location;
//                data.put("location", liveLocation.isEmpty() ? "Unknown" : liveLocation);
//                vehiclesToSend.add(data);
//            }
//
//            int vehicleCount = vehicleDetections.size();
//            SwingUtilities.invokeLater(() -> {
//                if (vehicleCountLabel != null)
//                    vehicleCountLabel.setText("Vehicle: " + vehicleCount);
//
//                if (signalPanel != null) {
//                    Color signalColor = vehicleCount > 10 ? Color.RED :
//                                        vehicleCount > 5 ? Color.ORANGE : Color.GREEN;
//                    signalPanel.setBackground(signalColor);
//                }
//
//                if (statusLabel != null) statusLabel.setText("Running");
//                if (videoLabel != null) videoLabel.setIcon(new ImageIcon(matToBufferedImage(frame)));
//            });
//
//            if (!vehiclesToSend.isEmpty()) sendToBackend(vehiclesToSend);
//        }
//
//        camera.release();
//        showStatus("Stopped");
//    }
//
//    public static void stopCamera() {
//        isRunning = false;
//    }
//
//    private static void showStatus(String msg) {
//        SwingUtilities.invokeLater(() -> {
//            if (statusLabel != null) statusLabel.setText(msg);
//        });
//    }
//
//    private static List<Detection> detectObjects(Mat image, Net net, List<String> classNames, Set<String> targetClasses) {
//        List<Rect> boxes = new ArrayList<>();
//        List<Float> confidences = new ArrayList<>();
//        List<String> labels = new ArrayList<>();
//
//        Mat blob = Dnn.blobFromImage(image, 1 / 255.0, new Size(416, 416), new Scalar(0, 0, 0), true, false);
//        net.setInput(blob);
//        List<Mat> outputs = new ArrayList<>();
//        net.forward(outputs, net.getUnconnectedOutLayersNames());
//
//        for (Mat result : outputs) {
//            for (int i = 0; i < result.rows(); i++) {
//                float confidence = (float) result.get(i, 4)[0];
//                if (confidence > 0.5) {
//                    Mat scores = result.row(i).colRange(5, result.cols());
//                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
//                    int classId = (int) mm.maxLoc.x;
//                    float finalConf = (float) mm.maxVal;
//                    if (finalConf > 0.5 && classId < classNames.size()) {
//                        String label = classNames.get(classId);
//                        if (!targetClasses.contains(label)) continue;
//
//                        float cx = (float) result.get(i, 0)[0] * image.cols();
//                        float cy = (float) result.get(i, 1)[0] * image.rows();
//                        float w = (float) result.get(i, 2)[0] * image.cols();
//                        float h = (float) result.get(i, 3)[0] * image.rows();
//
//                        int x = Math.max(0, (int) (cx - w / 2));
//                        int y = Math.max(0, (int) (cy - h / 2));
//                        int width = Math.min((int) w, image.cols() - x);
//                        int height = Math.min((int) h, image.rows() - y);
//
//                        boxes.add(new Rect(x, y, width, height));
//                        confidences.add(finalConf);
//                        labels.add(label);
//                    }
//                }
//            }
//        }
//
//        if (boxes.isEmpty()) return Collections.emptyList();
//
//        MatOfFloat matConfidences = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
//        MatOfRect2d matBoxes = new MatOfRect2d();
//        boxes.forEach(rect -> matBoxes.push_back(new MatOfRect2d(new Rect2d(rect.x, rect.y, rect.width, rect.height))));
//        MatOfInt indices = new MatOfInt();
//        Dnn.NMSBoxes(matBoxes, matConfidences, 0.5f, 0.4f, indices);
//
//        List<Detection> detections = new ArrayList<>();
//        for (int idx : indices.toArray()) {
//            detections.add(new Detection(boxes.get(idx), labels.get(idx)));
//        }
//
//        return detections;
//    }
//
//    private static List<String> loadClassNames(String path) {
//        List<String> names = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//            String line;
//            while ((line = br.readLine()) != null) names.add(line.trim());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return names;
//    }
//
//    private static BufferedImage matToBufferedImage(Mat matrix) {
//        int type = matrix.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
//        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
//        matrix.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
//        return image;
//    }
//
//    private static void sendToBackend(List<Map<String, String>> vehicles) {
//        try {
//            URL url = new URL("http://localhost:8080/api/traffic/save/details");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("POST");
//            con.setRequestProperty("Content-Type", "application/json");
//            con.setDoOutput(true);
//
//            StringBuilder json = new StringBuilder("[");
//            for (int i = 0; i < vehicles.size(); i++) {
//                Map<String, String> v = vehicles.get(i);
//                json.append("{\"vehicleType\":\"").append(v.get("vehicleType")).append("\",")
//                        .append("\"numberPlate\":\"").append(v.get("numberPlate")).append("\",")
//                        .append("\"timestamp\":\"").append(v.get("timestamp")).append("\",")
//                        .append("\"location\":\"").append(v.get("location")).append("\"}");
//                if (i < vehicles.size() - 1) json.append(",");
//            }
//            json.append("]");
//
//            try (OutputStream os = con.getOutputStream()) {
//                byte[] input = json.toString().getBytes("utf-8");
//                os.write(input, 0, input.length);
//            }
//
//            con.getResponseCode();
//        } catch (Exception ignored) {}
//    }
//
//    private static class Detection {
//        Rect box;
//        String label;
//
//        Detection(Rect box, String label) {
//            this.box = box;
//            this.label = label;
//        }
//    }
//}
//


package com.traffic.camera;

import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import org.opencv.core.Point;
import java.util.List;

public class LiveTrafficDetection {
    private static boolean isRunning = false;

    private static JLabel vehicleCountLabel;
    private static JLabel statusLabel;
    private static JLabel videoLabel;
    private static JTextArea plateLogArea;
    private static JPanel signalPanel;
    private static JTextField currentLocationField;
    private static volatile String location = "Unknown";

    public static void setVehicleCountLabel(JLabel label) { vehicleCountLabel = label; }
    public static void setStatusLabel(JLabel label) { statusLabel = label; }
    public static void setVideoLabel(JLabel label) { videoLabel = label; }
    public static void setPlateLogArea(JTextArea area) { plateLogArea = area; }
    public static void setSignalPanel(JPanel panel) { signalPanel = panel; }
    public static void setCurrentLocationField(JTextField field) { currentLocationField = field; }

    public static void setDetectedLocation(String loc) {
        if (loc != null && !loc.isBlank()) location = loc;
    }

    public static void startCamera() {
        isRunning = true;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            showStatus("Camera not found.");
            return;
        }
        showStatus("Camera Initializing...");

        Net vehicleNet = Dnn.readNetFromDarknet("models/yolov4.cfg", "models/yolov4.weights");
        Net plateNet = Dnn.readNetFromDarknet("models/yolov4-obj.cfg", "models/YOLOv4-obj_1000.weights");

        List<String> vehicleClasses = loadClassNames("models/coco.names");
        List<String> plateClasses = loadClassNames("models/obj.names");
        Set<String> vehicleTypes = Set.of("car", "bus", "motorbike", "truck");

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng");
        tesseract.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

        Mat frame = new Mat();
        while (isRunning && camera.read(frame)) {
            Imgproc.resize(frame, frame, new Size(640, 480));
            List<Map<String, String>> vehiclesToSend = new ArrayList<>();

            List<Detection> vehicleDetections = detectObjects(frame, vehicleNet, vehicleClasses, vehicleTypes);
            for (Detection det : vehicleDetections) {
                Imgproc.rectangle(frame, det.box, new Scalar(0, 255, 0), 2);
                Imgproc.putText(frame, det.label, new Point(det.box.x, det.box.y - 5),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(255, 0, 0), 2);

                String numberPlate = "";
                try {
                    Mat vehicleROI = new Mat(frame, det.box);
                    List<Detection> plates = detectObjects(vehicleROI, plateNet, plateClasses, Set.of("plate"));
                    if (!plates.isEmpty()) {
                        Rect pbox = plates.get(0).box;
                        Mat plateImg = new Mat(vehicleROI, pbox);
                        Imgproc.cvtColor(plateImg, plateImg, Imgproc.COLOR_BGR2GRAY);
                        Imgproc.bilateralFilter(plateImg, plateImg, 11, 17, 17);
                        Imgproc.adaptiveThreshold(plateImg, plateImg, 255,
                                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
                        Imgcodecs.imwrite("ocr_input.jpg", plateImg);

                        numberPlate = tesseract.doOCR(new File("ocr_input.jpg"))
                                .replaceAll("[^A-Z0-9]", "").trim();

                        if (!numberPlate.isEmpty() && plateLogArea != null) {
                            String finalPlate = numberPlate;
                            SwingUtilities.invokeLater(() -> plateLogArea.append(finalPlate + "\n"));
                        }
                        System.out.println("🔍 OCR Result: " + numberPlate);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Map<String, String> data = new HashMap<>();
                data.put("vehicleType", det.label);
                data.put("numberPlate", numberPlate);
                data.put("timestamp", LocalDateTime.now().toString());
                String liveLocation = currentLocationField != null ? currentLocationField.getText().trim() : location;
                data.put("location", liveLocation.isEmpty() ? "Unknown" : liveLocation);
                vehiclesToSend.add(data);
            }

            int vehicleCount = vehicleDetections.size();
            SwingUtilities.invokeLater(() -> {
                if (vehicleCountLabel != null)
                    vehicleCountLabel.setText("Vehicle: " + vehicleCount);

                if (signalPanel != null) {
                    Color signalColor = vehicleCount > 10 ? Color.RED :
                                        vehicleCount > 5 ? Color.ORANGE : Color.GREEN;
                    signalPanel.setBackground(signalColor);
                }

                if (statusLabel != null) statusLabel.setText("Running");
                if (videoLabel != null) videoLabel.setIcon(new ImageIcon(matToBufferedImage(frame)));
            });

            if (!vehiclesToSend.isEmpty()) sendToBackend(vehiclesToSend);
        }

        camera.release();
        showStatus("Stopped");
    }

    public static void stopCamera() {
        isRunning = false;
    }

    private static void showStatus(String msg) {
        SwingUtilities.invokeLater(() -> {
            if (statusLabel != null) statusLabel.setText(msg);
        });
    }

    private static List<Detection> detectObjects(Mat image, Net net, List<String> classNames, Set<String> targetClasses) {
        List<Rect> boxes = new ArrayList<>();
        List<Float> confidences = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Mat blob = Dnn.blobFromImage(image, 1 / 255.0, new Size(416, 416), new Scalar(0, 0, 0), true, false);
        net.setInput(blob);
        List<Mat> outputs = new ArrayList<>();
        net.forward(outputs, net.getUnconnectedOutLayersNames());

        for (Mat result : outputs) {
            for (int i = 0; i < result.rows(); i++) {
                float confidence = (float) result.get(i, 4)[0];
                if (confidence > 0.5) {
                    Mat scores = result.row(i).colRange(5, result.cols());
                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                    int classId = (int) mm.maxLoc.x;
                    float finalConf = (float) mm.maxVal;
                    if (finalConf > 0.5 && classId < classNames.size()) {
                        String label = classNames.get(classId);
                        if (!targetClasses.contains(label)) continue;

                        float cx = (float) result.get(i, 0)[0] * image.cols();
                        float cy = (float) result.get(i, 1)[0] * image.rows();
                        float w = (float) result.get(i, 2)[0] * image.cols();
                        float h = (float) result.get(i, 3)[0] * image.rows();

                        int x = Math.max(0, (int) (cx - w / 2));
                        int y = Math.max(0, (int) (cy - h / 2));
                        int width = Math.min((int) w, image.cols() - x);
                        int height = Math.min((int) h, image.rows() - y);

                        boxes.add(new Rect(x, y, width, height));
                        confidences.add(finalConf);
                        labels.add(label);
                    }
                }
            }
        }

        if (boxes.isEmpty()) return Collections.emptyList();

        MatOfFloat matConfidences = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
        MatOfRect2d matBoxes = new MatOfRect2d();
        boxes.forEach(rect -> matBoxes.push_back(new MatOfRect2d(new Rect2d(rect.x, rect.y, rect.width, rect.height))));
        MatOfInt indices = new MatOfInt();
        Dnn.NMSBoxes(matBoxes, matConfidences, 0.5f, 0.4f, indices);

        List<Detection> detections = new ArrayList<>();
        for (int idx : indices.toArray()) {
            detections.add(new Detection(boxes.get(idx), labels.get(idx)));
        }

        return detections;
    }

    private static List<String> loadClassNames(String path) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) names.add(line.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    private static BufferedImage matToBufferedImage(Mat matrix) {
        int type = matrix.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        matrix.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }

    private static void sendToBackend(List<Map<String, String>> vehicles) {
        try {
            URL url = new URL("http://localhost:8080/api/traffic/save/details");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < vehicles.size(); i++) {
                Map<String, String> v = vehicles.get(i);
                
                json.append("{\"vehicleType\":\"").append(v.get("vehicleType")).append("\",")
                .append("\"numberPlate\":\"").append(v.get("numberPlate")).append("\",")
                .append("\"timestamp\":\"").append(v.get("timestamp")).append("\",")
                .append("\"location\":\"").append(v.get("location")).append("\"}");

                if (i < vehicles.size() - 1) json.append(",");
            }
            json.append("]");

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            con.getResponseCode();
        } catch (Exception ignored) {}
    }

    private static class Detection {
        Rect box;
        String label;

        Detection(Rect box, String label) {
            this.box = box;
            this.label = label;
        }
    }
}

