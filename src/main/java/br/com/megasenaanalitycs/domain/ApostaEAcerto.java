package br.com.megasenaanalitycs.domain;

import java.util.List;

public class ApostaEAcerto {
    public final int[] aposta;
    public final List<Integer> acerto;

    ApostaEAcerto(int[] aposta, List<Integer> acerto) {
        this.aposta = aposta;
        this.acerto = acerto;
    }

    public int totalAcerto() {
        return acerto == null ? 0 : acerto.size();
    }
}
