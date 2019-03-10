
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

// This class is controller for first windows, where user can choose files to tag.

public class IntroController  {


    @FXML    TextField browseInField, browseOutField;
    @FXML    Button buttonTag;

    private List<File> selectedFile;

    @FXML
    public void clickedBrowseIn(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        selectedFile = fileChooser.showOpenMultipleDialog(null);

        if(!selectedFile.isEmpty())
            browseInField.setText(String.join(",", selectedFile.toString()));       // Add to textbox names of all selected files
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

        File fOut = new File(browseOutField.getText());

        if ( selectedFile.isEmpty() || !fOut.isDirectory() ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error has been found :(ub");
            alert.setHeaderText("Source or Destination Directory are not correct");
            alert.show();

            return;
        }


        new File(browseOutField.getText() + "/DataSetReady/coords").mkdirs();
        new File(browseOutField.getText() + "/DataSetReady/pictures").mkdirs();


        for (File f: selectedFile ) {
            System.out.println(f.getName());
        }      
        

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
            Parent root = loader.load();
            Stage stage = Main.getStage();
            stage.hide();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setScene(new Scene(root,primaryScreenBounds.getWidth(),primaryScreenBounds.getHeight()));
            Controller ctr = loader.getController();
            ctr.loadList(selectedFile, browseOutField.getText());
            stage.show();
            ctr.showSizes();

        }catch(IOException ex){
            System.exit(-1);
        }

    }



}
