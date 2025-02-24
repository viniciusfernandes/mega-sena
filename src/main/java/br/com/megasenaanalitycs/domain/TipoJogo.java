package br.com.megasenaanalitycs.domain;

public enum TipoJogo {
    MEGASENA(6, 60, 3, 3), LOTOMANIA(20, 100, 3, 3);
    public final int quantidadeNumeros;
    public final int maiorDezena;
    public final int ocorrenciaMaxima;
    public final int frequenciaMaxima;

    TipoJogo(int numeros, int maiorDezena, int ocorrenciaMaxima, int frequenciaMaxima) {
        this.quantidadeNumeros = numeros;
        this.maiorDezena = maiorDezena;
        this.ocorrenciaMaxima = ocorrenciaMaxima;
        this.frequenciaMaxima = frequenciaMaxima;
    }
}
