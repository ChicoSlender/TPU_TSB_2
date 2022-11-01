module org.benja.tpu_tsb_2 {
    requires javafx.controls;
    requires javafx.fxml;


    exports org.benja.tpu_tsb_2.ui;
    opens org.benja.tpu_tsb_2.ui to javafx.fxml;
}