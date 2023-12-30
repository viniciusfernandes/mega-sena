package br.com.megasenaanalitycs.utils;

import br.com.megasenaanalitycs.repository.TipoJogo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class EstatisticaUtils {

    public static List<int[]> gerarApostasAleatorias(int totalApostas) {
        var apostas = new ArrayList<int[]>();
        int[] aposta = null;
        boolean ok = false;
        int j = 0;
        for (int i = 0; i < totalApostas; i++) {
            aposta = new int[6];
            j = 0;
            do {
                aposta[j] = (int) Math.ceil(Math.random() * 61);
                j++;
                ok = j > 5;
            } while (!ok);
            Arrays.sort(aposta);
            apostas.add(aposta);
        }
        return apostas;
    }

    public static void printPorFrequenciaMaxima(int[] frequencias, int frequenciaLimite) {
        var freq = new StringBuilder();
        var num = new StringBuilder();
        for (int i = 0; i < frequencias.length; i++) {
            if (frequencias[i] <= frequenciaLimite) {
                num.append(i + 1 <= 9 ? "0" + (i + 1) : i + 1).append(" ");
                freq.append(frequencias[i] <= 9 ? "0" + frequencias[i] : frequencias[i]).append(" ");
            }
        }
        num.append("\n").append(freq).append("\n");
        Utils.print("Frequência dos Números Sorteados", num);
    }

    public static void printNumeroPorFrequencia(int[] frequencias) {
        printPorFrequenciaMaxima(frequencias, 1000);
    }


    public static void printFrequenciaPorApostas(List<int[]> apostas, int[] frequencia) {
        var ap = "";
        var freq = "";
        for (var aposta : apostas) {
            for (int i = 0; i < aposta.length; i++) {
                ap += stringfy(aposta[i]) + " ";
            }
            for (int i = 0; i < aposta.length; i++) {
                freq += stringfy(frequencia[aposta[i] - 1]) + " ";
            }
            System.out.println(ap + "\n" + freq + "\n");
            ap = "";
            freq = "";
        }
    }

    public static void printFrequenciaPorSorteio(List<int[]> sorteios, List<int[]> frequencias) {
        var num = new StringBuilder();
        var freq = new StringBuilder();
        var size = 40;
        sorteios = sorteios.subList(sorteios.size() - size, sorteios.size());
        frequencias = frequencias.subList(frequencias.size() - size, frequencias.size());
        int[] sorteio;
        int[] frequencia;
        int k;
        int i = size;
        while (--i > 0) {
            sorteio = sorteios.get(i);
            frequencia = frequencias.get(i - 1);
            for (int j = 0; j < sorteio.length; j++) {
                num.append(sorteio[j] <= 9 ? "0" + sorteio[j] : sorteio[j]).append(" ");
                k = sorteio[j] - 1;
                freq.append(frequencia[k] <= 9 ? "0" + frequencia[k] : frequencia[k]).append(" ");
            }
            freq.append("\n");
            num.append("\n").append(freq).append("\n");
            freq.delete(0, freq.length());
        }
        Utils.print("Frequência dos Últimos Sorteios", num);
    }

    public static void printFrequenciaSorteios(final List<int[]> frequencias) {
        var saida = new StringBuilder();
        for (int i = 0; i <= frequencias.size(); i++) {
            saida.append("(").append(i + 1).append(") ");
            saida.append(Utils.stringfy(frequencias.get(i)));
            saida.append("\n");
        }
        Utils.print("Frequencia dos Sorteios", saida);
    }


    public static void printFrequenciaNumerosSorteados(List<int[]> sorteios, List<int[]> blocosFrequencias, int tamanhoBloco) {
        int[] frequencias;
        int[] numerosSorteados;
        int idxNumero  ;
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

    public static void gerarVelocidadesDistribuicoes(TipoJogo tipoJogo, List<int[]> distribuicoes) {
        int[] distribuicaoAnterior = distribuicoes.get(0);
        int[] distribuicao;
        int[][] velocidades = new int[tipoJogo.total][distribuicoes.size()];
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
