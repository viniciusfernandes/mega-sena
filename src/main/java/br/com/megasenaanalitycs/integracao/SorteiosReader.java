package br.com.megasenaanalitycs.integracao;

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

public class SorteiosReader {
    private static Logger logger = LoggerFactory.getLogger(SorteiosReader.class);

    public static List<int[]> lerSorteiosAnteriores(File file) {
        List<int[]> jogos = new ArrayList<>();
        int line = 1;
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet
            for (Row row : sheet) {
                if (line++ == 1) {
                    continue;
                }
                int[] jogo = new int[6];
                jogo[0] = (int) row.getCell(2).getNumericCellValue();
                jogo[1] = (int) row.getCell(3).getNumericCellValue();
                jogo[2] = (int) row.getCell(4).getNumericCellValue();
                jogo[3] = (int) row.getCell(5).getNumericCellValue();
                jogo[4] = (int) row.getCell(6).getNumericCellValue();
                jogo[5] = (int) row.getCell(7).getNumericCellValue();
                line++;
                jogos.add(jogo);
            }
        } catch (IOException e) {
            logger.error(String.format("Failure on processing the line %d from the file %s", line, file));
        }
        return jogos;
    }

    public static List<int[]> lerApostas(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
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
}
