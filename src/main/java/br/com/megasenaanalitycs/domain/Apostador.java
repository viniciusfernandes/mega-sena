package br.com.megasenaanalitycs.domain;

import java.util.*;

public class Apostador {
    public final String nome;
    public final List<int[]> apostas = new ArrayList<>();
    public final List<List<Integer>> acertos = new ArrayList<>();

    public Apostador(String nome) {
        this.nome = nome;
    }

    public void addAposta(int[] aposta) {
        apostas.add(aposta);
    }

    public ApostaEAcerto verificarApostas(int[] sorteados) {
        List<Integer> melhorAcerto = null;
        int[] melhorAposta = null;
        for (var aposta : apostas) {
            var acerto = new ArrayList<Integer>();

            for (int j = 0; j < aposta.length; j++) {
                for (int i = 0; i < sorteados.length; i++) {
                    if (aposta[j] == sorteados[i]) {
                        acerto.add(sorteados[i]);
                        break;
                    }
                }
            }
            if (melhorAcerto == null || melhorAcerto.size() < acerto.size()) {
                melhorAcerto = acerto;
                melhorAposta = aposta;
            }
            acertos.add(acerto);
        }
        return new ApostaEAcerto(melhorAposta, melhorAcerto);
    }

    public List<ApostaEAcerto> getApostasEAcertos() {
        var apostasEacertos = new ArrayList<ApostaEAcerto>();
        for (int i = 0; i < apostas.size(); i++) {
            apostasEacertos.add(new ApostaEAcerto(apostas.get(i), acertos.get(i)));
        }
        return apostasEacertos;
    }
}
