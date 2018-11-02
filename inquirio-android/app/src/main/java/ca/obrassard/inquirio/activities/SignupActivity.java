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
import ca.obrassard.inquirio.transfer.LoginResponse;
import ca.obrassard.inquirio.transfer.SignupRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {


    InquirioService service = RetrofitUtil.get();

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
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(SignupActivity.this, response.errorBody());
                    return;
                }

                LoginResponse loginResponse = response.body();

                if (loginResponse.result){
                    LoggedUser.data = loginResponse;
                    Intent intent = new Intent(SignupActivity.this.getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    SignupActivity.this.finishAffinity();
                    ActivityCompat.finishAffinity(SignupActivity.this);

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                ErrorUtils.showGenServError(SignupActivity.this);
            }
        });
    }
}

