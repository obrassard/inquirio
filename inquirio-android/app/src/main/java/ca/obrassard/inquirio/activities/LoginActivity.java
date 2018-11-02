package ca.obrassard.inquirio.activities;

import android.annotation.SuppressLint;
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
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LoginRequest;
import ca.obrassard.inquirio.transfer.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    InquirioService service = RetrofitUtil.get();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String email = intent.getStringExtra("inquirio.email");

        final EditText emailfield = findViewById(R.id.email_field);
        emailfield.setText(email);
        final EditText passwordField = findViewById(R.id.passwd_field);
        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = emailfield.getText().toString().trim();
                String passwd = passwordField.getText().toString();
                if (!passwd.equals("") && !loginEmail.equals("")){
                    login(loginEmail,passwd);
                }
            }
        });
        passwordField.requestFocus();
    }

    public void login(String email, String passwd){

        LoginRequest lr = new LoginRequest();
        lr.email = email;
        lr.password = passwd;
        //Authenticate and retrieve userID
        service.login(lr).enqueue(new Callback<LoginResponse>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(LoginActivity.this, response.errorBody());
                    return;
                }

                LoginResponse loginResponse = response.body();

                if (loginResponse.result){
                    LoggedUser.data = loginResponse;
                    //TODO : RÉcupérer le token dans le loginresponse ici
                    LoggedUser.token = loginResponse.userID;
                    ///////////

                    Intent intent = new Intent(LoginActivity.this.getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finishAffinity();
                    ActivityCompat.finishAffinity(LoginActivity.this);
                } else {
                    //Erreur d'authentification
                    ErrorUtils.showGenServError(LoginActivity.this);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
