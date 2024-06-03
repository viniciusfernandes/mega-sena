package br.com.megasenaanalitycs.domain;

import br.com.megasenaanalitycs.utils.Utils;

public class Aposta {
    public String apostador;
    public int[] numeros;
    public final int totalNumeros;

    public Aposta(int totalNumeros) {
        this.totalNumeros = totalNumeros;
    }

    public String toString() {
        return apostador + " apostou [" + Utils.stringfy(numeros) + "]";
    }
}
