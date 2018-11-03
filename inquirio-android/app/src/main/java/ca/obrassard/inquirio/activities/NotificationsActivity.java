package ca.obrassard.inquirio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.activities.adapters.NotificationAdapter;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.NotificationSummary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.get();
    NotificationAdapter m_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        ListView lvNearItems = findViewById(R.id.lv_notifications);
        m_adapter = new NotificationAdapter(this);
        lvNearItems.setAdapter(m_adapter);
        lvNearItems.setEmptyView(findViewById(R.id.no_notif_ph));

        lvNearItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationSummary summary = m_adapter.getItem(position);
                if (summary == null){
                    Toast.makeText(NotificationsActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(NotificationsActivity.this.getApplicationContext(),NotificationDetailsActivity.class);
                i.putExtra("notification.id", summary.notificationID);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        service.getPotentiallyFoundItems(LoggedUser.data.userID, LoggedUser.token).enqueue(new Callback<List<NotificationSummary>>() {
            @Override
            public void onResponse(Call<List<NotificationSummary>> call, Response<List<NotificationSummary>> response) {
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
                ErrorUtils.showGenServError(NotificationsActivity.this);
            }
        });
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
