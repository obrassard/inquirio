package ca.obrassard.inquirio;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.Notification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationDetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.getMock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        //endregion

        final TextView txtDate = findViewById(R.id.txt_date);
        final TextView txtUser = findViewById(R.id.txt_username);
        final TextView txtItem = findViewById(R.id.txt_itemtitle);
        ImageView img = findViewById(R.id.img_notif);
        final TextView txtDesc = findViewById(R.id.txtMessage);
        Button btnOui = findViewById(R.id.btn_yes);
        Button btnNon = findViewById(R.id.btn_no);
        Button btnContact = findViewById(R.id.btn_contactFinder);

        long notifID = getIntent().getLongExtra("notification.id",1);

        service.getNotificationDetail(notifID).enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                Notification notification = response.body();
                txtDate.setText(getString(R.string.date, notification.date));
                txtUser.setText(notification.senderName);
                txtItem.setText(notification.itemName);
                txtDesc.setText(notification.message);
                //TODO Obtenir l'image
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(NotificationDetailsActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                NotificationDetailsActivity.this.finish();
            }
        });







        //sendSMS(12345678);
    }

    public void sendSMS(String number)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }

    //region [Evennement du tirroir]
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = new Intent();

        if (id == R.id.ham_account) {
            intent = new Intent(this, AccountActivity.class);
        } else if (id == R.id.ham_logout) {
            return false;
        } else if (id == R.id.ham_lostitem) {
            intent = new Intent(this, AddItemActivity.class);
        } else if (id == R.id.ham_myitems) {
            intent = new Intent(this, MyItemsActivity.class);
        } else if (id == R.id.ham_mynotif) {
            intent = new Intent(this, NotificationsActivity.class);
        }
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
