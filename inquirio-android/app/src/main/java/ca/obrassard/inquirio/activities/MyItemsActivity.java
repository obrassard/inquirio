package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.activities.adapters.MyFoundItemAdapter;
import ca.obrassard.inquirio.activities.adapters.MyLostItemAdapter;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.FoundItemSummary;
import ca.obrassard.inquirio.transfer.LostItemSummary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyItemsActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    MyLostItemAdapter m_adapterLostItems;
    MyFoundItemAdapter m_adapterFoundItems;
    InquirioService service = RetrofitUtil.get();

    ProgressDialog progressDialog ;

    private void beginLoading() {
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(MyItemsActivity.this,
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
        //region [Initialisation des éléments de navigation]
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchILostSomething();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerUtils.prepareHeader(navigationView);
        //endregion

        //Initialisation des listes
        ListView lvMyLostItems = findViewById(R.id.lv_mylostitems);
        m_adapterLostItems = new MyLostItemAdapter(this);
        lvMyLostItems.setAdapter(m_adapterLostItems);
        lvMyLostItems.setEmptyView(findViewById(R.id.noLostItems));

        ListView lvMyFoundItem = findViewById(R.id.lv_myitemsfound);
        m_adapterFoundItems = new MyFoundItemAdapter(this);
        lvMyFoundItem.setAdapter(m_adapterFoundItems);
        lvMyFoundItem.setEmptyView(findViewById(R.id.noFoundItems));


        lvMyFoundItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FoundItemSummary selecteditem = m_adapterFoundItems.getItem(position);
                Intent i = new Intent(MyItemsActivity.this.getApplicationContext(),ItemsDetailActivity.class);
                i.putExtra("item.id",selecteditem.itemID);
                i.putExtra("killOnLandscape",false);
                startActivity(i);
            }
        });

        lvMyLostItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LostItemSummary selecteditem = m_adapterLostItems.getItem(position);
                Intent i = new Intent(MyItemsActivity.this.getApplicationContext(),ItemsDetailActivity.class);
                i.putExtra("item.id",selecteditem.itemID);
                i.putExtra("killOnLandscape",false);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        beginLoading();
        service.getLostItemsByOwner(LoggedUser.data.userID).enqueue(new Callback<List<LostItemSummary>>() {
            @Override
            public void onResponse(Call<List<LostItemSummary>> call, Response<List<LostItemSummary>> response) {
                //endLoading();
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(MyItemsActivity.this, response.errorBody());
                    return;
                }
                List<LostItemSummary> li = response.body();

                m_adapterLostItems.clear();
                m_adapterLostItems.addAll(li);
                m_adapterLostItems.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<LostItemSummary>> call, Throwable t) {
                //endLoading();
                ErrorUtils.showGenServError(MyItemsActivity.this);
            }
        });


        service.getFoundItemsByOwner(LoggedUser.data.userID).enqueue(new Callback<List<FoundItemSummary>>() {
            @Override
            public void onResponse(Call<List<FoundItemSummary>> call, Response<List<FoundItemSummary>> response) {
                endLoading();
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(MyItemsActivity.this, response.errorBody());
                    return;
                }

                List<FoundItemSummary> li = response.body();

                m_adapterFoundItems.clear();
                m_adapterFoundItems.addAll(li);
                m_adapterFoundItems.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<FoundItemSummary>> call, Throwable t) {
                endLoading();
                ErrorUtils.showGenServError(MyItemsActivity.this);
            }
        });
    }

    private void LaunchILostSomething(){
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endLoading();
    }
}
