package br.com.megasenaanalitycs.domain;

public enum TipoJogo {
    MEGASENA(6, 8, 60, 4, 3),
    LOTOMANIA(20, 20, 100, 16, 3);
    public final int quantidadeNumerosSorteados;
    public final int quantidadeNumerosApostados;
    public final int maiorDezena;
    public final int ocorrenciaMaxima;
    public final int frequenciaMaxima;

    TipoJogo(int numeros, int numerosApostados, int maiorDezena, int ocorrenciaMaxima, int frequenciaMaxima) {
        this.quantidadeNumerosSorteados = numeros;
        this.quantidadeNumerosApostados = numerosApostados;
        this.maiorDezena = maiorDezena;
        this.ocorrenciaMaxima = ocorrenciaMaxima;
        this.frequenciaMaxima = frequenciaMaxima;
    }
}
