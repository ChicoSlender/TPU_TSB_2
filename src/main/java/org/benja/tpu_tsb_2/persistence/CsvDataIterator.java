package org.benja.tpu_tsb_2.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Clase utilizada para cargar datos desde el archivo csv, fila por fila.
 * Está implementada como un iterator simple, con los métodos hasNext() y next()
 * para recorrer fila por fila el archivo en un ciclo while.
 * */
public class CsvDataIterator implements Iterator<String[]> {
    private File csvFile;
    private Scanner fileScanner;

    public CsvDataIterator() {
        URL fileUrl = CsvDataIterator.class.getResource("series_data_clean.csv");

        if (fileUrl == null) {
            throw new NullPointerException("File url is null");
        }

        try {
            this.csvFile = new File(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error leyendo el path del archivo");
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
    @Override
    public boolean hasNext() {
        return this.fileScanner.hasNextLine();
    }

    /**
     * Extrae la siguiente fila del archivo csv y la devuelve como un array de strings
     * con un elemento por cada columna de la fila.
     *
     * @return array de celdas de la fila leida representadas como strings
     * @throws NullPointerException si no existe otra fila para ser leida
     * */
    @Override
    public String[] next() throws NullPointerException {
        if (!hasNext()) {
            throw new NullPointerException("El archivo no tiene otra fila para leer");
        }

        return this.fileScanner.nextLine().split(",");
    }

}
