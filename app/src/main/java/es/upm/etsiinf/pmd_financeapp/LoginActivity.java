package es.upm.etsiinf.pmd_financeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    public static final String FILE_NAME = "login";
    public static final String REMEMBER_ME_KEY = "remember_me";

    public static final String SHARED_PREFS = "sharedPrefs";

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
        progressBar = findViewById(R.id.login_progressBar);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");

        editTextEmail.setText(email);
        //editTextPassw.setText(password);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString();
                String password = editTextPassw.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("LoginActivity", "signInWithEmail:onComplete:" + task.isSuccessful());
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login correcto",
                                            Toast.LENGTH_SHORT).show();
                                    //Si se ha logeado correctamente, abrimos la actividad principal
                                    Intent intent = new Intent(LoginActivity.this, es.upm.etsiinf.pmd_financeapp.MainActivity.class);
                                    startActivity(intent);
                                    //finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Email/Contraseña incorrecta",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}