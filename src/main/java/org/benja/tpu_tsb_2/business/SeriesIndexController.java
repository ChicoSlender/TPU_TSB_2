package org.benja.tpu_tsb_2.business;

import org.benja.tpu_tsb_2.persistence.CsvDataLoader;

public class SeriesIndexController {
    CsvDataLoader dataLoader;

    public SeriesIndexController() {
        this.dataLoader = new CsvDataLoader();
    }
}
