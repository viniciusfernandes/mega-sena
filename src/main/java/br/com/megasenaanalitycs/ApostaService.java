package br.com.megasenaanalitycs;

import br.com.megasenaanalitycs.repository.ApostaRepository;
import br.com.megasenaanalitycs.repository.TipoJogo;

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

    public List<int[]> lerApostas(TipoJogo tipoJogo) throws IOException {
        return apostaRepository.lerApostas(tipoJogo);
    }

    public List<int[]> lerSorteiosAnteriores(TipoJogo tipoJogo) {
        return apostaRepository.lerSorteiosAnteriores(tipoJogo);
    }

    public boolean isEstatisticaApostaValida(int[] aposta, int[] frequencias) {
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

    public List<int[]> gerarFrequenciaSorteios(TipoJogo tipoJogo) {
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var tamanhoBloco = TAMANHO_BLOCCO_FREQUENCIA;
        if (tamanhoBloco > sorteios.size()) {
            tamanhoBloco = sorteios.size();
        }
        int lastIdx = sorteios.size() - tamanhoBloco;
        List<int[]> blocoSorteios = null;
        List<int[]> frequencias = new ArrayList<>();
        int[] frequencia = null;
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


    public List<String> validarApostas(TipoJogo tipoJogo) throws IOException {
        var apostas = lerApostas(tipoJogo);
        var sorteios = lerSorteiosAnteriores(tipoJogo);
        var frequencias = gerarUltimoBlocoFrequenciaSorteios(tipoJogo);
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
        return mensagensMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private boolean contains(Collection<int[]> sorteios, int[] aposta) {
        for (var sorteio : sorteios) {
            if (Arrays.equals(sorteio, aposta)) {
                return true;
            }
        }
        return false;
    }

    private int[] extrairFrequencias(int[] aposta, int[] frequencias) {
        var freq = new int[aposta.length];
        for (int i = 0; i < aposta.length; i++) {
            freq[i] = frequencias[aposta[i] - 1];
        }
        return freq;
    }
}
