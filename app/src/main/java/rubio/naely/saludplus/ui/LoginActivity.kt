package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.MainActivity
import rubio.naely.saludplus.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Vincular vistas
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)

        //Click en login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                         val uid = auth.currentUser?.uid
                        if (uid != null) {
                            db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener { document ->
                                    val rol = document.getString("rol")
                                    when (rol) {
                                        "Paciente" -> {
                                            startActivity(Intent(this, PacienteHomeActivity::class.java))
                                            finish()
                                        }
                                        "Médico" -> {
                                            startActivity(Intent(this, MedicoHomeActivity::class.java))
                                            finish()
                                        }
                                        else -> {
                                            Toast.makeText(this, "Rol no definido", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al obtener rol", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Link para ir a registro
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterPatientActivity::class.java))
        }
    }
}