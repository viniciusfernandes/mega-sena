package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.integracao.TipoJogo;
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
        File sorteiosFile = null;
        final var apostasFile = new File("src/main/resources/apostas.txt");
        Scanner scanner = new Scanner(System.in);
        String option;
        List<int[]> sorteios;
        List<int[]> frequencias;
        TipoJogo tipoJogo = null;
        do {
            if (tipoJogo == null) {
                option = "1";
            } else {
                System.out.println("\n*****************");
                System.out.println("Escolha uma das opções:");
                System.out.println("1- Escolha o Tipo de Jogo");
                System.out.println("2- Ler Sorteios Anteriores");
                System.out.println("3- Conferir Apostas");
                System.out.println("4- Validar apostas");
                System.out.println("5- Gerar Frequencia Ultimos Sorteios");
                System.out.println("6- Gerar Frequencia Todos Sorteios");
                System.out.println("S- Sair");
                System.out.println("*****************");
                option = scanner.nextLine();
            }
            switch (option.toUpperCase()) {
                case ("1"):
                    System.out.println("1- Mega Sena");
                    System.out.println("2- Lotomania");
                    option = scanner.nextLine();
                    if ("1".equalsIgnoreCase(option.toUpperCase())) {
                        tipoJogo = TipoJogo.MEGASENA;
                    } else {
                        tipoJogo = TipoJogo.LOTOMANIA;
                    }
                    System.out.println("\n*****************");
                    System.out.println("Você escolheu " + tipoJogo);
                    sorteiosFile = new File("src/main/resources/" + tipoJogo + ".xlsx");
                    break;
                case "2":
                    sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
                    Utils.print("Sorteios Anteriores", sorteios);
                    break;
                case "3":
                    conferir(tipoJogo, new int[]{4, 5, 10, 34, 58, 59});
                    break;
                case "4":
                    verificarApostas(tipoJogo, sorteiosFile, apostasFile);
                    break;
                case "5":
                    sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
                    var frequenciaSorteios = EstatisticaDinamica.gerarUltimoBlocoFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
                    EstatisticaDinamica.printNumeroPorFrequencia(frequenciaSorteios);
                    break;
                case "6":
                    sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
                    frequencias = EstatisticaDinamica.gerarFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
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

    private static void verificarApostas(TipoJogo tipoJogo, File sorteioFile, File apostasFile) throws IOException {
        var apostas = lerApostas(tipoJogo, apostasFile);
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteioFile);
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


    private static void conferir(TipoJogo tipoJogo, int[] sorteados) throws IOException {
        List<int[]> apostas = lerApostas(tipoJogo, new File("src/main/resources/apostas.txt"));
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
