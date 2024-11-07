package br.com.megasenaanalitycs.service;

import br.com.megasenaanalitycs.domain.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.megasenaanalitycs.utils.Utils.stringfy;

public class ApostaService {
    private final ApostaRepository apostaRepository;
    private static final int TAMANHO_BLOCCO_FREQUENCIA = 24;

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

    public boolean isEstatisticaApostaValida(int[] aposta, int[] frequencias) {
        var count = 0;
        final var maxOccurence = 3;
        final var maxFrequencia = 3;
        for (int i = 0; i < aposta.length; i++) {
            if (frequencias[aposta[i] - 1] >= maxFrequencia) {
                count++;
            }
            if (count >= maxOccurence) {
                return false;
            }
        }
        return true;
    }

    public List<int[]> gerarFrequenciaSorteios(TipoJogo tipoJogo) {
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var tamanhoBloco = TAMANHO_BLOCCO_FREQUENCIA;
        if (tamanhoBloco > sorteios.size()) {
            tamanhoBloco = sorteios.size();
        }
        int lastIdx = sorteios.size() - tamanhoBloco;
        List<int[]> blocoSorteios;
        List<int[]> frequencias = new ArrayList<>();
        int[] frequencia;
        int idx;
        for (int idxBloco = 0; idxBloco <= lastIdx; idxBloco++) {
            blocoSorteios = sorteios.subList(idxBloco, idxBloco + tamanhoBloco);
            frequencia = new int[tipoJogo.total];

            for (int[] sorteio : blocoSorteios) {
                for (int i = 0; i < sorteio.length; i++) {
                    idx = sorteio[i] - 1;
                    frequencia[idx]++;
                }
            }
            frequencias.add(frequencia);
        }
        return frequencias;
    }

    public int[] gerarUltimoBlocoFrequenciaSorteios(TipoJogo tipoJogo) {
        var frequencias = gerarFrequenciaSorteios(tipoJogo);
        return frequencias.get(frequencias.size() - 1);
    }

    public List<String> validarHipoteseEstatistica(TipoJogo tipoJogo) {
        var mensagens = new ArrayList<String>();
        var frequencias = gerarFrequenciaSorteios(tipoJogo);
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var primeiraFreq = frequencias.size() - 2;
        var total = 50;
        for (int i = primeiraFreq, j = sorteios.size() - 1; i >= primeiraFreq - total; i--, j--) {
            var frequencia = frequencias.get(i);
            var aposta = sorteios.get(j);
            if (!isEstatisticaApostaValida(aposta, frequencia)) {
                var frequenciaAposta = extrairFrequenciaAposta(aposta, frequencia);
                mensagens.add("A aposta número " + stringfy(j) +
                        " [" + stringfy(aposta) + "] não contém uma estatística favorável => " +
                        stringfy(frequenciaAposta));
            }
        }
        mensagens.add(0, "Total de apostas avaliadas=" + (total) + ". Total de apostas invalidas=" + mensagens.size() + "\n");
        return mensagens;
    }

    public List<int[]> gerarApostasMaximas(TipoJogo tipoJogo, int numeroMaxTentativas) {
        var apostas = new ArrayList<int[]>();
        var sorterios = lerSorteiosAnteriores(tipoJogo);
        var ultimoSorteio = sorterios.get(sorterios.size() - 1);
        var maxAcertos = 1;
        var frequenciaSorteios = gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
        while (numeroMaxTentativas-- >= 0) {
            var aposta = gerarAposta(tipoJogo);
            if (isEstatisticaApostaValida(aposta, frequenciaSorteios) && conferirAcertos(aposta, ultimoSorteio) <= maxAcertos) {
                apostas.add(aposta);
            }
        }
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

        TipoAposta tipoAposta = null;
        var validacoes = new ArrayList<ValidacaoAposta>();
        int[] frequencia = null;
        for (var apostador : apostadores) {
            var numAposta = 0;
            for (var aposta : apostador.apostas) {
                numAposta++;
                if (contains(sorteios, aposta)) {
                    tipoAposta = TipoAposta.SORTEADA;
                } else if (!isEstatisticaApostaValida(aposta, frequencias)) {
                    frequencia = extrairFrequenciaAposta(aposta, frequencias);
                    tipoAposta = TipoAposta.DESFAVORAVEL;
                } else if (contains(repetidas, aposta)) {
                    tipoAposta = TipoAposta.REPETIDA;
                } else {
                    repetidas.add(aposta);
                }
                if (tipoAposta != null) {
                    validacoes.add(new ValidacaoAposta(apostador.nome, aposta, tipoAposta, numAposta, frequencia));
                }
                tipoAposta = null;
                frequencia = null;
            }
        }
        return validacoes;
    }

    public List<int[]> gerarApostas(TipoJogo tipoJogo, int numApostas) {
        var apostas = new ArrayList<int[]>(numApostas);
        while (numApostas > 0) {
            apostas.add(gerarAposta(tipoJogo));
            numApostas--;
        }
        return apostas;
    }

    public int[] gerarAposta(TipoJogo tipoJogo) {
        var aposta = new int[tipoJogo.numeros];
        var indexSorteados = new boolean[tipoJogo.total];
        int num;
        int index = 0;
        do {
            num = random.nextInt(tipoJogo.total);
            if (indexSorteados[num]) {
                continue;
            }
            indexSorteados[num] = true;
            aposta[index++] = num + 1;
        } while (index < tipoJogo.numeros);
        return aposta;
    }

    private int[] toSortedArray(Collection<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).sorted().toArray();
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
}
