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
        service.isSubscribed(email).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Intent intent = new Intent();
                if (response.body()){
                    intent = new Intent(LoginHomeActivity.this.getApplicationContext(), LoginActivity.class);
                } else {
                    intent = new Intent(LoginHomeActivity.this.getApplicationContext(), SignupActivity.class);
                }
                intent.putExtra("inquirio.email",email);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(LoginHomeActivity.this, "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
