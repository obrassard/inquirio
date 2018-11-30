package ca.obrassard.inquirio.activities;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.obrassard.inquirio.DrawerUtils;
import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.activities.dialogs.ThanksDialog;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.FoundRequest;
import ca.obrassard.inquirio.transfer.RequestResult;
import ca.obrassard.inquirio.transfer.StringWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemFoundActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog progressDialog ;

    private void beginLoading() {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (progressDialog == null)
        progressDialog = ProgressDialog.show(ItemFoundActivity.this,
                "Veuillez patienter",null,true);
    }

    private void endLoading() {
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    InquirioService service = RetrofitUtil.get();
    private Bitmap itemImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_found);
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

        final int itemID = getIntent().getIntExtra("item.id",0);
        beginLoading();
        service.getItemName(itemID, LoggedUser.token).enqueue(new Callback<StringWrapper>() {
            @Override
            public void onResponse(Call<StringWrapper> call, Response<StringWrapper> response) {
                endLoading();
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(ItemFoundActivity.this, response.errorBody());
                    return;
                }
                TextView txtItemName = findViewById(R.id.txt_item_name);
                txtItemName.setText(response.body().value);
            }

            @Override
            public void onFailure(Call<StringWrapper> call, Throwable t) {
                endLoading();
                ErrorUtils.showGenServError(ItemFoundActivity.this);
                finish();
            }
        });

        //Prise de photo
        findViewById(R.id.btn_add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()){
                    openBackCamera();
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
                    Snackbar.make(findViewById(android.R.id.content), "Veuillez ajouter un message", Snackbar.LENGTH_LONG).show();
                    return;
                }
                else if (itemImage == null){
                    Snackbar.make(findViewById(android.R.id.content), "Veuillez prendre une photo de l'item", Snackbar.LENGTH_LONG).show();
                    return;
                }

                FoundRequest request = new FoundRequest();
                request.message = txtmessage.getText().toString();
                request.itemID = (int) itemID;
                request.senderID = (int) LoggedUser.data.userID;

                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                itemImage.compress(Bitmap.CompressFormat.JPEG,40,bytearrayoutputstream);
                byte[] image = bytearrayoutputstream.toByteArray();
                request.image = image;

                beginLoading();
                service.sendFoundRequest(request, LoggedUser.token).enqueue(new Callback<RequestResult>() {
                    @Override
                    public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                        endLoading();
                        if (!response.isSuccessful()) {
                            ErrorUtils.showExceptionError(ItemFoundActivity.this, response.errorBody());
                            return;
                        }
                        if (response.body().result) {
                            DialogFragment dialog = new ThanksDialog();
                            dialog.show(getFragmentManager(), "ThanksDialog");
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestResult> call, Throwable t) {
                        endLoading();
                        ErrorUtils.showGenServError(ItemFoundActivity.this);}
                });
            }
        });
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ItemFoundActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            return false;
        }
        return true;
    }


    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openBackCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ErrorUtils.showGenError(ItemFoundActivity.this);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.obrassard.inquirio.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //Reception de la photo depuis l'intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File f = new File(mCurrentPhotoPath);
            itemImage = BitmapFactory.decodeFile(f.getAbsolutePath());
            ((ImageView)findViewById(R.id.imgPreview)).setImageBitmap(itemImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openBackCamera();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Inquirio nécéssite l'acces au stockage local!",Snackbar.LENGTH_LONG).show();
                }
            }
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
