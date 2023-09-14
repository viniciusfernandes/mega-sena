package br.com.megasenaanalitycs.utils;

import java.util.List;

public class Utils {
    private Utils() {
    }

    public static String stringfy(int[] valores) {
        StringBuilder saida = new StringBuilder();
        stringfy(valores, saida);
        return saida.toString();
    }

    public static void stringfy(int[] valores, StringBuilder saida) {
        int last = valores.length - 1;
        for (int i = 0; i < valores.length; i++) {
            if (valores[i] >= 0 && valores[i] <= 9) {
                saida.append("0");
            }
            saida.append(valores[i]);
            if (i < last) {
                saida.append(" ");
            }
        }
    }

    public static void printFrequenciaNumeros(int[] frequencia) {
        StringBuilder saida = new StringBuilder();
        int num = 0;
        for (int i = 0; i < frequencia.length; i++) {
            num = i + 1;
            if (num <= 9) {
                saida.append(0);
            }
            saida.append(num).append("=").append(frequencia[i]).append("\n");
        }
        print("Frequencia dos Numeros", saida);
    }

    public static void printFrequenciaAposta(int[] frequencias, List<int[]> apostas) {
        StringBuilder saida = new StringBuilder();
        int idx = 0;
        for (var aposta : apostas) {
            for (int i = 0; i < aposta.length; i++) {
                idx = aposta[i] - 1;
                if (aposta[i] <= 9) {
                    saida.append(0);
                }
                saida.append(aposta[i]).append("=").append(frequencias[idx]).append(" ");
            }
            saida.append("\n");
        }
        print("Frequencia das Apostas", saida);
    }

    public static void print(String titulo, StringBuilder resultados) {
        final  var stars = "********************************\n";
        StringBuilder s = new StringBuilder();
        s.append(stars);
        s.append(titulo).append("\n");
        s.append(stars);
        s.append(resultados);
        s.append(stars);
        System.out.println(s);
    }

    public static void print(String titulo, List<int[]> sorteios) {
        StringBuilder appender = new StringBuilder();
        for (var jogo : sorteios) {
            appender.append(Utils.stringfy(jogo)).append("\n");
        }
        print(titulo, appender);
    }

    public static void printSorteiosAnteriores(List<int[]> sorteios) {
        print("SORTEIOS ANTERIORES", sorteios);
    }
}
