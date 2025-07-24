package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import rubio.naely.saludplus.R
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterPatientActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var generoSpinner: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var pacienteRadio: RadioButton
    private lateinit var medicoRadio: RadioButton
    private lateinit var especialidadEditText: EditText
    private lateinit var cedulaProfesionalEditText: EditText
    private lateinit var direccionConsultorioEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Vincular views
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
        generoSpinner = findViewById(R.id.generoSpinner)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        pacienteRadio = findViewById(R.id.pacienteRadio)
        medicoRadio = findViewById(R.id.medicoRadio)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)
        especialidadEditText = findViewById(R.id.especialidadEditText)
        cedulaProfesionalEditText = findViewById(R.id.cedulaEditText)
        direccionConsultorioEditText = findViewById(R.id.direccionConsultorioEditText)

        //ocultar campos del medico por defecto
        especialidadEditText.visibility = View.GONE
        cedulaProfesionalEditText.visibility = View.GONE
        direccionConsultorioEditText.visibility = View.GONE

        //Mostrar/ocultar segun seleccion de rol
        val rolRadioGroup = findViewById<RadioGroup>(R.id.rolRadioGroup)
        rolRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.medicoRadio) {
                especialidadEditText.visibility = View.VISIBLE
                cedulaProfesionalEditText.visibility = View.VISIBLE
                direccionConsultorioEditText.visibility = View.VISIBLE
            } else {
                especialidadEditText.visibility = View.GONE
                cedulaProfesionalEditText.visibility = View.GONE
                direccionConsultorioEditText.visibility = View.GONE
            }
        }

        // Spinner básico
        val generos = arrayOf("Seleccionar", "Masculino", "Femenino", "Otro")
        generoSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, generos)

        // Click en registrar
        registerButton.setOnClickListener {
            validarCampos()
        }

        // Ir al login
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validarCampos() {
        val nombre = nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val fecha = fechaNacimientoEditText.text.toString().trim()
        val genero = generoSpinner.selectedItem.toString()
        val email = emailEditText.text.toString()
        val pass = passwordEditText.text.toString()
        val confirm = confirmPasswordEditText.text.toString()
        val rol = when {
            pacienteRadio.isChecked -> "Paciente"
            medicoRadio.isChecked -> "Médico"
            else -> ""
        }
        if (nombre.isEmpty() || apellido.isEmpty() || fecha.isEmpty() || genero == "Seleccionar" || email.isEmpty()
            || pass.isEmpty() || confirm.isEmpty() || rol.isEmpty()
        ) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confirm) {
            Toast.makeText(this, "Las contraseñas no coindicen", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (rol == "Médico") {
            if (especialidadEditText.text.toString().trim().isEmpty() ||
                cedulaProfesionalEditText.text.toString().trim().isEmpty() ||
                direccionConsultorioEditText.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(this, "Completa los datos del médico", Toast.LENGTH_SHORT).show()
                return
            }
        }

        registrarUsuario(email, pass, nombre, apellido, fecha, genero, rol)

    }

    //funcion para registrar un nuevo usuario
    private fun registrarUsuario(
        email: String,
        pass: String,
        nombre: String,
        apellido: String,
        fecha: String,
        genero: String,
        rol: String
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val datos = mutableMapOf(
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "fechaNacimiento" to fecha,
                        "genero" to genero,
                        "rol" to rol,
                        "email" to email
                    )

                    if (rol == "Médico") {
                        datos["especialidad"] = especialidadEditText.text.toString().trim()
                        datos["cedula"] = cedulaProfesionalEditText.text.toString().trim()
                        datos["direccion"] = direccionConsultorioEditText.text.toString().trim()
                    }


                    db.collection("usuarios").document(uid).set(datos)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}





