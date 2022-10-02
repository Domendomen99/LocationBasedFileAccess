package it.unibas.posizioneAttuale.Controllo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.activity.ActivityPrincipale;
import it.unibas.posizioneAttuale.modello.Costanti;
import it.unibas.posizioneAttuale.vista.VistaPrincipale;

public class ControlloVistaPrincipale {

    private String TAG = ControlloVistaPrincipale.class.getSimpleName();

    private View.OnClickListener azioneAccediAFileDisponibili = new AzioneAccediAFileDisponibili();



    public View.OnClickListener getAzioneAccediAFileDisponibili() {
        return azioneAccediAFileDisponibili;
    }


    private class AzioneAccediAFileDisponibili implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "Eseguo Azione Accedi A File Disponibili");
            ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
            activityPrincipale.mostraActivityFileDisponibili();
        }
    }




}
