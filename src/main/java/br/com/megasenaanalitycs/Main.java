package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.domain.*;
import br.com.megasenaanalitycs.service.ApostaService;
import br.com.megasenaanalitycs.utils.Utils;
import org.slf4j.*;

import java.io.IOException;
import java.util.*;

import static br.com.megasenaanalitycs.utils.EstatisticaUtils.*;
import static br.com.megasenaanalitycs.utils.Utils.print;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static String option;
    private static TipoJogo tipoJogo;
    private static ApostaService apostaService;
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        apostaService = new ApostaService(new ApostaRepository());
        do {
            if (tipoJogo == null) {
                option = "1";
            } else {
                System.out.println("\n*****************");
                System.out.println("Escolha uma das opções:");
                System.out.println("1- Escolha o Tipo de Jogo");
                System.out.println("2- Sorteios Anteriores");
                System.out.println("3- Conferir Apostas");
                System.out.println("4- Validar apostas");
                System.out.println("5- Gerar Frequencia Ultimos Sorteios");
                System.out.println("6- Gerar Frequencia Todos Sorteios");
                System.out.println("7- Gerar Frequencia das Apostas");
                System.out.println("8- Gerar Apostas");
                System.out.println("9- Validar Hipotese");
                System.out.println("10- Ordernar Apostas");
                System.out.println("q- Sair");
                System.out.println("*****************");
                option = scanner.nextLine();
            }
            try {
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
                        printFrequenciaApostas();
                        break;
                    case "8":
                        printApostasMaximas();
                        break;
                    case "9":
                        printValidacaoHipoteseEstatistica();
                    case "10":
                        printOrdenacaoApostas();
                    case "q":
                        System.out.println("Encerrando...");
                        break;
                    default:
                        System.out.println("A opcao \"" + option + "\" Invalida. Tente novamente.");
                        break;
                }
            } catch (Exception e) {
                log.error("Houve uma falha na operação escolhida=" + option + ". Tente novamente.", e);
            }
        } while (!option.equalsIgnoreCase("S"));
        scanner.close();
    }

    private static void printOrdenacaoApostas() throws IOException {
        System.out.println("\n*****************");
        apostaService.ordernarApostas(tipoJogo);
        System.out.println("\nApostas Ordenadas");
        System.out.println("\n*****************");
    }

    private static void printValidacaoHipoteseEstatistica() {
        System.out.println("\n*****************");
        var mensagens = apostaService.validarHipoteseEstatistica(tipoJogo);
        mensagens.forEach(System.err::println);
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

    private static void printApostasMaximas() {
        System.out.println("\n*****************");
        System.out.println("Digite a quantidade de tentativas desejada");
        option = scanner.nextLine();
        var tentativas = Integer.parseInt(option);
        var apostas = apostaService.gerarApostas(tipoJogo, tentativas);
        Utils.print("Apostas Maximas", apostas);
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
        var validacoes = apostaService.validarApostas(tipoJogo);
        String mensagem = "";
        for (var validacao : validacoes) {
            mensagem += validacao.nome + " => Aposta (" + Utils.stringfy(validacao.numeroAposta) + ") [" + Utils.stringfy(validacao.aposta) + "]";
            if (TipoAposta.SORTEADA == validacao.tipoAposta) {
                mensagem += " já existe em resultados anteriores";
            } else if (TipoAposta.DESFAVORAVEL == validacao.tipoAposta) {
                mensagem += " não contém uma estatística favorável => " + Utils.stringfy(validacao.frequencia);
            } else if (TipoAposta.REPETIDA == validacao.tipoAposta) {
                mensagem += "  esta repetida";
            }
            mensagem += "\n";
        }
        if (mensagem.isBlank()) {
            System.out.println("TODOS OS JOGOS ESTÃO OK!");
        } else {
            System.err.println(mensagem);
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
        var apostadores = apostaService.lerApostadores(tipoJogo);
        var premiados = new HashMap<String, List<String>>();
        var acertos = new TreeSet<Integer>();
        Arrays.sort(sorteados);
        for (var apostador : apostadores) {
            var melhorAcerto = apostador.verificarApostas(sorteados);
            var apostasEAcertos = apostador.getApostasEAcertos();
            System.out.println("\nApostador: " + apostador.nome);
            for (var apostaEAcerto : apostasEAcertos) {
                System.out.println(Utils.stringfy(apostaEAcerto.aposta) + " => " + apostaEAcerto.acerto.size() + " acertos => " + apostaEAcerto.acerto);
                acertos.addAll(apostaEAcerto.acerto);
            }

            if (melhorAcerto.totalAcerto() == 4) {
                addPermiado("quadra", melhorAcerto, premiados);
            } else if (melhorAcerto.totalAcerto() == 5) {
                addPermiado("quina", melhorAcerto, premiados);
            } else if (melhorAcerto.totalAcerto() >= 6) {
                addPermiado("sextina", melhorAcerto, premiados);
            }

        }

        System.out.println("\nDezenas acertadas => " + Utils.stringfy(acertos.stream().mapToInt(Integer::intValue).toArray()));
        System.out.println("\n****** PREMIACAO ******");
        premiados.forEach((premiacao, listaPremiados) -> {
            System.out.println("******" + premiacao + "******");
            for (var premiado : listaPremiados) {
                System.out.println(premiado);
            }
        });
    }

    private static void addPermiado(String premiacao, ApostaEAcerto acertoAposta, HashMap<String, List<String>> premiadosMap) {
        var premiados = premiadosMap.get(premiacao);
        if (premiados == null) {
            premiados = new ArrayList<>();
            premiadosMap.put(premiacao, premiados);
        }
        premiados.add(Utils.stringfy(acertoAposta.aposta));
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
