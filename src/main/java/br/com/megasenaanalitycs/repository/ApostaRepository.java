package br.com.megasenaanalitycs.repository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApostaRepository {
    private static final Logger logger = LoggerFactory.getLogger(ApostaRepository.class);

    public List<Aposta> lerApostas(TipoJogo tipoJogo) throws IOException {
        var apostasFile = new File("src/main/resources/apostas.txt");
        var reader = new BufferedReader(new FileReader(apostasFile));
        String line;
        var apostas = new ArrayList<Aposta>();
        String[] numeros;
        int lineNum = 0;
        Aposta aposta;
        String nome = null;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            try {
                if (isBlanck(line)) {
                    continue;
                }

                if (isNome(line)) {
                    nome = line;
                    continue;
                }
                aposta = new Aposta(tipoJogo.numeros);
                aposta.apostador = nome;
                numeros = line.split(" ");
                aposta.numeros = new int[tipoJogo.numeros];
                for (int i = 0; i < numeros.length; i++) {
                    aposta.numeros[i] = Integer.parseInt(numeros[i]);
                }
                Arrays.sort(aposta.numeros);
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
                int[] jogo = new int[tipoJogo.numeros];
                for (int col = 0; col < tipoJogo.numeros; col++) {
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
        return line.matches("[A-Z]*[a-z]+");
    }
}

