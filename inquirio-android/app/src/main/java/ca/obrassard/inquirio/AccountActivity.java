package ca.obrassard.inquirio;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import ca.obrassard.inquirio.model.User;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.getMock();
    TextView txtName;
    TextView txtEmail;
    RatingBar ratingBar;
    TextView nbItemTFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //endregion

        txtEmail = findViewById(R.id.email);
        txtName = findViewById(R.id.name);
        ratingBar = findViewById(R.id.user_rating);
        nbItemTFound = findViewById(R.id.nbidtem);

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        service.getUserDetail(LoggedUserData.data.userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u = response.body();
                txtEmail.setText(u.email);
                txtName.setText(u.fullname);
                ratingBar.setRating((float)u.rating);
                nbItemTFound.setText(getString(R.string.nbitems, String.valueOf(u.itemsFound)));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AccountActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout (){
        Intent i = new Intent(this,LoginHomeActivity.class);
        startActivity(i);
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
