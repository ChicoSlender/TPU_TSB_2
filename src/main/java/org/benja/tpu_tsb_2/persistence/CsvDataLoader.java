package org.benja.tpu_tsb_2.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Clase utilizada para cargar datos desde el archivo csv, fila por fila.
 * Está implementada como un pseudo iterator simple, con los métodos hasNextRow() y nextRow()
 * para recorrer fila por fila el archivo en un ciclo while.
 * */
public class CsvDataLoader {
    private File csvFile;
    private Scanner fileScanner;

    public CsvDataLoader() {
        URL fileUrl = CsvDataLoader.class.getResource("series_data_clean.csv");

        if (fileUrl == null) {
            throw new NullPointerException("File url is null");
        }

        try {
            this.csvFile = new File(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error leyendo el path del archivo");
        }

        if (this.csvFile == null) {
            throw new NullPointerException("CSV file is null");
        }

        try {
            this.fileScanner = new Scanner(this.csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Archivo no encontrado");
        }
    }
    /**
     * Consulta si existe otra fila dentro del csv para ser leida
     *
     * @return si existe otra fila para ser leida
     * */
    public boolean hasNextRow() {
        return this.fileScanner.hasNextLine();
    }

    /**
     * Extrae la siguiente fila del archivo csv y la devuelve como un array de strings
     * con un elemento por cada columna de la fila.
     *
     * @return array de celdas de la fila leida representadas como strings
     * @throws NullPointerException si no existe otra fila para ser leida
     * */
    public String[] nextRow() throws NullPointerException {
        if (!hasNextRow()) {
            throw new NullPointerException("El archivo no tiene otra fila para leer");
        }

        return this.fileScanner.nextLine().split(",");
    }
}
