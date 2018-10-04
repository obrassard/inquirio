package ca.obrassard.inquirio;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import java.nio.ByteBuffer;

import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.FoundRequest;
import ca.obrassard.inquirio.transfer.RequestResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemFoundActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InquirioService service = RetrofitUtil.getMock();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap itemImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_found);
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

        final long itemID = getIntent().getLongExtra("item.id",0);
        service.getItemName(itemID).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                TextView txtItemName = findViewById(R.id.txt_item_name);
                        txtItemName.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ItemFoundActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        //Prise de photo
        findViewById(R.id.btn_add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //Envoie de la requete
        final TextView txtmessage = findViewById(R.id.txt_message);
        Button btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (txtmessage.getText().toString().trim().equals("")){
                    Toast.makeText(ItemFoundActivity.this, "Veuillez ajouter un message", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (itemImage == null){
                    Toast.makeText(ItemFoundActivity.this, "Veuillez prendre une photo de l'item", Toast.LENGTH_SHORT).show();
                    return;
                }

                FoundRequest request = new FoundRequest();
                request.message = txtmessage.getText().toString();
                request.itemID = itemID;
                request.senderID = LoggedUser.data.userID;

                final int length = itemImage.getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(length);
                itemImage.copyPixelsToBuffer( buffer);
                byte[] image = buffer.array();
                request.image = image;

                service.sendFoundRequest(request).enqueue(new Callback<RequestResult>() {
                    @Override
                    public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                        if (response.body().result) {
                            DialogFragment dialog = new ThanksDialog();
                            dialog.show(getFragmentManager(), "ThanksDialog");
                        }
                        else {
                            Toast.makeText(ItemFoundActivity.this, "Une erreur s'est produite veuillez réésayer", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestResult> call, Throwable t) {
                        Toast.makeText(ItemFoundActivity.this, "Une erreur s'est produite veuillez réésayer", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //Reception de la photo depuis l'intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            itemImage = (Bitmap) extras.get("data");
            ((ImageView)findViewById(R.id.imgPreview)).setImageBitmap(itemImage);
        }
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
