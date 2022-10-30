package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.EstatisticaDinamica.gerarFrequenciaSorteios;
import static org.example.EstatisticaDinamica.printFrequenciaNumerosSorteados;

public class Main {
    private static List<int[]> sorteiosAnteriores = new ArrayList<>();
    private static int TAMANHO_BLOCCO_FREQUENCIA = 15;
    public static void main(String[] args) throws IOException {
        conferir(17, 18, 20, 37, 45, 53);
        lerSorteiosAnteriores();
        //imprimirJogosAnteriores();
        var apostas = lerApostas();
        verificarExistenciaResultadosAnteriores(apostas);

        int[] frequenciaSorteios = EstatisticaDinamica.gerarUltimoBlocoFrequenciaSorteios(sorteiosAnteriores, TAMANHO_BLOCCO_FREQUENCIA);
        Utils.printFrequenciaAposta(frequenciaSorteios, apostas);
        EstatisticaDinamica.numeroComFrequenciaBaixa(frequenciaSorteios, 30);
        //  List<int[]> blocoFrequenciaSorteios = gerarFrequenciaSorteios(sorteiosAnteriores, TAMANHO_BLOCCO_FREQUENCIA);
        //printFrequenciaNumerosSorteados(sorteiosAnteriores, blocoFrequenciaSorteios, TAMANHO_BLOCCO_FREQUENCIA);
    }

    private static void imprimirJogosAnteriores() {
        for (var jogo : sorteiosAnteriores) {
            System.out.println(Utils.stringfy(jogo));
        }
    }

    private static void verificarExistenciaResultadosAnteriores(List<int[]> apostas) {
        String idAposta;
        for (var aposta : apostas) {
            idAposta = Utils.stringfy(aposta);
            if (sorteiosAnteriores.contains(idAposta)) {
                throw new IllegalArgumentException("O aposta [" + idAposta + "] já existe em resultados anteriores");
            }
        }
        System.out.println("TODOS OS JOGOS ESTÃO OK!");
    }

    private static List<int[]> lerApostas() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/vinicius/Projects/mega-sena/src/main/java/org/example/apostas.txt"));
        String line = null;
        var apostas = new ArrayList<int[]>();
        String[] numeros;
        while ((line = reader.readLine()) != null) {
            numeros = line.split(" ");
            var aposta = new int[6];
            for (int i = 0; i < numeros.length; i++) {
                aposta[i] = Integer.parseInt(numeros[i]);
            }
            Arrays.sort(aposta);
            apostas.add(aposta);
        }
        reader.close();
        return apostas;
    }

    private static void lerSorteiosAnteriores() throws IOException {
        String fileName = "/home/vinicius/Projects/mega-sena/src/main/java/org/example/tabela-jogos-anteriores.xml";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        String numero;
        int[] sorteio;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.matches("<td>\\d{2}/\\d{2}/\\d{4}</td>")) {
                sorteio = new int[6];
                for (int i = 0; i < 6; i++) {
                    line = reader.readLine();
                    line = line.trim();
                    line = line.replace("<td>", "").replace("</td>", "");
                    numero = line.substring(1);
                    sorteio[i] = Integer.parseInt(numero);
                }
                sorteiosAnteriores.add(sorteio);
            }
        }
        reader.close();
    }

    private static void conferir(int... sorteados) throws IOException {
        List<int[]> apostas = lerApostas();
        int acertos = 0;
        for (var aposta : apostas) {
            acertos = 0;
            for (int idxApt = 0; idxApt < aposta.length; idxApt++) {
                for (int idxSort = 0; idxSort < sorteados.length; idxSort++) {
                    if (aposta[idxApt] == sorteados[idxSort]) {
                        acertos++;
                        break;
                    }
                }
            }
            if (acertos == 4) {
                System.out.println("QUADRA para " + Utils.stringfy(aposta));
                System.out.println(Utils.stringfy(aposta) + " =>  Quadra!");
            } else if (acertos == 5) {
                System.out.println("QUINA para " + Utils.stringfy(aposta));
                System.out.println(Utils.stringfy(aposta) + " =>  Quina!");
            } else if (acertos >= 6) {
                System.out.println(Utils.stringfy(aposta) + " =>  GANHOU!");
            } else {
                System.out.println(Utils.stringfy(aposta) + " =>  " + acertos + " acertos");
            }
        }
    }


}
