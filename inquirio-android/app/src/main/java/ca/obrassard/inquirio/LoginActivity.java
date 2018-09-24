package ca.obrassard.inquirio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    InquirioService service = RetrofitUtil.getMock();
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

        //Authenticate and retrieve userID
        service.login(email,passwd).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (loginResponse.result){
                    LoggedUserData.data = loginResponse;
                    Intent intent = new Intent(LoginActivity.this.getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    //Erreur d'authentification
                    Toast.makeText(LoginActivity.this, "Le nom d'utilisateur ou mot de passe est incorrect", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
