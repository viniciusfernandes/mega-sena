package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.integracao.TipoJogo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import static br.com.megasenaanalitycs.integracao.SorteiosReader.lerApostas;
import static br.com.megasenaanalitycs.integracao.SorteiosReader.lerSorteiosAnteriores;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.gerarFrequenciaSorteios;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.gerarUltimoBlocoFrequenciaSorteios;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.printFrequenciaPorApostas;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.printFrequenciaPorSorteio;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.printNumeroPorFrequencia;
import static br.com.megasenaanalitycs.utils.Utils.print;
import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class Main {
    private static final int TAMANHO_BLOCCO_FREQUENCIA = 24;
    private static final Scanner scanner = new Scanner(System.in);
    private static String option;
    private static File sorteiosFile = null;
    private static TipoJogo tipoJogo;
    private static final File apostasFile = new File("src/main/resources/apostas.txt");

    public static void main(String[] args) throws IOException {
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
                System.out.println("7- Gerar Novas Apostas Estatísticas");
                System.out.println("8- Gerar Novas Apostas Sem Estatísticas");
                System.out.println("9- Gerar Frequencia das Apostas");
                System.out.println("S- Sair");
                System.out.println("*****************");
                option = scanner.nextLine();
            }
            switch (option.toUpperCase()) {
                case ("1"):
                    escolherTipoJogo();
                    break;
                case "2":
                    lerSorteiorAnteriores();
                    break;
                case "3":
                    conferir();
                    break;
                case "4":
                    verificarApostas();
                    break;
                case "5":
                    gerarFrequenciaUltimosSorteios();
                    break;
                case "6":
                    gerarFrequenciaTodosSorteios();
                    break;
                case "7":
                    gerarNovasApostasEstatisticas();
                    break;
                case "8":
                    gerarNovasApostasSemEstatisticas();
                    break;
                case "9":
                    gerarFrequenciaApostas();
                    break;
                case "S":
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("A opcao \"" + option + "\" Invalida. Tente novamente.");
                    break;
            }

        } while (!option.equalsIgnoreCase("S"));
        scanner.close();
    }

    private static void gerarNovasApostasSemEstatisticas() {
        System.out.println("\n*****************");
        System.out.println("Digite a quantidade de apostas");
        var numeroApostas = Integer.parseInt(scanner.nextLine());
        print("Apostas sem Estatísticas", gerarApostas(numeroApostas));
    }

    private static void gerarNovasApostasEstatisticas() {
        System.out.println("\n*****************");
        System.out.println("Digite a quantidade de apostas");
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var frequencia = gerarUltimoBlocoFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
        var apostas = gerarApostas(5);
        print("Apostas sem as Estatísticas", apostas);
    }

    private static void gerarFrequenciaUltimosSorteios() {
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var frequenciaSorteios = gerarUltimoBlocoFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
        printNumeroPorFrequencia(frequenciaSorteios);
    }

    private static void gerarFrequenciaTodosSorteios() {
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var frequencias = gerarFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
        printFrequenciaPorSorteio(sorteios, frequencias);
    }

    private static void gerarFrequenciaApostas() throws IOException {
        var apostas = lerApostas(tipoJogo, apostasFile);
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var frequencia = gerarUltimoBlocoFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
        printFrequenciaPorApostas(apostas, frequencia);
    }

    private static void lerSorteiorAnteriores() {
        print("Sorteios Anteriores", lerSorteiosAnteriores(tipoJogo, sorteiosFile));
    }

    private static void escolherTipoJogo() {
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
    }

    private static List<int[]> gerarApostas(int numApostas) {
        var random = new Random();
        var aposta = new HashSet<Integer>();
        var apostas = new ArrayList<int[]>(numApostas);
        int num;
        while (numApostas > 0) {
            do {
                num = random.nextInt(tipoJogo.total) + 1;
                if (aposta.contains(num)) {
                    continue;
                }
                aposta.add(num);
            } while (aposta.size() < tipoJogo.numeros);
            apostas.add(toSortedArray(aposta));
            aposta.clear();
            numApostas--;
        }
        return apostas;
    }


    private static void verificarApostas() throws IOException {
        var apostas = lerApostas(tipoJogo, apostasFile);
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var apostasRepetidas = new HashSet<int[]>();
        for (var aposta : apostas) {
            if (contains(sorteios, aposta)) {
                System.err.println("A aposta [" + stringfy(aposta) + "] já existe em resultados anteriores");
                return;
            }
            if (contains(apostasRepetidas, aposta)) {
                System.err.println("A aposta [" + stringfy(aposta) + "] esta repetida");
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


    private static void conferir() throws IOException {
        System.out.println("\n*****************");
        System.out.println("Digite os numeros Sorteados");
        var sorteados = toNumeros(scanner.nextLine());
        if (sorteados.length != tipoJogo.numeros) {
            System.err.println("Você deve digitar apenas " + tipoJogo.numeros + " numeros!");
            return;
        }
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
            System.out.println(stringfy(aposta) + " =>  " + acertos + " acertos");
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
        premiado.add(stringfy(aposta));
    }

    private static int[] toSortedArray(Set<Integer> bucket) {
        var arr = new int[bucket.size()];
        var i = 0;
        for (var num : bucket) {
            arr[i++] = num;
        }
        Arrays.sort(arr);
        return arr;
    }

    private static int[] toNumeros(String str) {
        var arr = str.replace("\\s+", " ").split(" ");
        var numeros = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            numeros[i] = Integer.valueOf(arr[i]);
        }
        return numeros;
    }
}
