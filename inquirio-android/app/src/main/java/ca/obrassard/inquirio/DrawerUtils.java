package ca.obrassard.inquirio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class DrawerUtils {

    public static void logout(Activity a){
        LoggedUser.data = null;
        Intent i = new Intent(a.getApplicationContext(),LoginHomeActivity.class);
        a.startActivity(i);
    }

    public static void prepareDrawer(){

    }

}
