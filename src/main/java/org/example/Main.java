package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private static List<int[]> sorteiosAnteriores = new ArrayList<>();
    private static int TAMANHO_BLOCCO_FREQUENCIA = 15;

    public static void main(String[] args) throws IOException {
        conferir(4, 5, 10, 34, 58, 59);
/*
        lerSorteiosAnteriores();
        //Utils.printSorteiosAnteriores(sorteiosAnteriores);
        var apostas = lerApostas();
        validarApostas(apostas);
        Utils.print("APOSTAS", apostas);
        int[] frequenciaSorteios = EstatisticaDinamica.gerarUltimoBlocoFrequenciaSorteios(sorteiosAnteriores, TAMANHO_BLOCCO_FREQUENCIA);
        //Utils.printFrequenciaAposta(frequenciaSorteios, apostas);
        EstatisticaDinamica.numeroComFrequenciaBaixa(frequenciaSorteios, 4);
        */
    }

    private static void validarApostas(List<int[]> apostas) {
        var apostasRepetidas = new HashSet<String>();
        String idAposta;
        for (var aposta : apostas) {
            idAposta = Utils.stringfy(aposta);
            if (sorteiosAnteriores.contains(idAposta)) {
                throw new IllegalArgumentException("A aposta [" + idAposta + "] já existe em resultados anteriores");
            }
            if (apostasRepetidas.contains(idAposta)) {
                throw new IllegalArgumentException("A aposta [" + idAposta + "] esta repetida");
            } else {
                apostasRepetidas.add(idAposta);
            }
        }
        System.out.println("TODOS OS JOGOS ESTÃO OK!");
    }


    private static List<int[]> lerApostas() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/vinicius/Projects/mega-sena/src/main/java/org/example/apostas.txt"));
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
                var aposta = new int[6];
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

    private static void lerSorteiosAnteriores() throws IOException {
        String fileName = "/home/vinicius/Projects/mega-sena/src/main/java/org/example/tabela-jogos-anteriores.xml";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        String numero;
        int[] sorteio;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.matches("<td>\\d{2}/\\d{2}/\\d{4}</td>")) {
                sorteio = new int[6];
                for (int i = 0; i < 6; i++) {
                    line = reader.readLine();
                    line = line.trim();
                    line = line.replace("<td>", "").replace("</td>", "");
                    numero = line.substring(1);
                    sorteio[i] = Integer.parseInt(numero);
                }
                sorteiosAnteriores.add(sorteio);
            }
        }
        reader.close();
    }

    private static void conferir(int... sorteados) throws IOException {
        List<int[]> apostas = lerApostas();
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
