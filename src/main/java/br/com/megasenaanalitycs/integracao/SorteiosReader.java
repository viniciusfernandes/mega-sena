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

    public static List<int[]> lerSorteiosAnteriores(TipoJogo tipoJogo, File file) {
        List<int[]> jogos = new ArrayList<>();
        int line = 1;
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
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
            logger.error(String.format("Failure on processing the line %d from the file %s", line, file));
        }
        return jogos;
    }

}

