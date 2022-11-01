module org.benja.tpu_tsb_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.benja.tpu_tsb_2 to javafx.fxml;
    exports org.benja.tpu_tsb_2;
}