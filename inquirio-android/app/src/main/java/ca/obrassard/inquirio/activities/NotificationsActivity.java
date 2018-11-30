package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.activities.adapters.NotificationAdapter;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.FinderContactDetail;
import ca.obrassard.inquirio.transfer.Notification;
import ca.obrassard.inquirio.transfer.NotificationSummary;
import ca.obrassard.inquirio.transfer.RequestResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean deviceIsLandscape;
    InquirioService service = RetrofitUtil.get();
    NotificationAdapter m_adapter;
    String telephone = "";
    TextView txtDate;
    TextView txtUser;
    TextView txtItem;
    ImageView img;
    TextView txtDesc ;
    Button btnOui;
    Button btnNon;
    Button btnContact;
    ListView lvNearItems;
    long selectedNotifId = -1;

    ScrollView landscapeDetails;
    LinearLayout landscapeEmpty;

    ProgressDialog progressDialog ;

    private void beginLoading() {
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(NotificationsActivity.this,
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
        deviceIsLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerUtils.prepareHeader(navigationView);
        //endregion
        //Initialisation du ListView
        lvNearItems = findViewById(R.id.lv_notifications);
        m_adapter = new NotificationAdapter(this);
        lvNearItems.setAdapter(m_adapter);
        lvNearItems.setEmptyView(findViewById(R.id.no_notif_ph));

        if (deviceIsLandscape){
            landscapeDetails = findViewById(R.id.landscape_detail);
            landscapeEmpty = findViewById(R.id.landscape_detail_empty);

            txtDate = findViewById(R.id.txt_date);
            txtUser = findViewById(R.id.txt_username);
            txtItem = findViewById(R.id.txt_itemtitle);
            img = findViewById(R.id.img_notif);
            txtDesc = findViewById(R.id.txtMessage);
            btnOui = findViewById(R.id.btn_yes);
            btnNon = findViewById(R.id.btn_no);
            btnContact = findViewById(R.id.btn_contactFinder);
            btnNon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginLoading();
                    service.denyCandidateNotification((int)selectedNotifId, LoggedUser.token).enqueue(new Callback<RequestResult>() {
                        @Override
                        public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(NotificationsActivity.this, response.errorBody());
                                return;
                            }
                            if (response.body().result){
                                landscapeDetails.setVisibility(View.GONE);
                                landscapeEmpty.setVisibility(View.VISIBLE);
                                selectedNotifId = -1;
                                refreshlist();
                            }
                        }

                        @Override
                        public void onFailure(Call<RequestResult> call, Throwable t) {
                            endLoading();
                            ErrorUtils.showGenServError(NotificationsActivity.this);
                        }
                    });
                }
            });

            btnOui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginLoading();
                    service.acceptCandidateNotification((int) selectedNotifId, LoggedUser.token).enqueue(new Callback<FinderContactDetail>() {
                        @Override
                        public void onResponse(Call<FinderContactDetail> call, Response<FinderContactDetail> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(NotificationsActivity.this, response.errorBody());
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
                            ErrorUtils.showGenServError(NotificationsActivity.this);
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

        lvNearItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationSummary summary = m_adapter.getItem(position);
                if (summary == null){
                    Toast.makeText(NotificationsActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedNotifId = summary.notificationID;

                if (deviceIsLandscape){


                    landscapeDetails.setVisibility(View.VISIBLE);
                    landscapeEmpty.setVisibility(View.GONE);
                    beginLoading();
                    service.getNotificationDetail((int)selectedNotifId, LoggedUser.token).enqueue(new Callback<Notification>() {
                        @Override
                        public void onResponse(Call<Notification> call, Response<Notification> response) {
                            endLoading();
                            if (!response.isSuccessful()) {
                                ErrorUtils.showExceptionError(NotificationsActivity.this, response.errorBody());
                                return;
                            }

                            Notification notification = response.body();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
                            txtDate.setText(getString(R.string.date, simpleDateFormat.format(notification.date)));
                            txtUser.setText(getString(R.string.user_and_rating, notification.senderName, notification.senderRating));
                            txtItem.setText(notification.itemName);
                            txtDesc.setText(notification.message);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(notification.photo, 0, notification.photo.length);
                            img.setImageBitmap(bitmap);
                            setTitle(notification.senderName);
                        }

                        @Override
                        public void onFailure(Call<Notification> call, Throwable t) {
                            endLoading();
                            ErrorUtils.showGenServError(NotificationsActivity.this);
                        }
                    });
                } else {
                    Intent i = new Intent(NotificationsActivity.this.getApplicationContext(),NotificationDetailsActivity.class);
                    i.putExtra("notification.id", summary.notificationID);
                    startActivity(i);
                }
            }
        });
    }

    public void refreshlist(){
        beginLoading();
        service.getPotentiallyFoundItems(LoggedUser.data.userID, LoggedUser.token).enqueue(new Callback<List<NotificationSummary>>() {
            @Override
            public void onResponse(Call<List<NotificationSummary>> call, Response<List<NotificationSummary>> response) {
                endLoading();
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(NotificationsActivity.this, response.errorBody());
                    return;
                }
                List<NotificationSummary> list = response.body();
                m_adapter.clear();
                m_adapter.addAll(list);
                m_adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<NotificationSummary>> call, Throwable t) {
                endLoading();
                ErrorUtils.showGenServError(NotificationsActivity.this);
            }
        });
    }


    public void sendSMS(String number)
    {
        beginLoading();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
        endLoading();
    }

    @Override
    protected void onResume() {
        if (deviceIsLandscape){
            landscapeDetails.setVisibility(View.GONE);
            landscapeEmpty.setVisibility(View.VISIBLE);
        }
        super.onResume();
        refreshlist();
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
