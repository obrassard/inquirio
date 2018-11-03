package ca.obrassard.inquirio.errorHandling;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.google.gson.Gson;

import java.io.IOException;

import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.activities.LoginHomeActivity;
import ca.obrassard.inquirio.errorHandling.APIRequestException.ApiRequestExceptionResponse;
import okhttp3.ResponseBody;

public class ErrorUtils {
    public static void showGenError(Activity a){
        Snackbar.make(a.findViewById(android.R.id.content), R.string.genericerror,Snackbar.LENGTH_LONG).show();
    }

    public static void showGenServError(Activity a){
        Snackbar.make(a.findViewById(android.R.id.content), R.string.genericconnexionerror,Snackbar.LENGTH_LONG).show();
    }

    public static void showLocationError(Activity a){
        //Snackbar.make(a.findViewById(android.R.id.content), "Impossible d'accéder à votre localisation !",Snackbar.LENGTH_LONG).show();
        Snackbar.make(a.findViewById(android.R.id.content), "Vérifiez que votre service de localisation est activé!",Snackbar.LENGTH_LONG).show();
    }
    public static void showLocationPermitionError(Activity a){
        Snackbar.make(a.findViewById(android.R.id.content), "Veuillez autoriser inquirio à accéder à votre emplacement!",Snackbar.LENGTH_LONG).show();
    }

    public static void showExceptionError(Activity a, ResponseBody errorBody){
        if (errorBody == null){
            ErrorUtils.showGenError(a);
            return;
        }
        try {
            Gson gson = new Gson();
            ApiRequestExceptionResponse ex = gson.fromJson(errorBody.string(),ApiRequestExceptionResponse.class);
            String key = ex.error.toString();
            if (ex.targetAttribute != null){
                key = key + "_" + ex.targetAttribute;
            }
            int ressourceid = a.getResources().getIdentifier(key, "string", a.getPackageName());
            Snackbar.make(a.findViewById(android.R.id.content), ressourceid,Snackbar.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
