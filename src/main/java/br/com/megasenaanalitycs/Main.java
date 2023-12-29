package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.repository.ApostaRepository;
import br.com.megasenaanalitycs.repository.TipoJogo;

import java.io.IOException;
import java.util.*;

import static br.com.megasenaanalitycs.utils.EstatisticaUtils.*;
import static br.com.megasenaanalitycs.utils.Utils.print;
import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static String option;
    private static TipoJogo tipoJogo;
    private static ApostaService apostaService;

    public static void main(String[] args) throws IOException {
        apostaService = new ApostaService(new ApostaRepository());
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
        var sorteios = apostaService.lerSorteiosAnteriores(tipoJogo);
        var frequencia = apostaService.gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
        var apostas = gerarApostas(5);
        print("Apostas sem as Estatísticas", apostas);
    }

    private static void printFrequenciaUltimosSorteios() {
        printNumeroPorFrequencia(apostaService.gerarUltimoBlocoFrequenciaSorteios(tipoJogo));
    }

    private static void printFrequenciaTodosSorteios() {
        var sorteios = apostaService.lerSorteiosAnteriores(tipoJogo);
        var frequencias = apostaService.gerarFrequenciaSorteios(tipoJogo);
        printFrequenciaPorSorteio(sorteios, frequencias);
    }

    private static void gerarFrequenciaApostas() throws IOException {
        var apostas = apostaService.lerApostas(tipoJogo);
        var frequencia = apostaService.gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
        printFrequenciaPorApostas(apostas, frequencia);
    }

    private static void lerSorteiorAnteriores() {
        print("Sorteios Anteriores", apostaService.lerSorteiosAnteriores(tipoJogo));
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


    private static void validarApostas() throws IOException {
        var mensagens = apostaService.validarApostas(tipoJogo);
        if (mensagens.isEmpty()) {
            System.out.println("TODOS OS JOGOS ESTÃO OK!");
        } else {
            mensagens.forEach(System.err::println);
        }
    }

    private static void conferir() throws IOException {
        System.out.println("\n*****************");
        System.out.println("Digite os numeros Sorteados");
        var sorteados = toNumeros(scanner.nextLine());
        if (sorteados.length != tipoJogo.numeros) {
            System.err.println("Você deve digitar apenas " + tipoJogo.numeros + " numeros!");
            return;
        }
        List<int[]> apostas = apostaService.lerApostas(tipoJogo);
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
