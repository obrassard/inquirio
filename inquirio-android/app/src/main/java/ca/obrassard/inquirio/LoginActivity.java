package ca.obrassard.inquirio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

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

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
