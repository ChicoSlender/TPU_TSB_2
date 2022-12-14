package org.benja.tpu_tsb_2.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.benja.tpu_tsb_2.support.Serie;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller para la ventana que contiene la tabla con la lista detallada de series pertenecientes a un género seleccionado.
 * */
public class SeriesListController implements Initializable {

    @FXML
    private TableView<SerieTableRow> seriesTable;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initTable();
    }

    /**
     * Método auxiliar para construir y agregar las columnas de la tabla de series al momento de la inicialización.
     * */
    private void initTable() {
        String[] titles = new String[] {"Título", "Emisión", "Calificación", "Dur. episodios", "Puntaje", "Sinopsis", "Votos", "Protagonista 1", "Protagonista 2", "Protagonista 3", "Protagonista 4"};
        String[] properties = new String[] {"title", "seriesRuntime", "certificate", "episodesRuntime", "imdbRating", "overview", "votes", "star1", "star2", "star3", "star4"};
        double[] widths = new double[] {200, 100, 75, 100, 75, 350, 75, 100, 100, 100, 100};

        for (int i = 0; i < titles.length; i++) {
            TableColumn<SerieTableRow, String> column = new TableColumn<>(titles[i]);
            column.setCellValueFactory(new PropertyValueFactory<SerieTableRow, String>(properties[i]));;
            column.setPrefWidth(widths[i]);
            this.seriesTable.getColumns().add(column);
        }
    }

    /**
     * Método público para recibir el listado de series obtenido de la consulta realizada por la ventana principal y llenar la tabla con el resultado
     * */
    public void fillTable(List<Serie> series) {
        this.seriesTable.getItems().addAll(parseSeriesList(series));
    }

    /**
     * Método auxiliar para mapear la lista de series al modelo de datos de la tabla
     *
     * @return lista con los datos necesarios para llenar la tabla, mapeados al modelo de datos SerieTableRow
     * */
    private List<SerieTableRow> parseSeriesList(List<Serie> series) {
        List<SerieTableRow> rows = new ArrayList<>();
        for (Serie serie : series) {
            SerieTableRow row = new SerieTableRow(serie);
            rows.add(row);
        }
        return rows;
    }

    /**
     * Clase interna que representa el modelo de datos utilizado por la tabla para representar su contenido.
     * Cada instancia de la clase representa una fila de la tabla.
     * */
    public static class SerieTableRow {
        private String title;
        private String seriesRuntime;
        private String certificate;
        private String episodesRuntime;
        private String imdbRating;
        private String overview;
        private String star1;
        private String star2;
        private String star3;
        private String star4;
        private String votes;

        public SerieTableRow(Serie serie) {
            this.title = serie.getTitle();
            this.seriesRuntime = serie.getSeriesRuntime();
            this.certificate = serie.getCertificate();
            this.episodesRuntime = serie.getEpisodesRuntime();
            this.imdbRating = String.valueOf(serie.getImdbRating());
            this.overview = serie.getOverview();
            this.star1 = serie.getStar1();
            this.star2 = serie.getStar2();
            this.star3 = serie.getStar3();
            this.star4 = serie.getStar4();
            this.votes = String.valueOf(serie.getVotes());
        }

        public String getTitle() {
            return title;
        }

        public String getSeriesRuntime() {
            return seriesRuntime;
        }

        public String getCertificate() {
            return certificate;
        }

        public String getEpisodesRuntime() {
            return episodesRuntime;
        }

        public String getImdbRating() {
            return imdbRating;
        }

        public String getOverview() {
            return overview;
        }

        public String getStar1() {
            return star1;
        }

        public String getStar2() {
            return star2;
        }

        public String getStar3() {
            return star3;
        }

        public String getStar4() {
            return star4;
        }

        public String getVotes() {
            return votes;
        }
    }
}
