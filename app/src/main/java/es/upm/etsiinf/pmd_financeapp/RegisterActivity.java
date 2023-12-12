package es.upm.etsiinf.pmd_financeapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextNombre, editTextEmail, editTextPassw;
    Button buttonRegistrar, buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //Si ya esta logeado, abrimos la actividad principal
            Intent intent = new Intent(RegisterActivity.this, es.upm.etsiinf.pmd_financeapp.MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextNombre = findViewById(R.id.registrar_txt_name);
        editTextEmail = findViewById(R.id.registrar_txt_email);
        editTextPassw = findViewById(R.id.registrar_txt_password);

        buttonLogin = findViewById(R.id.registrar_btn_login);
        buttonRegistrar = findViewById(R.id.registrar_btn_registrar);
        progressBar = findViewById(R.id.registrar_progressBar);

        mAuth = FirebaseAuth.getInstance();

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassw.getText().toString().trim();
                Log.i("RegisterActivity", "Nombre: " + nombre + " Email: " + email + " Password: " + password);

                if(TextUtils.isEmpty(nombre)){
                    editTextNombre.setError("El nombre es obligatorio");
                    editTextNombre.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    editTextEmail.setError("El email es obligatorio");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextPassw.setError("La contraseña es obligatoria");
                    return;
                }
                
                progressBar.setVisibility(View.VISIBLE);
                registrarUser(nombre, email, password);

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registrarUser(String nombre, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("RegisterActivity", "createUserWithEmail:onComplete:" + task.isSuccessful());
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(RegisterActivity.this, "Registro correcto",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Si se ha logeado correctamente, abrimos la actividad principal
                    Intent intent = new Intent(RegisterActivity.this, es.upm.etsiinf.pmd_financeapp.MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(RegisterActivity.this, "El registro ha fallado",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}