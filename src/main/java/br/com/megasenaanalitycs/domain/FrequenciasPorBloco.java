package br.com.megasenaanalitycs.domain;

import java.util.*;

public class FrequenciasPorBloco {
    public final int[] frequencias;
    public final List<Integer>[] posicoesNoBloco;
    public final int indiceInicial;
    public final int indiceFinal;

    public FrequenciasPorBloco(int indiceInicial, int indiceFinal, TipoJogo tipoJogo) {
        this.indiceInicial = indiceInicial;
        this.indiceFinal = indiceFinal;
        frequencias = new int[tipoJogo.maiorDezena];
        posicoesNoBloco = new List[tipoJogo.maiorDezena];
    }

    public void adicionarFrequencia(int index, int posicaoNoBloco) {
        frequencias[index]++;
        if (posicoesNoBloco[index] == null) {
            posicoesNoBloco[index] = new ArrayList<>();
        }
        posicoesNoBloco[index].add(posicaoNoBloco);
    }
}