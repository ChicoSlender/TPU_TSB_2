package org.benja.tpu_tsb_2.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller para la ventana que contiene la tabla con la cantidad de series agrupadas por puntaje para un género seleccionado.
 * */
public class SeriesRatingListController implements Initializable {

    @FXML
    private TableView<SeriesRatingTableRow> seriesRatingTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initTable();
    }

    /**
     * Método auxiliar para construir y agregar las columnas de la tabla de resultados al momento de la inicialización.
     * */
    private void initTable() {
        String[] titles = new String[] {"Puntaje", "Cantidad"};
        String[] properties = new String[] {"rating", "count"};
        double[] widths = new double[] {200, 200};

        for (int i = 0; i < titles.length; i++) {
            TableColumn<SeriesRatingTableRow, String> column = new TableColumn<>(titles[i]);
            column.setCellValueFactory(new PropertyValueFactory<SeriesRatingTableRow, String>(properties[i]));;
            column.setPrefWidth(widths[i]);
            this.seriesRatingTable.getColumns().add(column);
        }
    }

    /**
     * Método público para recibir los datos de la consulta realizada por la ventana principal y llenar la tabla con el resultado
     * */
    public void fillTable(Integer[] countPerRating) {
        this.seriesRatingTable.getItems().addAll(parseCountArray(countPerRating));
    }

    /**
     * Método auxiliar para mapear el array de enteros al modelo de datos de la tabla
     *
     * @return lista con los datos necesarios para llenar la tabla, mapeados al modelo de datos SeriesRatingTableRow
     * */
    private List<SeriesRatingTableRow> parseCountArray(Integer[] countPerRating) {
        List<SeriesRatingTableRow> rows = new ArrayList<>();
        for (int i = 0; i < countPerRating.length; i++) {
            SeriesRatingTableRow row = new SeriesRatingTableRow(i+1, countPerRating[i]);
            rows.add(row);
        }
        return rows;
    }

    /**
     * Clase interna que representa el modelo de datos utilizado por la tabla para representar su contenido.
     * Cada instancia de la clase representa una fila de la tabla.
     * */
    public static class SeriesRatingTableRow {
        private int rating;
        private int count;


        public SeriesRatingTableRow(int rating, int count) {
            this.rating = rating;
            this.count = count;
        }

        public int getRating() {
            return rating;
        }

        public int getCount() {
            return count;
        }
    }
}
