package br.com.megasenaanalitycs.integracao;

public enum TipoJogo {
    MEGASENA(6), LOTOMANIA(20);
    public final int numeros;

    TipoJogo(int numeros) {
        this.numeros = numeros;
    }
}