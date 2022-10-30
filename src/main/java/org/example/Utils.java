package org.example;

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
        StringBuilder s = new StringBuilder();
        s.append("**********************\n");
        s.append(titulo).append("\n");
        s.append("**********************\n");
        s.append(resultados);
        s.append("**********************\n");
        System.out.println(s);
    }
}
