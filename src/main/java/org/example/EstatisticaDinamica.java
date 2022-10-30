package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstatisticaDinamica {
    public static int[] gerarUltimoBlocoFrequenciaSorteios(List<int[]> sorteios, int tamanhoBloco) {
        var frequencias = gerarFrequenciaSorteios(sorteios, tamanhoBloco);
        return frequencias.get(frequencias.size() - 1);
    }


    public static int[] numeroComFrequenciaBaixa(int[] frequencias, int frequenciaLimite) {
        var numeros = new ArrayList<Integer>();
        var saida = new StringBuilder();
        for (int i = 0; i < frequencias.length; i++) {
            if (frequencias[i] <= frequenciaLimite) {
                numeros.add(i + 1);
                saida.append((i + 1) + "=" + frequencias[i]).append("\n");
            }
        }
        Utils.print("Numeros Baixa Frequencia", saida);
        return null;
    }

    public static List<int[]> gerarFrequenciaSorteios(List<int[]> sorteios, int tamanhoBloco) {
        if (tamanhoBloco > sorteios.size()) {
            tamanhoBloco = sorteios.size();
        }
        int lastIdx = sorteios.size() - tamanhoBloco;
        List<int[]> blocoSorteios = null;
        List<int[]> frequencias = new ArrayList<>();
        int[] frequencia = null;
        int idx;
        StringBuilder saida = new StringBuilder();
        for (int idxBloco = 0; idxBloco <= lastIdx; idxBloco++) {
            blocoSorteios = sorteios.subList(idxBloco, idxBloco + tamanhoBloco);
            frequencia = new int[60];

            for (int[] jogo : blocoSorteios) {
                for (int i = 0; i < jogo.length; i++) {
                    idx = jogo[i] - 1;
                    frequencia[idx]++;
                }
            }
            frequencias.add(frequencia);
            saida.append("(").append(idxBloco + 1).append(") ");
            Utils.stringfy(frequencia, saida);
            saida.append("\n");
        }
        // Utils.print("Frequencia dos Sorteios", saida);
        return frequencias;
    }

    public static void printFrequenciaNumerosSorteados(List<int[]> sorteios, List<int[]> blocosFrequencias, int tamanhoBloco) {
        int[] frequencias;
        int[] numerosSorteados;
        int idxNumero = 0;
        StringBuilder saida = new StringBuilder();
        int idxPenultimoBloco = blocosFrequencias.size() - 2;
        for (int idxBloco = 0, idxSort = tamanhoBloco; idxBloco <= idxPenultimoBloco; idxBloco++, idxSort++) {
            frequencias = blocosFrequencias.get(idxBloco);
            numerosSorteados = sorteios.get(idxSort);
            saida.append("(").append(idxSort + 1).append(") ");
            for (int i = 0; i < numerosSorteados.length; i++) {
                idxNumero = numerosSorteados[i] - 1;
                if (numerosSorteados[i] <= 9) {
                    saida.append("0");
                }
                saida.append(numerosSorteados[i]).append("=").append(frequencias[idxNumero]).append(" ");
            }
            saida.append("\n");
            int[] distribuicao = new int[20];
            for (int i = 0; i < frequencias.length; i++) {
                if (i < 9) {
                    saida.append("0");
                }
                saida.append(i + 1).append("=").append(frequencias[i]).append(" ");
                distribuicao[frequencias[i]]++;
            }
            saida.append("\n");
            for (int i = 0; i < distribuicao.length; i++) {
                saida.append(i).append("=").append(distribuicao[i]).append(" ");
            }
            saida.append("\n\n");
        }
        System.out.println(saida);
    }

    public static void gerarVelocidadesDistribuicoes(List<int[]> distribuicoes) {
        int[] distribuicaoAnterior = distribuicoes.get(0);
        int[] distribuicao;
        int[][] velocidades = new int[60][distribuicoes.size()];
        int velocidade;
        for (int idxDist = 1; idxDist < distribuicoes.size(); idxDist++) {
            distribuicao = distribuicoes.get(idxDist);
            for (int idxNumero = 0; idxNumero < distribuicao.length; idxNumero++) {
                velocidade = distribuicao[idxNumero] - distribuicaoAnterior[idxNumero];
                if (velocidade < 0) {
                    velocidade = 0;
                }
                velocidades[idxNumero][idxDist] = velocidade;
            }
            distribuicaoAnterior = distribuicao;
        }
        for (int idxVel = 0; idxVel < velocidades.length; idxVel++) {
            System.out.println("vel " + (idxVel + 1) + " => " + Utils.stringfy(velocidades[idxVel]));
        }
    }


}
