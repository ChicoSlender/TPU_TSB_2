package org.benja.tpu_tsb_2.ui;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.benja.tpu_tsb_2.business.SeriesIndexController;
import org.benja.tpu_tsb_2.support.EDimensionDatos;
import org.benja.tpu_tsb_2.support.Serie;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Label labelResultadoCantidad;
    private SeriesIndexController seriesIndexController;
    public VBox window;
    public Button btnConsulta;
    @FXML
    private MenuItem btnQuit;

    @FXML
    private ComboBox<String> comboGenero;

    @FXML
    private ComboBox<EDimensionDatos> comboTipoDato;

    private String selectedGenre;

    private Stage stage;

    @FXML
    void btnQuitClick(ActionEvent event) {
        getStage().close();
    }

    @FXML
    void comboGeneroValueChange(ActionEvent event) {

    }

    @FXML
    void comboTipoDatoValueChange(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.labelResultadoCantidad.setText("");

        this.seriesIndexController = new SeriesIndexController();
        this.seriesIndexController.proccessDataFile();

        String[] genders = this.seriesIndexController.getAvailableGenders();
        this.comboGenero.setItems(FXCollections.observableArrayList(genders));

        this.comboTipoDato.setItems(FXCollections.observableArrayList(EDimensionDatos.values()));
    }
    @FXML
    public void btnConsultaClick(ActionEvent actionEvent) {
        this.labelResultadoCantidad.setText("");
        this.selectedGenre = this.comboGenero.getValue();
        EDimensionDatos selectedDimension = this.comboTipoDato.getValue();

        if (this.selectedGenre == null || selectedDimension == null) {
            return;
        }

        switch (selectedDimension) {
            case CANTIDAD_SERIES:
                showSeriesCountForSelectedGenre();
                break;
            case LISTADO_SERIES:
                showSeriesDetailForSelectedGenre();
                break;
        }
    }

    private void showSeriesCountForSelectedGenre() {
        Integer seriesCount = this.seriesIndexController.getSeriesCountForGenre(this.selectedGenre);

        this.labelResultadoCantidad.setText("Cantidad de series del género " + this.selectedGenre + ": " + seriesCount.toString());
    }

    private void showSeriesDetailForSelectedGenre() {
        List<Serie> series = this.seriesIndexController.getSeriesDetailsForGenre(this.selectedGenre);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("series-list-view.fxml"));
            Scene seriesListScene = new Scene(fxmlLoader.load(), 640, 400);
            SeriesListController seriesListController = (SeriesListController) fxmlLoader.getController();

            Stage newStage = new Stage();
            newStage.setScene(seriesListScene);
            newStage.show();
            seriesListController.fillTable(series);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando vista de la lista de series por género");
        }
    }

    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) window.getScene().getWindow();
        }

        return this.stage;
    }
}