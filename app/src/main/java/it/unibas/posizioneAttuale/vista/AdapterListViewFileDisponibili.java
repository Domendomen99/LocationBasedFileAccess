package it.unibas.posizioneAttuale.vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.activity.ActivityMostraFileDisponibili;
import it.unibas.posizioneAttuale.activity.ActivityPrincipale;
import it.unibas.posizioneAttuale.modello.Costanti;

public class AdapterListViewFileDisponibili extends BaseAdapter {

    private List<String> listaFile;
    private List<String> listauriFile;

    public AdapterListViewFileDisponibili(List<String> listaFile, List<String> listauriFile) {
        this.listaFile = listaFile;
        this.listauriFile = listauriFile;
    }

    @Override
    public int getCount() {
        //return 1;
        return listaFile.size();
    }

    @Override
    public Object getItem(int i) {
        //return "nomeFile";
        return listaFile.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View riga;
        if(view!=null){
            riga=view;
        }else{
            LayoutInflater layoutInflater = (LayoutInflater) Applicazione.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            riga = layoutInflater.inflate(R.layout.riga_file,viewGroup,false);
        }
        String nomeFile = listaFile.get(i);
        //String nomeFile = "nomefile";
        TextView campoNomeFile = riga.findViewById(R.id.labelNomeFile);
        campoNomeFile.setText(nomeFile);
        return riga;
    }

    public void aggiornaDatiUnoAllaVolta(String nomeFile,String uriFile){
        listaFile.add(nomeFile);
        listauriFile.add(uriFile);
        List<String> listaUri = (List<String>) Applicazione.getInstance().getModello().getBean(Costanti.LISTA_URI_FILE);
        listaUri.add(uriFile);
        List<String> listaNomiFile = (List<String>) Applicazione.getInstance().getModello().getBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI);
        listaNomiFile.add(nomeFile);
        this.notifyDataSetChanged();
    }

    public void aggiornaDatiTuttiInsieme(List<String>listaNomiFile,List<String>listaUrifile){
        this.listaFile = listaNomiFile;
        this.listauriFile = listaUrifile;
        Applicazione.getInstance().getModello().putBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI,listaNomiFile);
        Applicazione.getInstance().getModello().putBean(Costanti.LISTA_URI_FILE,listaUrifile);
        this.notifyDataSetChanged();
    }
}
