import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    ImageView imageView;
    @FXML
    Canvas canvas;
    @FXML
    Pane pane;
    @FXML
    Label mouseVal;
    @FXML
    HBox infoBox;
    @FXML
    Label rectLabelVal;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Slider thicknessSlider;


    private Image img = null;

    private boolean hasRect = false;
    private List<File> fileList = null;
    private Rectangle rectangle;
    private int x, y;
    private Iterator<File> fileIterator;
    private File currentFile;
    private String destination;
    private boolean saveTotxt, saveToCrop;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colorPicker.setValue(Color.RED);
        canvas.getGraphicsContext2D().setStroke(colorPicker.getValue());
        thicknessSlider.setValue(3);
        canvas.getGraphicsContext2D().setLineWidth(thicknessSlider.getValue());

    }


    // get info about program
    @FXML
    private void handleAboutAction(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText("This program was created to tag and crop image.\n Drag mouse to draw rect,\n Key 'Z' clears rect, \n Key 'Space' goes to next image");
        alert.showAndWait();
    }


    private void loadPicture() throws FileNotFoundException {

        //reset rect from recent image
        hasRect = false;
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // load next image
        if (fileIterator.hasNext()) {
            currentFile = fileIterator.next();
            FileInputStream io = new FileInputStream(currentFile);
            img = new Image(io);
            imageView.setImage(img);
            imageView.setPreserveRatio(false);


            adjustSize();
            showSizes();
        }

    }

    // load file list and load destination path and bool of saving txt and crop image
    void loadList(List<File> list, String path, boolean saveTxt, boolean saveCrop) {

        fileList = list;
        fileIterator = fileList.iterator();
        destination = path + "/";
        saveTotxt = saveTxt;
        saveToCrop = saveCrop;

        System.out.println(saveTxt);

        // select first image to load
        try {
            loadPicture();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // resize picture
    private void adjustSize() {

        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setResizable(false);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = primaryScreenBounds.getWidth();
        double screenHeight = primaryScreenBounds.getHeight();

        // Count the scale
        double Sx = img.getWidth() / screenWidth;
        double Sy = img.getHeight() / screenHeight;
        double S = Sx > Sy ? Sx : Sy;

        // The scale should be more than 1
        S = S < 1 ? 1 : S;

        pane.setMaxSize(img.getWidth() / S, img.getHeight() / S);
        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.fitHeightProperty().bind(pane.heightProperty());
        canvas.widthProperty().bind(imageView.fitWidthProperty());
        canvas.heightProperty().bind(imageView.fitHeightProperty());
        stage.setMinHeight(imageView.getFitHeight() + infoBox.getHeight());


    }

    // printing basic info to debug
    void showSizes() {
        System.out.println("Window size: \tW = " + pane.getScene().getWindow().getWidth() + "\tH = " + pane.getScene().getWindow().getHeight());
        System.out.println("Scene size: \tW = " + pane.getScene().getWindow().getScene().getWidth() + "\tH = " + pane.getScene().getWindow().getScene().getHeight());
        System.out.println("Pane size:  \tW = " + pane.getWidth() + "\tH = " + pane.getHeight());
        System.out.println("ImageView size:\tW = " + imageView.getFitWidth() + "\tH = " + imageView.getFitHeight());
        System.out.println("Canvas size: \tW = " + canvas.getWidth() + "\tH = " + canvas.getHeight());
        System.out.println("Image size: \tW = " + img.getWidth() + "\tH = " + img.getHeight());
    }

    @FXML
    void colorChooser() {
        canvas.getGraphicsContext2D().setStroke(colorPicker.getValue());
    }

    @FXML
    void thicknessSlider() {
        canvas.getGraphicsContext2D().setLineWidth(thicknessSlider.getValue());
    }


    // drawing rect while mouse drag
    @FXML
    void handleMouseDrag(MouseEvent event) {

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int x1, x2, y1, y2;

        x1 = (x <= event.getX()) ? x : (int) event.getX();
        x2 = (x <= event.getX()) ? (int) event.getX() : x;
        y1 = (y <= event.getY()) ? y : (int) event.getY();
        y2 = (y <= event.getY()) ? (int) event.getY() : y;

        canvas.getGraphicsContext2D().strokeRect(x1, y1, x2 - x1, y2 - y1);
        mouseVal.setText("X: " + (int) event.getX() + " Y: " + (int) event.getY());
        canvas.requestFocus();
    }


    // draw rect
    @FXML
    void ReleasedMouse(MouseEvent event) {
        if (!hasRect) {

            hasRect = true;
            rectLabelVal.setText("X1: " + x + " X2: " + event.getX() + "\n" + "Y1: " + y + " Y2: " + event.getY());
            rectangle = new Rectangle(x, (int) event.getX(), y, (int) event.getY());
        }
    }

    @FXML
    public void handleKey(KeyEvent event) throws IOException {

        // Press Z to clear drawn rect
        if (event.getCode() == KeyCode.Z) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            rectLabelVal.setText("No Rectangle");
            hasRect = false;
            rectangle = null;

        } else if (event.getCode() == KeyCode.SPACE) {

            if (hasRect) {

                // remove extension from fie name
                int pos = currentFile.getName().lastIndexOf('.');
                String nameWithoutExtension = currentFile.getName().substring(0, pos);


                // save images and text
                try {

                    // save image with rect

                    File output = new File(destination + "pictures/" + nameWithoutExtension + ".png");
                    WritableImage wrimg = new WritableImage((int) pane.getWidth(), (int) pane.getHeight());
                    pane.snapshot(null, wrimg);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(wrimg, null);
                    ImageIO.write(renderedImage, "png", output);

                    //save cropped image
                    if (saveToCrop) {

                        File output_crop = new File(destination + "cropped/" + nameWithoutExtension + ".png");
                        System.out.println(rectangle.getX1() + "  " + (rectangle.getX2() - rectangle.getX1()));
                        WritableImage wrimg_crop = new WritableImage(img.getPixelReader(), rectangle.getX1(), rectangle.getY1(), rectangle.getX2() - rectangle.getX1(), rectangle.getY2() - rectangle.getY1());
                        RenderedImage renderedImage_crop = SwingFXUtils.fromFXImage(wrimg_crop, null);
                        ImageIO.write(renderedImage_crop, "png", output_crop);
                    }

                    // The template of txt is:  X1  \n X2 \n Y1 \n Y2 \n Width of image \n Heighr of image

                    if (saveTotxt) {
                        File outputTxt = new File(destination + "coordinates/" + nameWithoutExtension + ".txt");
                        PrintWriter pw = new PrintWriter(outputTxt);
                        pw.println(rectangle.getX1());
                        pw.println(rectangle.getX2());
                        pw.println(rectangle.getY1());
                        pw.println(rectangle.getY2());
                        pw.println((int) imageView.getFitWidth());
                        pw.println((int) imageView.getFitHeight());
                        pw.close();
                    }
//
                } catch (IOException e) {
                    System.out.println("Error while saving file");
                    System.exit(-1);
                } catch (Exception e) {
                    System.out.println(e.toString());

                }
            }
            rectLabelVal.setText("No Rectangle");
            hasRect = false;
            rectangle = null;
            loadPicture();
        }
    }

    // handle drawing rect
    @FXML
    public void handleMousePressed(MouseEvent event) {

        // clear drawn rect
        if (hasRect) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            rectLabelVal.setText("No Rectangle");
            hasRect = false;
            rectangle = null;

        }

        //save coordinates
        x = (int) (event.getX());
        y = (int) (event.getY());

    }


    // handle mouse moved to inform about cursor position
    @FXML
    public void handleMouseMoved(MouseEvent event) {
        mouseVal.setText("X: " + (int) event.getX() + " Y: " + (int) event.getY());
    }
}
