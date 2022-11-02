package org.benja.tpu_tsb_2.support;

public enum EDimensionDatos {
    CANTIDAD_SERIES("Cantidad de series"),
    LISTADO_SERIES("Detalle de series"),
    CANTIDAD_SERIES_PUNTUACION("Cantidad por puntuación");

    private String displayString;

    EDimensionDatos(String displayString) {
        this.displayString = displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
