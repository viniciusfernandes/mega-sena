package br.com.megasenaanalitycs.service;

import br.com.megasenaanalitycs.domain.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class ApostaService {
    private final ApostaRepository apostaRepository;
    private static final int QUANTIDADE_SORTEIOS_PARA_FREQUENCIA = 24;

    public ApostaService(ApostaRepository apostaRepository) {
        this.apostaRepository = apostaRepository;
    }

    private final Random random = new Random();

    public List<Apostador> lerApostadores(TipoJogo tipoJogo) throws IOException {
        return apostaRepository.lerApostadores(tipoJogo);
    }

    public List<int[]> lerApostas(TipoJogo tipoJogo) throws IOException {
        var apostadores = apostaRepository.lerApostadores(tipoJogo);
        return apostadores.stream().flatMap(apostador -> apostador.apostas.stream()).collect(Collectors.toList());
    }

    public List<int[]> lerSorteiosAnteriores(TipoJogo tipoJogo) {
        return apostaRepository.lerSorteiosAnteriores(tipoJogo);
    }

    public boolean isEstatisticaApostaValida(int[] aposta, int[] frequencias, TipoJogo tipoJogo) {
        var count = 0;
        for (int i = 0; i < aposta.length; i++) {
            if (frequencias[aposta[i] - 1] >= tipoJogo.frequenciaMaxima) {
                count++;
            }
            if (count >= tipoJogo.ocorrenciaMaxima) {
                return false;
            }
        }
        return true;
    }

    public List<int[]> gerarFrequenciasSorteio(TipoJogo tipoJogo) {
        return gerarFrequenciasPorBloco(tipoJogo).stream()
                .map(bloco -> bloco.frequencias)
                .collect(Collectors.toList());
    }

    public List<FrequenciasPorBloco> gerarFrequenciasPorBloco(TipoJogo tipoJogo) {
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var tamanhoBloco = QUANTIDADE_SORTEIOS_PARA_FREQUENCIA;
        if (tamanhoBloco > sorteios.size()) {
            tamanhoBloco = sorteios.size();
        }
        int totalBlocos = sorteios.size() - tamanhoBloco + 1;
        List<int[]> blocoSorteios;
        List<FrequenciasPorBloco> frequencias = new ArrayList<>();
        int idx;
        for (int numBloco = 1; numBloco <= totalBlocos; numBloco++) {
            int idxBloco = numBloco - 1;
            blocoSorteios = sorteios.subList(idxBloco, idxBloco + tamanhoBloco);
            var frequenciaPorBloco = new FrequenciasPorBloco(numBloco, numBloco + tamanhoBloco - 1, tipoJogo);
            var posicaoNoBloco = 1;
            for (int[] sorteio : blocoSorteios) {
                for (int i = 0; i < sorteio.length; i++) {
                    idx = sorteio[i] - 1;
                    frequenciaPorBloco.adicionarFrequencia(idx, posicaoNoBloco);
                }
                posicaoNoBloco++;
            }
            frequencias.add(frequenciaPorBloco);
        }
        return frequencias;
    }

    public int[] gerarUltimoBlocoFrequenciaSorteios(TipoJogo tipoJogo) {
        var frequencias = gerarFrequenciasSorteio(tipoJogo);
        return frequencias.get(frequencias.size() - 1);
    }

    public void ordernarApostas(TipoJogo tipoJogo) throws IOException {
        apostaRepository.ordernarApostas(tipoJogo);
    }

    public void ordernar(List<int[]> apostas) {
        apostas.forEach(Arrays::sort);
    }

    public List<String> validarHipoteseEstatistica(TipoJogo tipoJogo) {
        var mensagens = new ArrayList<String>();
        var frequencias = gerarFrequenciasSorteio(tipoJogo);
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var primeiraFreq = frequencias.size() - 2;
        var total = 50;
        for (int i = primeiraFreq, j = sorteios.size() - 1; i >= primeiraFreq - total; i--, j--) {
            var frequencia = frequencias.get(i);
            var aposta = sorteios.get(j);
            if (!isEstatisticaApostaValida(aposta, frequencia, tipoJogo)) {
                var frequenciaAposta = extrairFrequenciaAposta(aposta, frequencia);
                mensagens.add("A aposta número " + stringfy(j) +
                        " [" + stringfy(aposta) + "] não contém uma estatística favorável => " +
                        stringfy(frequenciaAposta));
            }
        }
        mensagens.add(0, "Total de apostas avaliadas=" + (total) + ". Total de apostas invalidas=" + mensagens.size() + "\n");
        return mensagens;
    }

    public List<int[]> gerarApostas(TipoJogo tipoJogo, int numeroMaxTentativas) {
        var apostas = new ArrayList<int[]>();
//        var sorterios = lerSorteiosAnteriores(tipoJogo);
//        var ultimoSorteio = sorterios.get(sorterios.size() - 1);
//        var maxAcertos = 1;
        var frequenciaSorteios = gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
        while (numeroMaxTentativas-- >= 0) {
            var aposta = gerarAposta(tipoJogo);
            if (isEstatisticaApostaValida(aposta, frequenciaSorteios, tipoJogo)) {
                apostas.add(aposta);
            }
        }
        ordernar(apostas);
        return apostas;
    }

    public int conferirAcertos(int[] aposta, int[] sorteio) {
        int acertos = 0;
        for (int idxApt = 0; idxApt < aposta.length; idxApt++) {
            for (int idxSort = 0; idxSort < aposta.length; idxSort++) {
                if (aposta[idxApt] == sorteio[idxSort]) {
                    acertos++;
                    break;
                }
            }
        }
        return acertos;
    }


    public List<ValidacaoAposta> validarApostas(TipoJogo tipoJogo) throws IOException {
        var apostadores = lerApostadores(tipoJogo);
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var frequencias = gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
        var repetidas = new ArrayList<int[]>();

        var validacoes = new ArrayList<ValidacaoAposta>();
        int[] frequencia = null;
        for (var apostador : apostadores) {
            var numAposta = 0;
            for (var aposta : apostador.apostas) {
                numAposta++;
                if (contains(repetidas, aposta)) {
                    validacoes.add(new ValidacaoAposta(apostador.nome, aposta, TipoAposta.REPETIDA, numAposta, frequencia));
                } else {
                    repetidas.add(aposta);
                }
                if (hasNumeroInvalido(aposta, tipoJogo)) {
                    validacoes.add(new ValidacaoAposta(apostador.nome, aposta, TipoAposta.NUMERO_INVALIDO, numAposta, frequencia));
                    break;
                }
                if (contains(sorteios, aposta)) {
                    validacoes.add(new ValidacaoAposta(apostador.nome, aposta, TipoAposta.SORTEADA, numAposta, frequencia));
                }
                if (!isEstatisticaApostaValida(aposta, frequencias, tipoJogo)) {
                    frequencia = extrairFrequenciaAposta(aposta, frequencias);
                    validacoes.add(new ValidacaoAposta(apostador.nome, aposta, TipoAposta.DESFAVORAVEL, numAposta, frequencia));
                }
                if (hasNumerosRepetidos(aposta)) {
                    validacoes.add(new ValidacaoAposta(apostador.nome, aposta, TipoAposta.NUMERO_REPETIDO, numAposta, frequencia));
                }

                frequencia = null;
            }
        }
        return validacoes;
    }

    private boolean contains(Collection<int[]> sorteios, int[] aposta) {
        for (var sorteio : sorteios) {
            if (Arrays.equals(sorteio, aposta)) {
                return true;
            }
        }
        return false;
    }

    private int[] extrairFrequenciaAposta(int[] aposta, int[] frequencias) {
        var freq = new int[aposta.length];
        for (int i = 0; i < aposta.length; i++) {
            freq[i] = frequencias[aposta[i] - 1];
        }
        return freq;
    }

    private int[] gerarAposta(TipoJogo tipoJogo) {
        var aposta = new int[tipoJogo.quantidadeNumeros];
        var indexSorteados = new boolean[tipoJogo.maiorDezena];
        int num;
        int index = 0;
        do {
            num = random.nextInt(tipoJogo.maiorDezena);
            if (indexSorteados[num]) {
                continue;
            }
            indexSorteados[num] = true;
            aposta[index++] = num + 1;
        } while (index < tipoJogo.quantidadeNumeros);
        return aposta;
    }

    private boolean hasNumerosRepetidos(int[] aposta) {
        var numeros = new HashSet<Integer>();
        for (var num : aposta) {
            if (numeros.contains(num)) {
                return true;
            }
            numeros.add(num);
        }
        return false;
    }

    private boolean hasNumeroInvalido(int[] aposta, TipoJogo tipoJogo) {
        for (var num : aposta) {
            if (num < 1 || num > tipoJogo.maiorDezena) {
                return true;
            }
        }
        return false;
    }
}
