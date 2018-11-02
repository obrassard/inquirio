package ca.obrassard.inquirio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.RequestResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginHomeActivity extends AppCompatActivity {

    InquirioService service = RetrofitUtil.getMock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_home);

        final EditText email = findViewById(R.id.email_field);
        Button btnNext = findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = email.getText().toString().trim();
                if (!address.equals("")){
                    LoginStepOne(address);
                }
            }
        });
    }

    public void LoginStepOne(final String email){
        service.isSubscribed(email).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                Intent intent;
                if (response.body() == null){
                    Toast.makeText(LoginHomeActivity.this, "Une erreur est survenue, veuillez réésayer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body().result){
                    intent = new Intent(LoginHomeActivity.this.getApplicationContext(), LoginActivity.class);
                } else {
                    intent = new Intent(LoginHomeActivity.this.getApplicationContext(), SignupActivity.class);
                }
                intent.putExtra("inquirio.email",email);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                Toast.makeText(LoginHomeActivity.this, "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
