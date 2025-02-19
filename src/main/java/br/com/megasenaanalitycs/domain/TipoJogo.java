package br.com.megasenaanalitycs.domain;

public enum TipoJogo {
    MEGASENA(6, 60, 3, 3), LOTOMANIA(20, 100, 3, 3);
    public final int numeros;
    public final int total;
    public final int ocorrenciaMaxima;
    public final int frequenciaMaxima;

    TipoJogo(int numeros, int total, int ocorrenciaMaxima, int frequenciaMaxima) {
        this.numeros = numeros;
        this.total = total;
        this.ocorrenciaMaxima = ocorrenciaMaxima;
        this.frequenciaMaxima = frequenciaMaxima;
    }
}
