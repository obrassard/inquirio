package ca.obrassard.inquirio.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.obrassard.inquirio.R;
import ca.obrassard.inquirio.errorHandling.ErrorUtils;
import ca.obrassard.inquirio.services.InquirioService;
import ca.obrassard.inquirio.services.RetrofitUtil;
import ca.obrassard.inquirio.transfer.RequestResult;
import ca.obrassard.inquirio.transfer.SubscriptionCheckRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Path;

public class LoginHomeActivity extends AppCompatActivity {

    InquirioService service = RetrofitUtil.get();

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
        SubscriptionCheckRequest scr = new SubscriptionCheckRequest();
        scr.email = email;
        service.isSubscribed(scr).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (!response.isSuccessful()) {
                    ErrorUtils.showExceptionError(LoginHomeActivity.this, response.errorBody());
                    return;
                }

                Intent intent;
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
                ErrorUtils.showGenServError(LoginHomeActivity.this);
            }
        });
    }
}
