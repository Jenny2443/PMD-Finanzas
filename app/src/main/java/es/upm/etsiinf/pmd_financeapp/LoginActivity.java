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

    CheckBox checkBox;
    private static final String FILE_NAME = "login";
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

        checkBox = findViewById(R.id.login_checkBox);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");

        editTextEmail.setText(email);
        editTextPassw.setText(password);
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
                                    //Si el usuario ha marcado el checkbox, guardamos el email y la contraseña
                                    if(checkBox.isChecked()){
                                        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
                                        editor.putString("email", email);
                                        editor.putString("password", password);
                                        editor.apply();
                                    }

                                    Toast.makeText(LoginActivity.this, "Login correcto",
                                            Toast.LENGTH_SHORT).show();
                                    //Si se ha logeado correctamente, abrimos la actividad principal
                                    Intent intent = new Intent(LoginActivity.this, es.upm.etsiinf.pmd_financeapp.MainActivity.class);
                                    intent.putExtra("checkbox",true);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "El login ha fallado",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}