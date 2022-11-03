package org.benja.tpu_tsb_2.business;

import org.benja.tpu_tsb_2.persistence.CsvDataIterator;
import org.benja.tpu_tsb_2.support.Serie;
import org.benja.tpu_tsb_2.support.TSBHashTableDA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Clase controladora que encapsula la lógica de llenar los mapas con los datos provenientes del archivo csv y
 * la de consultar información a partir de un género y dimensión seleccionadas.
 * Contiene un mapa por cada dimensión de datos disponible para mostrar.
 * */
public class SeriesIndexController {
    private Iterator<String[]> dataIterator;
    //Mapa que almacena contadores de series por género, con el género como clave
    private TSBHashTableDA<String, Integer> quantityPerGender;
    //Mapa que almacena listas de series por género, con el género como clave
    private TSBHashTableDA<String, List<Serie>> seriesPerGender;
    //Mapa que almacena contadores de series por rating por género, con el género como clave y el rating-1 como índice del array de contadores
    private TSBHashTableDA<String, Integer[]> quantityPerScorePerGender;

    public SeriesIndexController() {
        this.dataIterator = new CsvDataIterator();
        this.quantityPerGender = new TSBHashTableDA<>();
        this.quantityPerScorePerGender = new TSBHashTableDA<>();
        this.seriesPerGender = new TSBHashTableDA<>();
    }

    /**
     * Obtiene a partir de los mapas un array con strings correspondientes a todos los géneros de series disponibles
     *
     * @return array de géneros en formato string
     * */
    public String[] getAvailableGenders() {
        return quantityPerGender.keySet().toArray(new String[0]);
    }

    public int getSeriesCountForGenre(String genre) {
        int count = 0;
        if (this.quantityPerGender.containsKey(genre)) {
            count = this.quantityPerGender.get(genre);
        }

        return count;
    }

    public List<Serie> getSeriesDetailsForGenre(String genre) {
        List<Serie> list = new ArrayList<>();

        if (this.seriesPerGender.containsKey(genre)) {
            list = this.seriesPerGender.get(genre);
        }

        return list;
    }

    public Integer[] getSeriesCountPerRatingForGenre(String genre) {
        Integer[] counters = new Integer[10];
        Arrays.fill(counters, 0);

        if (this.quantityPerScorePerGender.containsKey(genre)) {
            counters = this.quantityPerScorePerGender.get(genre);
        }

        return counters;
    }

    /**
     * Inicializa los mapas con los datos provenientes del archivo csv
     * */
    public void proccessDataFile() {
        this.dataIterator.next(); //Omite la fila que contiene las cabeceras de columna del archivo csv

        while (this.dataIterator.hasNext()) {
            String[] dataRow = dataIterator.next();
            String[] genres = getGenresFromDataRow(dataRow);

            countGenresInRow(genres);
            addSeriesToListsOfSeriesPerGender(dataRow, genres);
            countGenresPerScoreInRow(dataRow, genres);
        }
    }

    //Métodos privados de la clase

    /**
     * Método auxiliar que extrae el listado de géneros al que pertenece la serie representada en la fila de datos
     *
     * @return array de géneros incluidos en la fila en formato string
     * */
    private String[] getGenresFromDataRow(String[] dataRow) {
        return dataRow[4].split("\\|");
    }

    /**
     * Método auxiliar que aumenta los contadores de series por genero contenidos en el mapa quantityPerGender a partir de un array de géneros en formato string.
     * En caso de no existir entrada en el mapa para alguno de los géneros recibidos, se crea una con el contador puesto en 1
     *
     * @param genres array de géneros contenidos en la fila en formato string
     * */
    private void countGenresInRow(String[] genres) {
        for (String genre : genres) {
            Integer countForGenre = this.quantityPerGender.get(genre);
            if (countForGenre == null) {
                countForGenre = 0;
            }
            countForGenre++;

            this.quantityPerGender.put(genre, countForGenre);
        }
    }

    /**
     * Método auxiliar que mapea los datos de una fila a un objeto Serie y lo agrega a la lista correspondiente a cada uno de los géneros
     * contenidos en el array pasado por parámetro, contenidas dentro del mapa seriesPerGender.
     * Si no existe en el mapa una lista correspondiente para alguno de los géneros, la lista es creada y agregada al mapa
     *
     * @param dataRow array que representa una fila de datos del archivo csv
     * @param genres array de géneros contenidos en la fila en formato string
     * */
    private void addSeriesToListsOfSeriesPerGender(String[] dataRow, String[] genres) {
        Serie serie = new Serie(dataRow);
        for (String genre : genres) {
            if (!this.seriesPerGender.containsKey(genre)) {
                this.seriesPerGender.put(genre, new ArrayList<Serie>());
            }

            List<Serie> seriesPerGenderList = this.seriesPerGender.get(genre);
            seriesPerGenderList.add(serie);
        }
    }

    /**
     * Método auxiliar que extrae la parte entera de la puntuación contenida en la fila de datos y aumenta los contadores correspondientes
     * a cada uno de los géneros contenidos en el array pasado como parámetro.
     * Si no existen contadores para alguno de los género, un array de 10 contadores inicializados en 0 es creado e insertado en el mapa para
     * luego aumentar en 1 el correspondiente al puntaje de la fila.
     *
     * @param dataRow array que representa una fila de datos del archivo csv
     * @param genres array de géneros contenidos en la fila en formato string
     * */
    private void countGenresPerScoreInRow(String[] dataRow, String[] genres) {
        float rating = Float.parseFloat(dataRow[5]);
        int integerRating = (int) rating;

        for (String genre : genres) {
            Integer[] counterArrayForGenre = this.quantityPerScorePerGender.get(genre);
            if (counterArrayForGenre == null) {
                counterArrayForGenre = new Integer[10];
                Arrays.fill(counterArrayForGenre, 0);
                this.quantityPerScorePerGender.put(genre, counterArrayForGenre);
            }

            counterArrayForGenre[integerRating-1]++;
        }
    }
}
