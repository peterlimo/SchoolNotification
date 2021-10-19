package com.supreme.supremedevs.schoolnotification.Login;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.supreme.supremedevs.schoolnotification.MainActivity;
import com.supreme.supremedevs.schoolnotification.R;
import com.supreme.supremedevs.schoolnotification.Utils.Constants;


public class signup extends AppCompatActivity {
    EditText password,cpassword,emails;
    TextView txt_alreadyHave;
    Button btn_registrar;

    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String email;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            email = extras == null ? "" : extras.getString(Constants.TAG_EMAIL);
        } else {
            email = savedInstanceState.getString(Constants.TAG_EMAIL);
        }

        emails =  findViewById(R.id.email);
        password =  findViewById(R.id.password);
        loginProgress = findViewById(R.id.login_progress);
        emails.setText(email);
        cpassword =  findViewById(R.id.cpassword);
        txt_alreadyHave =  findViewById(R.id.txt_already_have);
        txt_alreadyHave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, login.class));
                finish();
            }
        });

        btn_registrar =  findViewById(R.id.btn_register);
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreate();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptCreate() {
        // Store values at the time of the login attempt.
        email = emails.getText().toString();
        String passwords = password.getText().toString();
        String cpasswords = cpassword.getText().toString();

        boolean cancel = false;
        View focusView = null;



        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emails.setError("empty email");
            focusView = emails;
            cancel = true;
        } else if (TextUtils.isEmpty(passwords)) {
            password.setError("empty password");
            focusView =password;
            cancel = true;

        } else if (TextUtils.isEmpty(passwords)) {
            password.setError("field required");
            focusView = password;
            cancel = true;
        }
        else if (!(passwords.equals(cpasswords))) {
            cpassword.setError("passowrd not same");
            focusView = cpassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            loginProgress.setVisibility(View.VISIBLE);

            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Intent setupIntent = new Intent(signup.this, SetupActivity.class);
                        startActivity(setupIntent);
                        finish();
                    } else {
                        loginProgress.setVisibility(View.INVISIBLE);

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(signup.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(signup.this, login.class));
        finish();
    }


}