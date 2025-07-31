package rubio.naely.saludplus.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.R
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var generoSpinner: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var especialidadEditText: EditText
    private lateinit var cedulaProfesionalEditText: EditText
    private lateinit var direccionConsultorioEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView

    private lateinit var btnPaciente: Button
    private lateinit var btnMedico: Button
    private var rolSeleccionado: String = "Paciente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Vistas
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
        generoSpinner = findViewById(R.id.generoSpinner)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        especialidadEditText = findViewById(R.id.especialidadEditText)
        cedulaProfesionalEditText = findViewById(R.id.cedulaEditText)
        direccionConsultorioEditText = findViewById(R.id.direccionConsultorioEditText)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)

        btnPaciente = findViewById(R.id.btnPaciente)
        btnMedico = findViewById(R.id.btnMedico)

        // Rol: Paciente
        btnPaciente.setOnClickListener {
            rolSeleccionado = "Paciente"
            btnPaciente.setBackgroundResource(R.drawable.bg_segmented_selected)
            btnPaciente.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            btnMedico.setBackgroundResource(R.drawable.bg_segmented_unselected)
            btnMedico.setTextColor(ContextCompat.getColor(this, R.color.gris_text))
            especialidadEditText.visibility = View.GONE
            cedulaProfesionalEditText.visibility = View.GONE
            direccionConsultorioEditText.visibility = View.GONE
        }

        // Rol: Médico
        btnMedico.setOnClickListener {
            rolSeleccionado = "Médico"
            btnMedico.setBackgroundResource(R.drawable.bg_segmented_selected)
            btnMedico.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            btnPaciente.setBackgroundResource(R.drawable.bg_segmented_unselected)
            btnPaciente.setTextColor(ContextCompat.getColor(this, R.color.gris_text))
            especialidadEditText.visibility = View.VISIBLE
            cedulaProfesionalEditText.visibility = View.VISIBLE
            direccionConsultorioEditText.visibility = View.VISIBLE
        }

        // Spinner género
        val generos = arrayOf("Seleccionar", "Masculino", "Femenino", "Otro")
        generoSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, generos)

        // Calendario
        fechaNacimientoEditText.setOnClickListener { mostrarDatePicker() }

        // Registro
        registerButton.setOnClickListener { validarCampos() }

        // Ir a login
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                fechaNacimientoEditText.setText(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun validarCampos() {
        val nombre = nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val fecha = fechaNacimientoEditText.text.toString().trim()
        val genero = generoSpinner.selectedItem.toString()
        val email = emailEditText.text.toString()
        val pass = passwordEditText.text.toString()
        val confirm = confirmPasswordEditText.text.toString()

        if (nombre.isEmpty() || apellido.isEmpty() || fecha.isEmpty() || genero == "Seleccionar" ||
            email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
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

        if (rolSeleccionado == "Médico") {
            if (especialidadEditText.text.toString().isEmpty() ||
                cedulaProfesionalEditText.text.toString().isEmpty() ||
                direccionConsultorioEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Completa los datos del médico", Toast.LENGTH_SHORT).show()
                return
            }
        }

        registrarUsuario(email, pass, nombre, apellido, fecha, genero, rolSeleccionado)
    }

    private fun registrarUsuario(
        email: String, pass: String,
        nombre: String, apellido: String,
        fecha: String, genero: String, rol: String
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val datos = mutableMapOf<String, Any>(
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

