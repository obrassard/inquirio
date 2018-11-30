package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class LoginHomeActivity extends AppCompatActivity {

    InquirioService service = RetrofitUtil.get();
    ProgressDialog progressDialog ;

    private void beginLoading() {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (progressDialog == null)
        progressDialog = ProgressDialog.show(LoginHomeActivity.this,
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
        beginLoading();
        service.isSubscribed(scr).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                endLoading();
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
                endLoading();
                ErrorUtils.showGenServError(LoginHomeActivity.this);
            }
        });
    }
}
