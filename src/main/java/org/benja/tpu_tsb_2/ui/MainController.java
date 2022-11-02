package org.benja.tpu_tsb_2.ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.benja.tpu_tsb_2.business.SeriesIndexController;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public VBox window;
    public Button btnConsulta;
    @FXML
    private MenuItem btnQuit;

    @FXML
    private ComboBox<?> comboGenero;

    @FXML
    private ComboBox<?> comboTipoDato;

    @FXML
    private TableView<?> tableSeries;

    @FXML
    void btnQuitClick(ActionEvent event) {
        Stage stage = (Stage) window.getScene().getWindow();
        stage.close();
    }

    @FXML
    void comboGeneroValueChange(ActionEvent event) {

    }

    @FXML
    void comboTipoDatoValueChange(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void btnConsultaClick(ActionEvent actionEvent) {

    }
}