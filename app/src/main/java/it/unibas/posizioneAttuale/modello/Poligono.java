package it.unibas.posizioneAttuale.modello;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Poligono {

    private String nomeAula;
    private List<LatLng> listaPunti;

    public Poligono(String nome, List<LatLng> listaPunti) {
        this.nomeAula = nome;
        this.listaPunti = listaPunti;
    }

    public Poligono() {

    }

    public String getNomeAula() {
        return nomeAula;
    }

    public void setNomeAula(String nomeAula) {
        this.nomeAula = nomeAula;
    }

    public List<LatLng> getListaPunti() {
        return listaPunti;
    }

    public void setListaPunti(List<LatLng> listaPunti) {
        this.listaPunti = listaPunti;
    }

    @Override
    public String toString() {
        return "Poligono{" +
                "nomeAula='" + nomeAula + '\'' +
                ", listaPunti=" + listaPunti +
                '}';
    }
}
