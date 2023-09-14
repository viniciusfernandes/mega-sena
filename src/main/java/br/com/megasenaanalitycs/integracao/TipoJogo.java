package br.com.megasenaanalitycs.integracao;

public enum TipoJogo {
    MEGASENA(6, 60), LOTOMANIA(20, 100);
    public final int numeros;
    public final int total;

    TipoJogo(int numeros, int total) {
        this.numeros = numeros;
        this.total = total;
    }
}