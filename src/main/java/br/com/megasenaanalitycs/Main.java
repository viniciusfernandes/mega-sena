package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.integracao.TipoJogo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static br.com.megasenaanalitycs.integracao.SorteiosReader.lerSorteiosAnteriores;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.*;
import static br.com.megasenaanalitycs.utils.Utils.print;
import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class Main {
    private static final int TAMANHO_BLOCCO_FREQUENCIA = 24;
    private static final Scanner scanner = new Scanner(System.in);
    private static String option;
    private static File sorteiosFile = null;
    private static TipoJogo tipoJogo;

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
                    validarApostas();
                    break;
                case "5":
                    printFrequenciaUltimosSorteios();
                    break;
                case "6":
                    printFrequenciaTodosSorteios();
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

    private static void printFrequenciaUltimosSorteios() {
        printNumeroPorFrequencia(gerarFrequenciaUltimosSorteios());
    }

    private static int[] gerarFrequenciaUltimosSorteios() {
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        return gerarUltimoBlocoFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
    }

    private static void printFrequenciaTodosSorteios() {
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var frequencias = gerarFrequenciaSorteios(tipoJogo, sorteios, TAMANHO_BLOCCO_FREQUENCIA);
        printFrequenciaPorSorteio(sorteios, frequencias);
    }

    private static void gerarFrequenciaApostas() throws IOException {
        var apostas = lerApostas(tipoJogo);
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

    private static boolean isEstatisticaApostaValida(int[] aposta, int[] frequencias) {
        var count = 0;
        for (int i = 0; i < aposta.length; i++) {
            if (frequencias[aposta[i] - 1] >= 3) {
                count++;
            }
            if (count > 2) {
                return false;
            }
        }
        return true;
    }

    private static int[] extrairFrequencias(int[] aposta, int[] frequencias) {
        var freq = new int[aposta.length];
        for (int i = 0; i < aposta.length; i++) {
            freq[i] = frequencias[aposta[i] - 1];
        }
        return freq;
    }


    private static void validarApostas() throws IOException {
        var apostas = lerApostas(tipoJogo);
        var sorteios = lerSorteiosAnteriores(tipoJogo, sorteiosFile);
        var frequencias = gerarFrequenciaUltimosSorteios();
        var repetidas = new ArrayList<int[]>();

        final var REPETIDA = "repetida";
        final var SORTEADA = "sorteada";
        final var DESFAVORAVEL = "desfavoravel";
        var mensagensMap = Map.of(
                DESFAVORAVEL, new ArrayList<String>(),
                SORTEADA, new ArrayList<String>(),
                REPETIDA, new ArrayList<String>());
        var numAposta = 0;
        for (var aposta : apostas) {
            numAposta++;
            if (contains(sorteios, aposta)) {
                mensagensMap.get(SORTEADA).add("A aposta número " + stringfy(numAposta) + " [" + stringfy(aposta) + "] já existe em resultados anteriores");
            }
            if (!isEstatisticaApostaValida(aposta, frequencias)) {
                var frequencia = extrairFrequencias(aposta, frequencias);
                mensagensMap.get(DESFAVORAVEL).add("A aposta número " + stringfy(numAposta)
                        + " [" + stringfy(aposta) + "] não contém uma estatística favorável => " + stringfy(frequencia));
            }
            if (contains(repetidas, aposta)) {
                mensagensMap.get(REPETIDA).add("A aposta número " + stringfy(numAposta) + " [" + stringfy(aposta) + "] esta repetida");
            } else {
                repetidas.add(aposta);
            }

        }

        if (mensagensMap.get(REPETIDA).isEmpty() && mensagensMap.get(DESFAVORAVEL).isEmpty() && mensagensMap.get(SORTEADA).isEmpty()) {
            System.out.println("TODOS OS JOGOS ESTÃO OK!");
        } else {
            mensagensMap.forEach((tipo, mensagens) -> {
                for (var mensagem : mensagens) {
                    System.err.println(mensagem);
                }
            });
        }
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
        List<int[]> apostas = lerApostas(tipoJogo);
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

    private static List<int[]> lerApostas(TipoJogo tipoJogo) throws IOException {
        var apostasFile = new File("src/main/resources/apostas.txt");
        BufferedReader reader = new BufferedReader(new FileReader(apostasFile));
        String line = null;
        var apostas = new ArrayList<int[]>();
        String[] numeros;
        int lineNum = 0;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            try {
                if (line.isEmpty() || line.isBlank() || (line.charAt(0) < '0' || line.charAt(0) > '9')) {
                    continue;
                }
                numeros = line.split(" ");
                var aposta = new int[tipoJogo.numeros];
                for (int i = 0; i < numeros.length; i++) {
                    aposta[i] = Integer.parseInt(numeros[i]);
                }
                Arrays.sort(aposta);
                apostas.add(aposta);
            } catch (Exception e) {
                System.err.println("Falha na linha " + lineNum + " => " + line);
                e.printStackTrace();
                throw e;
            }
        }
        reader.close();
        return apostas;
    }
}
