package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.FinderContactDetail;
import ca.obrassard.inquirio.transfer.Notification;
import ca.obrassard.inquirio.transfer.RequestResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationDetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.get();
    String telephone = "";

    ProgressDialog progressDialog ;

    private void beginLoading() {
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(NotificationDetailsActivity.this,
                "Veuillez patienter",null,true);
    }

    private void endLoading() {
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean deviceIsLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        if (deviceIsLandscape){
            startActivity(new Intent(NotificationDetailsActivity.this, MainActivity.class ));
            finish();
        }

        //region [Initialisation des éléments graphiques]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerUtils.prepareHeader(navigationView);
        //endregion

        final TextView txtDate = findViewById(R.id.txt_date);
        final TextView txtUser = findViewById(R.id.txt_username);
        final TextView txtItem = findViewById(R.id.txt_itemtitle);
        final ImageView img = findViewById(R.id.img_notif);
        final TextView txtDesc = findViewById(R.id.txtMessage);
        final Button btnOui = findViewById(R.id.btn_yes);
        Button btnNon = findViewById(R.id.btn_no);
        Button btnContact = findViewById(R.id.btn_contactFinder);

        final int notifID = (int)getIntent().getLongExtra("notification.id",1);

        beginLoading();
        service.getNotificationDetail(notifID, LoggedUser.token).enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                endLoading();
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(NotificationDetailsActivity.this, response.errorBody());
                    return;
                }

                Notification notification = response.body();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
                txtDate.setText(getString(R.string.date, simpleDateFormat.format(notification.date)));
                txtUser.setText(getString(R.string.user_and_rating, notification.senderName, notification.senderRating));
                txtItem.setText(notification.itemName);
                txtDesc.setText(notification.message);
                //TODO Obtenir l'image
                Bitmap bitmap = BitmapFactory.decodeByteArray(notification.photo, 0, notification.photo.length);
                img.setImageBitmap(bitmap);
                setTitle(notification.senderName);
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                endLoading();
                ErrorUtils.showGenServError(NotificationDetailsActivity.this);
                NotificationDetailsActivity.this.finish();
            }
        });

        btnNon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginLoading();
                service.denyCandidateNotification(notifID, LoggedUser.token).enqueue(new Callback<RequestResult>() {
                    @Override
                    public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                        endLoading();
                        if (!response.isSuccessful()) {
                            ErrorUtils.showExceptionError(NotificationDetailsActivity.this, response.errorBody());
                            return;
                        }
                        if (response.body().result){
                            NotificationDetailsActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestResult> call, Throwable t) {
                        endLoading();
                        ErrorUtils.showGenServError(NotificationDetailsActivity.this);
                    }
                });
            }
        });

        btnOui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginLoading();
                service.acceptCandidateNotification((int) notifID, LoggedUser.token).enqueue(new Callback<FinderContactDetail>() {
                    @Override
                    public void onResponse(Call<FinderContactDetail> call, Response<FinderContactDetail> response) {
                        endLoading();
                        if (!response.isSuccessful()) {
                            ErrorUtils.showExceptionError(NotificationDetailsActivity.this, response.errorBody());
                            return;
                        }
                        FinderContactDetail finderContactDetail = response.body();
                        findViewById(R.id.ll_btn_yn).setVisibility(View.GONE);
                        findViewById(R.id.ll_btn_contact).setVisibility(View.VISIBLE);
                        telephone = finderContactDetail.phoneNumber;
                    }

                    @Override
                    public void onFailure(Call<FinderContactDetail> call, Throwable t) {
                        endLoading();
                        ErrorUtils.showGenServError(NotificationDetailsActivity.this);
                    }
                });
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!telephone.trim().equals("")){
                sendSMS(telephone);
            }
            }
        });
    }

    public void sendSMS(String number)
    {
        beginLoading();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
        endLoading();
    }

    //region [Evennement du tirroir]
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerUtils.handleDrawerClick(id,this);
        return true;
    }

    //Gestion de la fermeture du tiroir
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //endregion
}
