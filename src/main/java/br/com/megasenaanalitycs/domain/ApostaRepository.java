package br.com.megasenaanalitycs.domain;

import br.com.megasenaanalitycs.utils.Utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public class ApostaRepository {
    private static final Logger logger = LoggerFactory.getLogger(ApostaRepository.class);
    private final File apostasFile = new File("src/main/resources/apostas.txt");

    public List<Apostador> lerApostadores(TipoJogo tipoJogo) throws IOException {
        var reader = new BufferedReader(new FileReader(apostasFile));
        String line;
        var apostadores = new ArrayList<Apostador>();
        String[] numeros;
        int lineNum = 0;
        Apostador apostador = null;
        int[] aposta;
        while ((line = reader.readLine()) != null) {

            lineNum++;
            try {
                if (isBlanck(line)) {
                    continue;
                }
                if (isNome(line)) {
                    apostador = new Apostador(line);
                    apostadores.add(apostador);
                    continue;
                }
                numeros = line.split(" ");
                if (numeros.length != tipoJogo.quantidadeNumeros) {
                    throw new IllegalArgumentException("A aposta nao contem o total de numeros necessarios, que eh de=" + tipoJogo.quantidadeNumeros + "e foi enviado=" + numeros.length);
                }
                aposta = new int[tipoJogo.quantidadeNumeros];
                for (int i = 0; i < numeros.length; i++) {
                    aposta[i] = Integer.parseInt(numeros[i]);
                }
                Arrays.sort(aposta);
                apostador.addAposta(aposta);
            } catch (Exception e) {
                System.err.println("Falha na linha " + lineNum + " => " + line);
                e.printStackTrace();
                throw e;
            }
        }
        reader.close();
        return apostadores;
    }

    public void escreverApostas(List<int[]> apostas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(apostasFile))) {
            StringBuilder content = new StringBuilder();
            content.append("Vinicius\n");
            for (int[] aposta : apostas) {
                content.append(Utils.stringfy(aposta)).append("\n");
            }
            writer.write(content.toString());
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao manipular o arquivo de apostas.", e);
        }
    }

    public void ordernarApostas(TipoJogo tipoJogo) throws IOException {
        var apostadores = lerApostadores(tipoJogo);
        var conteudoApostas = new StringBuilder();
        for (var apostador : apostadores) {
            apostador.ordernarApostas();
            conteudoApostas.append("\n").append(apostador.nome).append("\n");
            for (var aposta : apostador.apostas) {
                conteudoApostas.append(Utils.stringfy(aposta)).append("\n");
            }
        }
        var writer = new BufferedWriter(new FileWriter(apostasFile));
        writer.write(conteudoApostas.toString());
        writer.close();
    }


    public List<int[]> lerSorteiosAnteriores(TipoJogo tipoJogo) {
        var sorteiosFile = new File("src/main/resources/" + tipoJogo + ".xlsx");
        List<int[]> jogos = new ArrayList<>();
        int line = 1;
        try (FileInputStream fis = new FileInputStream(sorteiosFile); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet
            final int shift = 2;
            for (Row row : sheet) {
                if (line++ == 1) {
                    continue;
                }
                int[] jogo = new int[tipoJogo.quantidadeNumeros];
                for (int col = 0; col < tipoJogo.quantidadeNumeros; col++) {
                    jogo[col] = (int) row.getCell(shift + col).getNumericCellValue();
                    if (tipoJogo == TipoJogo.LOTOMANIA && jogo[col] == 0) {
                        jogo[col] = 100;
                    }
                }

                line++;
                jogos.add(jogo);
            }
        } catch (IOException e) {
            logger.error(String.format("Failure on processing the line %d from the file %s", line, sorteiosFile));
        }
        return jogos;
    }

    private boolean isBlanck(String line) {
        return line == null || line.isBlank();
    }

    private boolean isNome(String line) {
        return line.trim().charAt(0) >= 'A' && line.trim().charAt(0) <= 'z';
    }
}

