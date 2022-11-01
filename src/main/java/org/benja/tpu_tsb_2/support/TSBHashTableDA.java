package org.benja.tpu_tsb_2.support;

import java.io.Serializable;
import java.util.*;

/**
 * Clase que emula la funcionalidad de la clase nativa java.util.Hashtable provista por Java.
 * TSBHashTableDA utiliza un arreglo de objetos de una clase interna Entry que implementa la interfaz interna homologa de Map
 *
 * La clase no admite repeticion de claves (al insertar una clave repetida se sobreescribe el valor insertado previamente emparejado con la misma clave),
 * ni la inserción de pares con claves o valores nulos.
 *
 * A diferencia de la clase provista por la cátedra esta implementa un método de direccionamiento abierto para la resolución de colisiones
 *
 * @author Grupo 20
 * @version Octubre de 2022
 * @param <K> La clase de los objetos que serán usados como clave en la tabla.
 * @param <V> La clase de los objetos que serán los valores almacenados en la tabla. */

public class TSBHashTableDA<K, V> implements Map<K, V>, Cloneable, Serializable {
    //************************ Constantes (privadas o públicas).

    // El tamaño máximo que podrá tener el arreglo interno de soporte
    // Se utiliza el numero dado ya que se trata del numero primo más cercano al valor devuelto por Integer.MAX_VALUE
    private final static int MAX_SIZE = 2147483587;


    //************************ Atributos privados (estructurales).

    // la tabla hash: el arreglo que contiene los pares clave-valor (objetos de la clase Entry)
    private Map.Entry<K, V>[] table;

    // el tamaño inicial de la tabla (tamaño con el que fue creada)
    private int initialCapacity;

    // la cantidad de objetos que contiene la tabla
    private int count;

    // el factor de carga utilizado para determinar si hace falta un rehashing de la tabla
    private float loadFactor;

    // Generador auxiliar de numeros primos
    private PrimeNumberGenerator primeGenerator;

    //************************ Atributos privados (para gestionar las vistas).

    /*
     * Cada uno de estos campos se inicializa para contener una instancia de la
     * vista que sea más apropiada, la primera vez que esa vista es requerida.
     * La vista son objetos stateless (no se requiere que almacenen datos, sino
     * que sólo soportan operaciones), y por lo tanto no es necesario crear más
     * de una de cada una.
     */
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;


    //************************ Atributos protegidos (control de iteración).

    // conteo de operaciones de cambio de tamaño (utilizado para fail-fast iterator).
    protected transient int modCount;

    //************************ Constructores.

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 11 y con factor
     * de carga igual a 0.5f.
     */
    public TSBHashTableDA()
    {
        this(11, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con factor
     * de carga igual a 0.5f.
     * @param initialCapacity la capacidad inicial de la tabla.
     */
    public TSBHashTableDA(int initialCapacity)
    {
        this(initialCapacity, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor
     * de carga indicado. Si la capacidad inicial indicada por initial_capacity
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el factor de
     * carga indicado es negativo o cero, se ajustará a 0.5f.
     * @param initialCapacity la capacidad inicial de la tabla.
     * @param loadFactor el factor de carga de la tabla.
     */
    public TSBHashTableDA(int initialCapacity, float loadFactor)
    {
        if(loadFactor <= 0) { loadFactor = 0.5f; }
        if(initialCapacity <= 0) { initialCapacity = 11; }
        else
        {
            if(initialCapacity > TSBHashTableDA.MAX_SIZE)
            {
                initialCapacity = TSBHashTableDA.MAX_SIZE;
            }
        }

        this.table = new Entry[initialCapacity];

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.count = 0;
        this.modCount = 0;

        this.primeGenerator = new PrimeNumberGenerator();
    }

    /**
     * Crea una tabla a partir del contenido del Map especificado.
     * @param t el Map a partir del cual se creará la tabla.
     */
    public TSBHashTableDA(Map<? extends K,? extends V> t)
    {
        this(11, 0.5f);
        this.putAll(t);
    }

    //************************ Implementación de métodos especificados por la interfaz Map.

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.contains(value);
    }

    @Override
    public V get(Object key) {
        if(key == null) {
            throw new NullPointerException("TSBHashTableDA.get(): parámetro null");
        }

        int indexOfKey = this.searchForIndexOfKey((K) key);

        V value = null;
        if (indexOfKey >= 0) {
            Map.Entry<K, V> entry = this.table[indexOfKey];
            if (entry != null && !((Entry) entry).isDeleted()) {
                value = entry.getValue();
            }
        }

        return value;
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("TSBHashTableDA.put(): ninguno de los parámetros puede ser null");
        }

        int indexOfKey = this.searchForIndexOfKey(key);

        if (indexOfKey < 0) {
            throw new IndexOutOfBoundsException("TSBHashTableDA.put(): no se encontró un lugar donde ubicar el par clave-valor");
        }

        V oldValue = null;
        Map.Entry<K, V> entry = this.table[indexOfKey];
        if (entry != null && !((Entry) entry).isDeleted()) {
            oldValue = entry.getValue();
            entry.setValue(value);
        }
        else {
            this.table[indexOfKey] = new Entry<>(key, value);
            this.count++;
            this.modCount++;
            if (this.isTableOverloaded()) {
                this.rehash();
            }
        }

        return oldValue;
    }

    @Override
    public V remove(Object key) {
        if(key == null) {
            throw new NullPointerException("TSBHashTableDA.get(): parámetro null");
        }

        int indexOfKey = this.searchForIndexOfKey((K) key);

        V value = null;
        if (indexOfKey >= 0) {
            Map.Entry<K, V> entry = this.table[indexOfKey];
            if (entry != null && !((Entry<K, V>) entry).isDeleted()) {
                value = entry.getValue();
                ((Entry<K, V>) entry).delete();
                this.modCount++;
                this.count--;
            }
        }

        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
        {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        this.table = new Entry[this.initialCapacity];
        this.count = 0;
        this.modCount++;
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null)  {
            this.keySet = new KeySet();
        }

        return this.keySet;
    }

    @Override
    public Collection<V> values() {
        if (this.values == null)  {
            this.values = new ValueCollection();
        }

        return this.values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null)  {
            this.entrySet = new EntrySet();
        }

        return this.entrySet;
    }

    //************************ Métodos redefinidos heredados de Object

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        Set<Map.Entry<K, V>> entries = this.entrySet();

        string.append("{\n");
        for (Map.Entry<K, V> entry : entries) {
            string.append(entry.toString());
            string.append(",\n");
        }
        string.append("}");

        return string.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        TSBHashTableDA<K, V> clonedHashTable = (TSBHashTableDA<K, V>) super.clone();
        clonedHashTable.primeGenerator = new PrimeNumberGenerator();
        clonedHashTable.entrySet = null;
        clonedHashTable.keySet = null;
        clonedHashTable.values = null;
        clonedHashTable.count = 0;
        clonedHashTable.table = new Entry[this.table.length];
        clonedHashTable.putAll(this);
        clonedHashTable.modCount = 0;

        return clonedHashTable;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Map)) { return false; }

        Map<K, V> t = (Map<K, V>) obj;
        if(t.size() != this.size()) { return false; }

        try
        {
            Set<Map.Entry<K,V>> entries = this.entrySet();
            for (Map.Entry<K, V> entry : entries) {
                K key = entry.getKey();
                V value = entry.getValue();
                if(t.get(key) == null) {
                    return false;
                }
                else if(!value.equals(t.get(key)))
                {
                    return false;
                }
            }
        }
        catch (ClassCastException | NullPointerException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Retorna un hash code para la tabla completa.
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode() {
        int[] weights = new int[] {1, 2, 3, 5, 7, 11};

        int hash = 0;
        int i = 0;
        Set<Map.Entry<K,V>> entries = this.entrySet();
        for (Map.Entry<K, V> entry : entries) {
            hash += weights[i % weights.length] * entry.hashCode() / this.count;
            i++;
        }

        return hash;
    }
    //************************ Metodos especificos a la implementación de la clase

    /**
     * Función hash
     * @param k clave entera
     * @return indice válido para la clave k para poder acceder a la tabla
     */
    private int hash(int k)
    {
        return hash(k, this.table.length);
    }

    /**
     * Función hash
     * @param key objeto que representa una clave valida para la tabla
     * @return indice válido para la clave k para poder acceder a la tabla
     */
    private int hash(K key)
    {
        return hash(key.hashCode(), this.table.length);
    }

    /**
     * Función hash
     * @param key objeto que representa una clave valida para la tabla
     * @param t tamaño de tabla
     * @return indice válido para la clave k para poder acceder a una tabla del tamaño especificado
     */
    private int hash(K key, int t)
    {
        return hash(key.hashCode(), t);
    }

    /**
     * Función hash
     * @param k clave entera
     * @param t tamaño de tabla
     * @return indice válido para la clave k para poder acceder a una tabla del tamaño especificado
     */
    private int hash(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;
    }

    /**
     * Busca en el arreglo mediante exploración cuadrática el indice donde se encuentra ubicado el objeto con la clave dada o el primer lugar disponible para su insercion
     * El elemento del arreglo estará disponible cuando sea nulo (nunca fue ocupado) o cuando esté marcado como "tumba" (entrada eliminada).
     * La gestión de colisiones mediante direccionamiento abierto se encuentra implementada en este método
     * @param key clave cuyo indice debe buscarse
     * @return indice del par donde se encuentra la clave o un lugar disponible para su inserción.
     * */
    private int searchForIndexOfKey(K key) {
        int motherIndex = this.hash(key);

        int tombstoneIndex = -1;
        Map.Entry<K, V> currentEntry;
        for (int i = 0; i < this.table.length; i++) {
            int currentIndex = (motherIndex + (int)Math.pow(i, 2)) % this.table.length;
            currentEntry = this.table[currentIndex];

            if (currentEntry == null) {
                return tombstoneIndex < 0? currentIndex : tombstoneIndex;
            }

            if (tombstoneIndex < 0 && ((Entry) currentEntry).isDeleted()) {
                tombstoneIndex = currentIndex;
            }

            if (currentEntry.getKey().equals(key)) {
                return currentIndex;
            }
        }

        return tombstoneIndex;
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    public boolean contains(Object value)
    {
        Set<Map.Entry<K, V>> entries = this.entrySet();

        for (Map.Entry<K, V> entry : entries) {
            if (Objects.equals(value, entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido.
     * Se invoca automaticamente cuando se detecta que la proporción entre
     * elementos almacenados en la tabla y su tamaño supera el valor loadFactor.
     * También se asegura que el tamaño de la tabla siempre se trate de un numero
     * primo y que no supere el valor maximo dado por la constante MAX_SIZE
     */
    private void rehash() {
        int oldLength = this.table.length;
        int newLength = oldLength * 2 + 1;
        newLength = this.primeGenerator.nextPrime(newLength);
        newLength = Math.min(newLength, MAX_SIZE);

        Map.Entry<K, V>[] oldTable = this.table;
        this.table = new Entry[newLength];

        this.modCount++;
        this.count = 0;

        for (Map.Entry<K, V> entry : oldTable) {
            if (entry != null && !((Entry) entry).isDeleted()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Calcula la proporción entre la cantidad de elementos almacenados y el tamaño de la tabla
     * y comprueba si superó el factor de carga, necesitando de un rehashing si fuera el caso
     * @return si la tabla supera el factor de carga o no*/
    private boolean isTableOverloaded() {
        double length = this.table.length;
        return this.count / length >= 0.5;
    }

    //************************ Clases internas de soporte

    /**
     * Clase interna que representa los pares de objetos clave-valor a almacenar dentro de la tabla hash.
     * Instancias de esta clase son las almacenadas dentro del arreglo de soporte
     */
    private class Entry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;
        private boolean deleted;

        /**
         * Constructor de la clase
         * @throws IllegalArgumentException si se intenta inicializar con clave y/o valor nulos*/
        public Entry(K key, V value)
        {
            if(key == null || value == null)
            {
                throw new IllegalArgumentException("Entry(): no se acepta null como parámetros");
            }
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        /**
         * @throws IllegalArgumentException si se intenta setear un valor null*/
        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new IllegalArgumentException("Entry.setValue(): no se acepta null como parámetro");
            }

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            final Entry other = (Entry) obj;
            return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public String toString() {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }

        /**
         * Comprueba si el objeto fue marcado como "tumba" (borrado lógico)*/
        public boolean isDeleted() {
            return deleted;
        }

        /**
         * Marca al objeto como "tumba" (borrado lógico)*/
        public boolean delete() {
            this.deleted = true;
            return deleted;
        }
    }

    /**
     * Clase interna que representa una vista de todos los PARES mapeados en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public int size() {
            return TSBHashTableDA.this.count;
        }

        @Override
        public void clear() {
            TSBHashTableDA.this.clear();
        }

        /**
         * Comprueba si la vista (y por tanto la tabla hash) contiene el par especificado*/
        @Override
        public boolean contains(Object o) {
           if (o == null) return false;
           if (!(o instanceof Entry)) return false;

           Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
           K key = entry.getKey();
           int index = TSBHashTableDA.this.hash(key);

           Map.Entry<K, V> tableEntry = TSBHashTableDA.this.table[index];

           return Objects.equals(entry, tableEntry);
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("remove(): parámetro null");
            }
            if (!(o instanceof Entry)) return false;

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();

            return TSBHashTableDA.this.remove(key) != null;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
            private int currentEntry;
            private int lastEntry;
            private boolean nextOk;
            private int expectedModCount;

            private EntrySetIterator() {
                currentEntry = -1;
                lastEntry = -1;
                nextOk = false;
                expectedModCount = TSBHashTableDA.this.modCount;
            }


            @Override
            public boolean hasNext() {
                if (TSBHashTableDA.this.isEmpty()) return false;
                Map.Entry<K, V>[] table = TSBHashTableDA.this.table;
                if (currentEntry >= table.length) return false;

                int nextEntry = currentEntry + 1;
                while (nextEntry < table.length && (table[nextEntry] == null || ((Entry) table[nextEntry]).isDeleted())) {
                    nextEntry++;
                }

                return nextEntry < table.length;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (expectedModCount != TSBHashTableDA.this.modCount) {
                    throw new ConcurrentModificationException("EntrySetIterator.next(): modificación inesperada de tabla");
                }
                if (!this.hasNext()) {
                    throw new NoSuchElementException("EntrySetIterator.next(): no hay siguiente elemento");
                }

                Map.Entry<K, V>[] table = TSBHashTableDA.this.table;

                lastEntry = currentEntry;
                currentEntry++;
                while(table[currentEntry] == null || ((Entry) table[currentEntry]).isDeleted()) {
                    currentEntry++;
                }

                nextOk = true;
                return table[currentEntry];
            }

            @Override
            public void remove() {
                if (!nextOk) {
                    throw new IllegalStateException("EntrySetIterator.remove(): se debe invocar a next() antes de volver a invocar a remove()");
                }

                Map.Entry<K, V> removed = TSBHashTableDA.this.table[currentEntry];
                EntrySet.this.remove(removed);

                if(lastEntry != currentEntry)
                {
                    currentEntry = lastEntry;
                }

                nextOk = false;

                expectedModCount++;
            }
        }

    }

    /**
     * Clase interna que representa una vista de todas los Claves mapeadas en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class KeySet extends AbstractSet<K>
    {
        @Override
        public Iterator<K> iterator()
        {
            return new KeySetIterator();
        }

        @Override
        public int size()
        {
            return TSBHashTableDA.this.count;
        }

        @Override
        public boolean contains(Object o)
        {
            return TSBHashTableDA.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o)
        {
            return (TSBHashTableDA.this.remove(o) != null);
        }

        @Override
        public void clear()
        {
            TSBHashTableDA.this.clear();
        }

        private class KeySetIterator implements Iterator<K>
        {
            private int currentEntry;
            private int lastEntry;

            private boolean nextOk;

            private int expectedModCount;

            /**
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            public KeySetIterator()
            {
                lastEntry = -1;
                currentEntry = -1;
                nextOk = false;
                expectedModCount = TSBHashTableDA.this.modCount;
            }

            /**
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext()
            {
                if (TSBHashTableDA.this.isEmpty()) return false;
                Map.Entry<K, V>[] table = TSBHashTableDA.this.table;
                if (currentEntry >= table.length) return false;

                int nextEntry = currentEntry + 1;
                while (nextEntry < table.length && (table[nextEntry] == null || ((Entry) table[nextEntry]).isDeleted())) {
                    nextEntry++;
                }

                return nextEntry < table.length;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public K next()
            {
                if (expectedModCount != TSBHashTableDA.this.modCount) {
                    throw new ConcurrentModificationException("KeySetIterator.next(): modificación inesperada de tabla");
                }
                if (!this.hasNext()) {
                    throw new NoSuchElementException("KeySetIterator.next(): no hay siguiente elemento");
                }

                Map.Entry<K, V>[] table = TSBHashTableDA.this.table;

                lastEntry = currentEntry;
                currentEntry++;
                while(table[currentEntry] == null || ((Entry) table[currentEntry]).isDeleted()) {
                    currentEntry++;
                }

                nextOk = true;
                return table[currentEntry].getKey();
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove()
            {
                if (!nextOk) {
                    throw new IllegalStateException("KeySetIterator.remove(): se debe invocar a next() antes de volver a invocar a remove()");
                }

                Map.Entry<K, V> removed = TSBHashTableDA.this.table[currentEntry];
                KeySet.this.remove(removed.getKey());

                if(lastEntry != currentEntry)
                {
                    currentEntry = lastEntry;
                }

                nextOk = false;

                expectedModCount++;
            }
        }
    }

    /*
     * Clase interna que representa una vista de todos los VALORES mapeados en
     * la tabla: si la vista cambia, cambia también la tabla que le da respaldo,
     * y viceversa. La vista es stateless: no mantiene estado alguno (es decir,
     * no contiene datos ella misma, sino que accede y gestiona directamente los
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class ValueCollection extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator()
        {
            return new ValueCollectionIterator();
        }

        @Override
        public int size()
        {
            return TSBHashTableDA.this.count;
        }

        @Override
        public boolean contains(Object o)
        {
            return TSBHashTableDA.this.containsValue(o);
        }

        @Override
        public void clear()
        {
            TSBHashTableDA.this.clear();
        }

        private class ValueCollectionIterator implements Iterator<V>
        {

            private int currentEntry;
            private int lastEntry;
            private boolean nextOk;

            private int expectedModCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            public ValueCollectionIterator()
            {
                currentEntry = -1;
                lastEntry = -1;
                nextOk = false;
                expectedModCount = TSBHashTableDA.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext()
            {
                if (TSBHashTableDA.this.isEmpty()) return false;
                Map.Entry<K, V>[] table = TSBHashTableDA.this.table;
                if (currentEntry >= table.length) return false;

                int nextEntry = currentEntry + 1;
                while (nextEntry < table.length && (table[nextEntry] == null || ((Entry) table[nextEntry]).isDeleted())) {
                    nextEntry++;
                }

                return nextEntry < table.length;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public V next()
            {
                if (expectedModCount != TSBHashTableDA.this.modCount) {
                    throw new ConcurrentModificationException("ValueCollectionIterator.next(): modificación inesperada de tabla");
                }
                if (!this.hasNext()) {
                    throw new NoSuchElementException("ValueCollectionIterator.next(): no hay siguiente elemento");
                }

                Map.Entry<K, V>[] table = TSBHashTableDA.this.table;

                lastEntry = currentEntry;
                currentEntry++;
                while(table[currentEntry] == null || ((Entry) table[currentEntry]).isDeleted()) {
                    currentEntry++;
                }

                nextOk = true;
                return table[currentEntry].getValue();
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove()
            {
                if(!nextOk)
                {
                    throw new IllegalStateException("ValueCollectionIterator.remove(): debe invocar a next() antes de remove()...");
                }

                Map.Entry<K, V> removed = TSBHashTableDA.this.table[currentEntry];

                TSBHashTableDA.this.remove(removed.getKey());

                if(lastEntry != currentEntry)
                {
                    currentEntry = lastEntry;
                }

                nextOk = false;
                expectedModCount++;
            }
        }
    }
}
