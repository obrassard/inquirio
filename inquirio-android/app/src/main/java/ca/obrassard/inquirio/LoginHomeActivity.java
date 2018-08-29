package ca.obrassard.inquirio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginHomeActivity extends AppCompatActivity {

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

    public void LoginStepOne(String email){
        //Verification de l'existence du email

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("inquirio.email",email);
        startActivity(intent);
    }
}
