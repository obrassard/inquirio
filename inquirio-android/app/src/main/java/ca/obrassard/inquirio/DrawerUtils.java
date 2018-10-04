package ca.obrassard.inquirio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;

public class DrawerUtils {

    public static void logout(Activity a){
        LoggedUser.data = null;
        Intent i = new Intent(a.getApplicationContext(),LoginHomeActivity.class);
        a.startActivity(i);
    }

    public static void handleDrawerClick(int id, Activity a){
        Intent intent = new Intent();
        if (id == R.id.ham_account) {
            intent = new Intent(a, AccountActivity.class);
        } else if (id == R.id.ham_logout) {
            logout(a);
            return;
        } else if (id == R.id.ham_lostitem) {
            intent = new Intent(a, AddItemActivity.class);
        } else if (id == R.id.ham_myitems) {
            intent = new Intent(a, MyItemsActivity.class);
        } else if (id == R.id.ham_mynotif) {
            intent = new Intent(a, NotificationsActivity.class);
        }
        a.startActivity(intent);

        DrawerLayout drawer = a.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public static void prepareHeader(NavigationView a){
        View header = a.getHeaderView(0);
        ((TextView)header.findViewById(R.id.drawer_uname)).setText(LoggedUser.data.userFullName);
        ((TextView)header.findViewById(R.id.drawer_phone)).setText(LoggedUser.data.userPhoneNumber);

    }

}
