import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
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
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML ImageView imageView;
    @FXML Canvas canvas;
    @FXML Pane pane;
    @FXML Label mouseVal;
    @FXML HBox infoBox;
    @FXML Label rectLabelVal;
    @FXML ColorPicker colorPicker;
    @FXML Slider thicknessSlider;

    private Image img = null;

    private boolean hasRect = false;
    private int counter = 0;
    private File[] fileList = null;
    private Rectangle rectangle;
    private int x, y;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colorPicker.setValue(Color.RED);
        canvas.getGraphicsContext2D().setStroke(colorPicker.getValue());
        thicknessSlider.setValue(3);
        canvas.getGraphicsContext2D().setLineWidth(thicknessSlider.getValue());

    }

    private void loadPicture() throws FileNotFoundException {
        hasRect = false;
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (counter < fileList.length) {
            if (fileList[counter] != null) {
                FileInputStream io = new FileInputStream(fileList[counter]);
                img = new Image(io);
                imageView.setImage(img);
                imageView.setPreserveRatio(false);

            }
            if (counter != fileList.length - 1)
                counter++;
        }
        adjustSize();
        showSizes();

    }

    void loadList(File[] list) {

        if (list != null) {

            for (int i = 0; i < list.length; i++) {
                if (!list[i].isFile()) list[i] = null;

                else {
                    String extension;

                    int j = list[i].getName().lastIndexOf('.');
                    if (j > 0) {
                        extension = list[i].getName().substring(j + 1);
                        if (!extension.equals("jpg"))
                            list[i] = null;
                    }
                }
            }
            hasRect = false;

            while (list[counter] == null)
                counter++;

            fileList = list;

            try {
                loadPicture();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else System.exit(-1);

    }

    private void adjustSize() {

        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setResizable(false);
        pane.setMaxSize(img.getWidth(), img.getHeight());
        pane.setMinSize(img.getWidth(), img.getHeight());
        imageView.fitWidthProperty().bind(pane.widthProperty());
        imageView.fitHeightProperty().bind(pane.heightProperty());
        canvas.widthProperty().bind(imageView.fitWidthProperty());
        canvas.heightProperty().bind(imageView.fitHeightProperty());
        stage.setMinHeight(imageView.getFitHeight() + infoBox.getHeight());


    }

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

    @FXML
    void handleMouseDrag(MouseEvent event) {

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int x1,x2,y1,y2;

        x1 = (x <= event.getX()) ? x : (int)event.getX();
        x2 = (x <= event.getX()) ? (int)event.getX() : x;
        y1 = (y <= event.getY()) ? y : (int)event.getY();
        y2 = (y <= event.getY()) ? (int)event.getY() : y;

        canvas.getGraphicsContext2D().strokeRect(x1, y1, x2 - x1, y2 - y1);
        mouseVal.setText("X: " + (int) event.getX() + " Y: " + (int) event.getY());
        canvas.requestFocus();
    }

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
        if (event.getCode() == KeyCode.Z) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            rectLabelVal.setText("No Rectangle");
            hasRect = false;
            rectangle = null;

        } else if (event.getCode() == KeyCode.SPACE) {

            if (hasRect) {

                try {
                    File output = new File(fileList[counter].getParent() + "/DataSetReady/pictures/" + fileList[counter].getName() + ".png");
                    File outputTxt = new File(fileList[counter].getParent() + "/DataSetReady/coords/" + fileList[counter].getName() + ".txt");
                    WritableImage wrimg = new WritableImage((int) pane.getWidth(), (int) pane.getHeight());
                    pane.snapshot(null, wrimg);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(wrimg, null);
                    ImageIO.write(renderedImage, "png", output);

                    PrintWriter pw = new PrintWriter(outputTxt, "UTF-8");
                    pw.println(rectangle.getX1());
                    pw.println(rectangle.getX2());
                    pw.println(rectangle.getY1());
                    pw.println(rectangle.getY2());
                    pw.close();

                } catch (IOException e) {
                    System.out.println("I'm an IOException :)");
                    System.exit(-1);
                } catch (Exception e) {
                    System.out.println("rrr");
                    counter++;
                }
            }
            rectLabelVal.setText("No Rectangle");
            hasRect = false;
            rectangle = null;
            loadPicture();
        }
    }

    @FXML
    public void handleMousePressed(MouseEvent event) {

        if (!hasRect) {
            x = (int) (event.getX());
            y = (int) (event.getY());
        } else {

            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            rectLabelVal.setText("No Rectangle");
            hasRect = false;
            rectangle = null;
            x = (int) (event.getX());
            y = (int) (event.getY());
        }
    }


    @FXML
    public void handleMouseMoved(MouseEvent event) {
        mouseVal.setText("X: " + (int) event.getX() + " Y: " + (int) event.getY());
    }
}
