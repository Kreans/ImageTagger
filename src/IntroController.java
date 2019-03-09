
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;


public class IntroController  {


    @FXML    TextField browseInField, browseOutField;
    @FXML    Button buttonTag;


    @FXML
    public void clickedBrowseIn(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose source folder");
        File dir = directoryChooser.showDialog(null);

        if(dir!=null)
            browseInField.setText(dir.getAbsolutePath());
    }

    @FXML
    public void clickedBrowseOut(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose destination folder");
        File dir = directoryChooser.showDialog(null);

        if(dir!=null)
            browseOutField.setText(dir.getAbsolutePath());
    }


    @FXML
    public void End(){
        File fIn = new File(browseInField.getText());
        File fOut = new File(browseOutField.getText());
        Boolean bl1, bl2;
        if ((bl1=!fIn.isDirectory())||(bl2 = !fOut.isDirectory())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error has been found :(ub");
            String str1, str2;
            alert.setHeaderText("Source or Destination Directory are not correct");
            alert.show();

            return;
        }


        new File(browseOutField.getText() + "/DataSetReady/coords").mkdirs();
        new File(browseOutField.getText() + "/DataSetReady/pictures").mkdirs();

        File[] list = new File(browseInField.getText()).listFiles();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
            Parent root = loader.load();
            Stage stage = Main.getStage();
            stage.hide();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setScene(new Scene(root,primaryScreenBounds.getWidth(),primaryScreenBounds.getHeight()));
            Controller ctr = loader.getController();
            ctr.loadList(list);
            stage.show();
            ctr.showSizes();

        }catch(IOException ex){
            System.exit(-1);
        }

    }



}
