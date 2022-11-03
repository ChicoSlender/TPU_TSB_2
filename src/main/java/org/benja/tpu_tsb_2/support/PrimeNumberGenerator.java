package org.benja.tpu_tsb_2.support;

/**
 * Clase auxiliar utilizada por TSBHashTable para el manejo de números primos.
 * Legada de la entrega anterior del TPU
 * */
public class PrimeNumberGenerator {
    public int nextPrime(int n) {
        int prime = n;
        if (prime % 2 == 0) prime++;

        while (!this.isPrime(prime)) {
            prime += 2;
        }

        return prime;
    }

    private boolean isPrime(int n) {
        int sqrt = (int) Math.floor(Math.sqrt(n));

        for (int i = 2; i <= sqrt; i++) {
            if (n % i == 0) return false;
        }

        return true;
    }
}
