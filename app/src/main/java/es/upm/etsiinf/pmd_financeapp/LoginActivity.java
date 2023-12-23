package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassw;
    Button buttonLogin, buttonRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    //private static final String FILE_NAME = "login";
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Si ya esta logeado, abrimos la actividad principal
            Intent intent = new Intent(LoginActivity.this, es.upm.etsiinf.pmd_financeapp.MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.login_txt_email);
        editTextPassw = findViewById(R.id.login_txt_password);

        buttonLogin = findViewById(R.id.login_btn_login);
        buttonRegister = findViewById(R.id.login_btn_register);
        progressBar = findViewById(R.id.login_progressBar);
        mAuth = FirebaseAuth.getInstance();

        //SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        //String email = prefs.getString("email", "");
        //String password = prefs.getString("password", "");
//
//        editTextEmail.setText(email);
//        editTextPassw.setText(password);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassw.getText());
                //String email = editTextEmail.getText().toString();
                //String password = editTextPassw.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                    //editTextEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    //editTextPassw.setError("Password is required");
                    return;
                }

//                mAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Log.i("LoginActivity", "signInWithEmail:onComplete:" + task.isSuccessful());
//                                progressBar.setVisibility(View.GONE);
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(LoginActivity.this, "Login correcto",
//                                            Toast.LENGTH_SHORT).show();
//                                    //Si se ha logeado correctamente, abrimos la actividad principal
//                                    Intent intent = new Intent(LoginActivity.this, es.upm.etsiinf.pmd_financeapp.MainActivity.class);
//                                    intent.putExtra("checkbox",true);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Toast.makeText(LoginActivity.this, "El login ha fallado",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}