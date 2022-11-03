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

public class SeriesRatingListController implements Initializable {

    @FXML
    private TableView<SeriesRatingTableRow> seriesRatingTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initTable();
    }

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

    public void fillTable(Integer[] countPerRating) {
        this.seriesRatingTable.getItems().addAll(parseSeriesList(countPerRating));
    }

    private List<SeriesRatingTableRow> parseSeriesList(Integer[] countPerRating) {
        List<SeriesRatingTableRow> rows = new ArrayList<>();
        for (int i = 0; i < countPerRating.length; i++) {
            SeriesRatingTableRow row = new SeriesRatingTableRow(i+1, countPerRating[i]);
            rows.add(row);
        }
        return rows;
    }

    public class SeriesRatingTableRow {
        int rating;
        int count;


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
