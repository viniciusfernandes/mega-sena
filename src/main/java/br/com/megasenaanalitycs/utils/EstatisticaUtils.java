package br.com.megasenaanalitycs.utils;

import br.com.megasenaanalitycs.domain.FrequenciasPorBloco;

import java.util.List;

import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class EstatisticaUtils {
    public static void printPorFrequenciaMaxima(FrequenciasPorBloco frequenciasPorBloco, int frequenciaLimite) {
        var output = new StringBuilder();
        var header = new StringBuilder();
        header.append("Dezena Freq. Pos. Bloco\n");
        var frequencias = frequenciasPorBloco.frequencias;
        for (int i = 0; i < frequencias.length; i++) {
            var dezena = i + 1;
            if (frequencias[i] <= frequenciaLimite) {
                output
                        .append(dezena <= 9 ? "0" + (dezena) : dezena).append(" ")
                        .append("    ").append(frequencias[i])
                        .append("     ")
                        .append(frequenciasPorBloco.posicoesNoBloco[i]).append("\n");
            } else {
                throw new IllegalStateException();
            }
        }
        header.append(output);
        Utils.print("Frequência dos Números Sorteados", header);
    }


    public static void printNumeroPorFrequencia(FrequenciasPorBloco frequenciasPorBloco) {
        printPorFrequenciaMaxima(frequenciasPorBloco, 1000);
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
}
