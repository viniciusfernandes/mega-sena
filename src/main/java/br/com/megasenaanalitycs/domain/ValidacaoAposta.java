package br.com.megasenaanalitycs.domain;

public class ValidacaoAposta {
    public final String nome;
    public final int[] aposta;
    public final TipoAposta tipoAposta;
    public final int  numeroAposta;
    public final int[]  frequencia;

    public ValidacaoAposta(String nome, int[] aposta, TipoAposta tipoAposta, int numeroAposta, int[] frequencia) {
        this.nome = nome;
        this.aposta = aposta;
        this.tipoAposta = tipoAposta;
        this.numeroAposta = numeroAposta;
        this.frequencia = frequencia;
    }
}
