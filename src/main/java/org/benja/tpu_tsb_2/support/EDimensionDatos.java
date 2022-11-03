package org.benja.tpu_tsb_2.support;

/**
 * Enum que representa las distintas "dimensiones" o "tipos" de dato a visualizar en la aplicación, seleccionables en la consulta.
 */
public enum EDimensionDatos {
    CANTIDAD_SERIES("Cantidad de series"),
    LISTADO_SERIES("Detalle de series"),
    CANTIDAD_SERIES_PUNTUACION("Cantidad por puntuación");

    //Atributo que representa la cadena de texto visualizada por pantalla para cada valor posible del enum.
    private String displayString;

    EDimensionDatos(String displayString) {
        this.displayString = displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
