package org.benja.tpu_tsb_2.ui;
import javafx.collections.FXCollections;
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
import org.benja.tpu_tsb_2.support.EDimensionDatos;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private SeriesIndexController seriesIndexController;
    public VBox window;
    public Button btnConsulta;
    @FXML
    private MenuItem btnQuit;

    @FXML
    private ComboBox<String> comboGenero;

    @FXML
    private ComboBox<EDimensionDatos> comboTipoDato;

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
        this.seriesIndexController = new SeriesIndexController();
        this.seriesIndexController.proccessDataFile();

        String[] genders = this.seriesIndexController.getAvailableGenders();
        this.comboGenero.setItems(FXCollections.observableArrayList(genders));

        this.comboTipoDato.setItems(FXCollections.observableArrayList(EDimensionDatos.values()));
    }
    @FXML
    public void btnConsultaClick(ActionEvent actionEvent) {
        String selectedGenre = this.comboGenero.getValue();
        EDimensionDatos selectedDimension = this.comboTipoDato.getValue();

        if (selectedGenre == null || selectedDimension == null) {
            return;
        }

        System.out.println(selectedGenre);
        System.out.println(selectedDimension);
    }
}