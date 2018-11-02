package ca.obrassard.inquirio.activities;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.obrassard.inquirio.LoggedUser;
import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LoginResponse;
import ca.obrassard.inquirio.transfer.SignupRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {


    InquirioService service = RetrofitUtil.getMock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();
        String email = intent.getStringExtra("inquirio.email");

        final EditText emailfield = findViewById(R.id.email_field);
        emailfield.setText(email);

        final EditText telephoneField = findViewById(R.id.cellnum);
        final EditText completeNameField = findViewById(R.id.fullname);
        final EditText passwordField = findViewById(R.id.passwd_field);
        final EditText confirmationField = findViewById(R.id.passwd_conf);

        telephoneField.requestFocus();

        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupRequest sr = new SignupRequest();
                sr.cellNumber = telephoneField.getText().toString();
                sr.fullName = completeNameField.getText().toString();
                sr.email = emailfield.getText().toString();
                sr.password = passwordField.getText().toString();
                sr.passwdConfirmation = confirmationField.getText().toString();
                signup(sr);
            }
        });

    }

    private void signup(SignupRequest request){
        service.signup(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (loginResponse == null){
                    Toast.makeText(SignupActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (loginResponse.result){
                    LoggedUser.data = loginResponse;
                    Intent intent = new Intent(SignupActivity.this.getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    SignupActivity.this.finishAffinity();
                    ActivityCompat.finishAffinity(SignupActivity.this);

                } else {
                    //Ereur d'authentification
                    Toast.makeText(SignupActivity.this, "Impossible de vous connecter, veuillez réésayer.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

