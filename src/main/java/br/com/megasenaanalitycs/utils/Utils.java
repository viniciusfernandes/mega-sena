package br.com.megasenaanalitycs.utils;

import java.util.List;

public class Utils {
    private Utils() {
    }

    public static String stringfy(int valor) {
        return valor >= 0 && valor <= 9 ? "0" + valor : String.valueOf(valor);
    }

    public static String stringfy(int[] valores) {
        int last = valores.length - 1;
        var saida = "";
        for (int i = 0; i < valores.length; i++) {
            saida += stringfy(valores[i]);
            if (i < last) {
                saida += " ";
            }
        }
        return saida;
    }

    public static void printFrequenciaNumeros(int[] frequencia) {
        StringBuilder saida = new StringBuilder();
        int maior = -1;
        for (int i = 0; i < frequencia.length; i++) {
            if (maior < frequencia[i]) {
                maior = frequencia[i];
            }
        }
        int space = String.valueOf(maior).length() - 2;
        String blankSpace = " ";
        if (space > 0) {
            for (int i = 0; i < space; i++) {
                blankSpace += " ";
            }
        }


        for (int i = 0; i < frequencia.length; i++) {
            if (i <= 9) {
                saida.append(0);
            }
            saida.append(i).append(blankSpace);
        }
        saida.append("\n");
        for (int i = 0; i < frequencia.length; i++) {
            if (frequencia[i] <= 9) {
                saida.append(0);
            }
            saida.append(frequencia[i]).append(" ");
        }
        saida.append("\n");
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
        print(titulo, resultados.toString());
    }

    public static void print(String titulo, String resultados) {
        final var stars = "********************************\n";
        StringBuilder s = new StringBuilder();
        s.append(stars);
        s.append(titulo).append("\n");
        s.append(stars);
        s.append(resultados);
        s.append(stars);
        System.out.println(s);
    }

    public static void print(String titulo, List<int[]> apostas) {
        StringBuilder appender = new StringBuilder();
        for (var jogo : apostas) {
            appender.append(Utils.stringfy(jogo)).append("\n");
        }
        print(titulo, appender);
    }

    public static void printSorteiosAnteriores(List<int[]> sorteios) {
        print("SORTEIOS ANTERIORES", sorteios);
    }
}
