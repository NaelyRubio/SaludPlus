package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
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



class RegisterPatientActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var generoSpinner: Spinner
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var pacienteRadio: RadioButton
    private lateinit var medicoRadio: RadioButton
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var especialidadEditText: EditText
    private lateinit var cedulaProfesionalEditText: EditText
    private lateinit var direccionConsultorioEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)

        //Vincular views
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
        generoSpinner = findViewById(R.id.generoSpinner)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText= findViewById(R.id.confirmPasswordEditText)
        pacienteRadio = findViewById(R.id.pacienteRadio)
        medicoRadio = findViewById(R.id.medicoRadio)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)
        especialidadEditText = findViewById(R.id.especialidadEditText)
        cedulaProfesionalEditText = findViewById(R.id.cedulaEditText)
        direccionConsultorioEditText = findViewById(R.id.direccionConsultorioEditText)


        // Spinner básico
        val generos = arrayOf("Seleccionar", "Masculino", "Femenino", "Otro")
        generoSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, generos)

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

    private fun validarCampos(){
        val nombre= nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val fecha = fechaNacimientoEditText.text.toString().trim()
        val genero = generoSpinner.selectedItem.toString()
        val pass = passwordEditText.text.toString()
        val confirm = confirmPasswordEditText.text.toString()
        val rol = if (pacienteRadio.isChecked) "Paciente" else if (medicoRadio.isChecked) "Médico" else ""
        if (nombre.isEmpty() || apellido.isEmpty() || fecha.isEmpty() || genero == "Seleccionar" || pass.isEmpty() || confirm.isEmpty() || rol.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Validación correcta", Toast.LENGTH_LONG).show()
    }
}




