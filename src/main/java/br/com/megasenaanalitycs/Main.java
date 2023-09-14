package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.utils.EstatisticaDinamica;
import br.com.megasenaanalitycs.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static br.com.megasenaanalitycs.integracao.SorteiosReader.lerApostas;
import static br.com.megasenaanalitycs.integracao.SorteiosReader.lerSorteiosAnteriores;

public class Main {
    private static int TAMANHO_BLOCCO_FREQUENCIA = 24;

    public static void main(String[] args) throws IOException {
        final var sorteiosFile = new File("src/main/resources/megasena.xlsx");
        final var apostasFile = new File("src/main/resources/apostas.txt");
        Scanner scanner = new Scanner(System.in);
        String option;
        List<int[]> sorteios;
        List<int[]> frequencias;
        do {
            System.out.println("\n*****************");
            System.out.println("Choose an option:");
            System.out.println("1- Ler Sorteios Anteriores");
            System.out.println("2- Conferir Apostas");
            System.out.println("3- Validar apostas");
            System.out.println("4- Gerar Frequencia Ultimos Sorteios");
            System.out.println("4- Gerar Frequencia Todos Sorteios");
            System.out.println("S- Sair");
            System.out.println("*****************");

            option = scanner.nextLine();

            switch (option.toUpperCase()) {
                case "1":
                    sorteios = lerSorteiosAnteriores(sorteiosFile);
                    Utils.print("Sorteios Anteriores", sorteios);
                    break;
                case "2":
                    conferir(4, 5, 10, 34, 58, 59);
                    break;
                case "3":
                    verificarApostas(sorteiosFile, apostasFile);
                    break;
                case "4":
                    sorteios = lerSorteiosAnteriores(sorteiosFile);
                    var frequenciaSorteios = EstatisticaDinamica.gerarUltimoBlocoFrequenciaSorteios(sorteios, TAMANHO_BLOCCO_FREQUENCIA);
                    EstatisticaDinamica.printNumeroPorFrequencia(frequenciaSorteios);
                    break;
                case "5":
                    sorteios = lerSorteiosAnteriores(sorteiosFile);
                    frequencias = EstatisticaDinamica.gerarFrequenciaSorteios(sorteios, TAMANHO_BLOCCO_FREQUENCIA);
                    EstatisticaDinamica.printFrequenciaPorSorteio(sorteios, frequencias);
                    break;
                case "S":
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opcao Invalida. Tente novamente.");
                    break;
            }

        } while (!option.equalsIgnoreCase("S"));
        scanner.close();
    }

    private static void verificarApostas(File sorteioFile, File apostasFile) throws IOException {
        var apostas = lerApostas(apostasFile);
        var sorteios = lerSorteiosAnteriores(sorteioFile);
        var apostasRepetidas = new HashSet<int[]>();
        for (var aposta : apostas) {
            if (contains(sorteios, aposta)) {
                System.err.println("A aposta [" + Utils.stringfy(aposta) + "] já existe em resultados anteriores");
                return;
            }
            if (contains(apostasRepetidas, aposta)) {
                System.err.println("A aposta [" + Utils.stringfy(aposta) + "] esta repetida");
                return;
            } else {
                apostasRepetidas.add(aposta);
            }
        }
        System.out.println("TODOS OS JOGOS ESTÃO OK!");
    }

    private static boolean contains(Collection<int[]> sorteios, int[] aposta) {
        for (var sorteio : sorteios) {
            if (Arrays.equals(sorteio, aposta)) {
                return true;
            }
        }
        return false;
    }


    private static void conferir(int... sorteados) throws IOException {
        List<int[]> apostas = lerApostas(new File("src/main/resources/apostas.txt"));
        var premiados = new HashMap<String, List<String>>();
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
                addPermiado("quadra", aposta, premiados);
            } else if (acertos == 5) {
                addPermiado("quina", aposta, premiados);
            } else if (acertos >= 6) {
                addPermiado("sextina", aposta, premiados);
            }
            System.out.println(Utils.stringfy(aposta) + " =>  " + acertos + " acertos");
        }
        premiados.forEach((premiacao, listaPremiados) -> {
            System.out.println("******" + premiacao + "******");
            for (var premiado : listaPremiados) {
                System.out.println(premiado);
            }
        });
    }

    private static void addPermiado(String premiacao, int[] aposta, HashMap<String, List<String>> premiados) {
        var premiado = premiados.get(premiacao);
        if (premiado == null) {
            premiado = new ArrayList<>();
            premiados.put(premiacao, premiado);
        }
        premiado.add(Utils.stringfy(aposta));
    }

}
