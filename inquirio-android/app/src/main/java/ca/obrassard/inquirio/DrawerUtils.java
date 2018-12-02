package ca.obrassard.inquirio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ca.obrassard.inquirio.activities.AccountActivity;
import ca.obrassard.inquirio.activities.AddItemActivity;
import ca.obrassard.inquirio.activities.LoginHomeActivity;
import ca.obrassard.inquirio.activities.MyItemsActivity;
import ca.obrassard.inquirio.activities.NotificationsActivity;
import ca.obrassard.inquirio.activities.SignupActivity;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LogoutResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrawerUtils {

    public static void logout(final Activity a){
        final ProgressDialog progressDialog = ProgressDialog.show(a,
                "Veuillez patienter",null,true);
        RetrofitUtil.get().logout().enqueue(new Callback<LogoutResponse>() {

            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {

                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(a, response.errorBody());
                    progressDialog.dismiss();
                    return;
                }
                LogoutResponse lr = response.body();
                if (lr.success){
                    LoggedUser.data = null;
                    Intent i = new Intent(a.getApplicationContext(),LoginHomeActivity.class);
                    a.startActivity(i);
                    Toast.makeText(a, "Vous avez été déconnecté", Toast.LENGTH_LONG).show();
                    a.finishAffinity();
                    ActivityCompat.finishAffinity(a);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(a, lr.message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showGenServError(a);
            }
        });
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
