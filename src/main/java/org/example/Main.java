package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //conferir(4, 13, 21, 26, 47, 51);
        processarTabelaJogosAnteriores();
    }

    private static List<int[]> lerJogos() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/vinicius/Projects/megasena/src/main/java/org/example/jogos.txt"));
        String line = null;
        var jogos = new ArrayList<int[]>();
        String[] numeros;
        while ((line = reader.readLine()) != null) {
            numeros = line.split(" ");
            var jogo = new int[6];
            for (int i = 0; i < numeros.length; i++) {
                jogo[i] = Integer.parseInt(numeros[i]);
            }
            Arrays.sort(jogo);
            jogos.add(jogo);
        }
        reader.close();
        return jogos;
    }

    private static void processarTabelaJogosAnteriores() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/vinicius/Projects/megasena/src/main/java/org/example/tabela-jogos-anteriores.xml"));
        String line = null;
        StringBuilder jogo;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.matches("<td>\\d{2}/\\d{2}/\\d{4}</td>")) {
                jogo = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    line = reader.readLine();
                    line = line.trim();
                    line = line.replace("<td>", "").replace("</td>", "");
                    jogo.append(line.substring(1)).append(" ");
                }
                System.out.println(jogo);
            }
        }
        reader.close();
    }

    private static void conferir(int... sorteados) throws IOException {
        List<int[]> jogos = lerJogos();
        int acertos = 0;
        for (var jogo : jogos) {
            acertos = 0;
            for (int i = 0; i < jogo.length; i++) {
                if (jogo[i] == sorteados[i]) {
                    acertos++;
                }
            }
            if (acertos == 4) {
                System.out.println("QUADRA para " + toString(jogo));
            } else if (acertos == 5) {
                System.out.println("QUINA para " + toString(jogo));
            } else if (acertos >= 6) {
                System.out.println("GANHOU!!!");
            } else {
                System.out.println("Jogo " + toString(jogo) + "acertou " + acertos);
            }
        }
    }

    private static String toString(int[] jogo) throws IOException {
        StringBuilder s = new StringBuilder();
        for (var numero : jogo) {
            if (numero <= 9) {
                s.append("0");
            }
            s.append(numero).append(" ");
        }
        return s.toString();
    }
}
