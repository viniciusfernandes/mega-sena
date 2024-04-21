package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.repository.Aposta;
import br.com.megasenaanalitycs.repository.ApostaRepository;
import br.com.megasenaanalitycs.repository.TipoJogo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static br.com.megasenaanalitycs.utils.EstatisticaUtils.printFrequenciaPorApostas;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.printFrequenciaPorSorteio;
import static br.com.megasenaanalitycs.utils.EstatisticaUtils.printNumeroPorFrequencia;
import static br.com.megasenaanalitycs.utils.Utils.print;

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
                System.out.println("H- Validar Hipotese");
                System.out.println("S- Sair");
                System.out.println("*****************");
                option = scanner.nextLine();
            }
            switch (option.toUpperCase()) {
                case ("1"):
                    escolherTipoJogo();
                    break;
                case "2":
                    printSorteiorAnteriores();
                    break;
                case "3":
                    printConferenciaApostas();
                    break;
                case "4":
                    printValidacaoApostas();
                    break;
                case "5":
                    printFrequenciaUltimosSorteios();
                    break;
                case "6":
                    printFrequenciaTodosSorteios();
                    break;
                case "7":
                    printNovasApostasEstatisticas();
                    break;
                case "8":
                    printNovasApostasSemEstatisticas();
                    break;
                case "9":
                    printFrequenciaApostas();
                    break;
                case "H":
                    printValidacaoHipoteseEstatistica();
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

    private static void printValidacaoHipoteseEstatistica() {
        System.out.println("\n*****************");
        var mensagens = apostaService.validarHipoteseEstatistica(tipoJogo);
        mensagens.forEach(System.err::println);
    }

    private static void printNovasApostasSemEstatisticas() {
        System.out.println("\n*****************");
        System.out.println("Digite a quantidade de apostas");
        var numeroApostas = Integer.parseInt(scanner.nextLine());
        print("Apostas sem Estatísticas", apostaService.gerarApostas(tipoJogo, numeroApostas));
    }

    private static void printNovasApostasEstatisticas() {
        System.out.println("\n*****************");
        System.out.println("Digite a quantidade de apostas");
        var apostas = apostaService.gerarApostas(tipoJogo, 5);
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

    private static void printFrequenciaApostas() throws IOException {
        var apostas = apostaService.lerApostas(tipoJogo);
        var frequencia = apostaService.gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
        printFrequenciaPorApostas(apostas, frequencia);
    }

    private static void printSorteiorAnteriores() {
        print("Sorteios Anteriores", apostaService.lerSorteiosAnteriores(tipoJogo));
    }

    private static void escolherTipoJogo() {
        System.out.println("1- Mega Sena");
        System.out.println("2- Lotomania");
        option = scanner.nextLine();
        if ("1".equalsIgnoreCase(option)) {
            tipoJogo = TipoJogo.MEGASENA;
        } else {
            tipoJogo = TipoJogo.LOTOMANIA;
        }
        System.out.println("\n*****************");
        System.out.println("Você escolheu " + tipoJogo);
    }


    private static void printValidacaoApostas() throws IOException {
        var mensagens = apostaService.validarApostas(tipoJogo);
        if (mensagens.isEmpty()) {
            System.out.println("TODOS OS JOGOS ESTÃO OK!");
        } else {
            mensagens.forEach(System.err::println);
        }
    }

    private static void printConferenciaApostas() throws IOException {
        System.out.println("\n*****************");
        System.out.println("Digite os numeros Sorteados");
        var sorteados = toNumeros(scanner.nextLine());
        if (sorteados.length != tipoJogo.numeros) {
            System.err.println("Você deve digitar apenas " + tipoJogo.numeros + " numeros!");
            return;
        }
        List<Aposta> apostas = apostaService.lerApostas(tipoJogo);
        var premiados = new HashMap<String, List<String>>();
        int acertos;
        for (var aposta : apostas) {
            acertos = 0;
            for (int idxApt = 0; idxApt < aposta.totalNumeros; idxApt++) {
                for (int idxSort = 0; idxSort < sorteados.length; idxSort++) {
                    if (aposta.numeros[idxApt] == sorteados[idxSort]) {
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
            System.out.println(aposta + " =>  " + acertos + " acertos");
        }
        premiados.forEach((premiacao, listaPremiados) -> {
            System.out.println("******" + premiacao + "******");
            for (var premiado : listaPremiados) {
                System.out.println(premiado);
            }
        });
    }

    private static void addPermiado(String premiacao, Aposta aposta, HashMap<String, List<String>> premiadosMap) {
        var premiados = premiadosMap.get(premiacao);
        if (premiados == null) {
            premiados = new ArrayList<>();
            premiadosMap.put(premiacao, premiados);
        }
        premiados.add(aposta.toString());
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
