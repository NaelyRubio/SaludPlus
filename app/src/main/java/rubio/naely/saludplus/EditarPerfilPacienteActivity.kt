package rubio.naely.saludplus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarPerfilPacienteActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etGenero: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var tvCambiarFoto: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageView

    private val firestore = FirebaseFirestore.getInstance()
    private var idPaciente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editarperfilpaciente)

        // Referencias de vista
        etNombre = findViewById(R.id.etNombre)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etGenero = findViewById(R.id.etGenero)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        tvCambiarFoto = findViewById(R.id.tvCambiarFoto)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnBack = findViewById(R.id.btnBack)

        // Recuperar ID del paciente (recomendado pasar desde PerfilPacienteActivity)
        idPaciente = intent.getStringExtra("idPaciente")

        if (idPaciente == null) {
            Toast.makeText(this, "ID de paciente no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosPaciente()

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosPaciente() {
        firestore.collection("pacientes").document(idPaciente!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etNombre.setText(document.getString("nombre"))
                    etFechaNacimiento.setText(document.getString("fechaNacimiento"))
                    etGenero.setText(document.getString("sexo"))
                    etCorreo.setText(document.getString("correo"))
                    etTelefono.setText(document.getString("telefono"))
                } else {
                    Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarCambios() {
        val nuevoCorreo = etCorreo.text.toString().trim()
        val nuevoTelefono = etTelefono.text.toString().trim()

        if (nuevoCorreo.isEmpty() || nuevoTelefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val datosActualizados = mapOf(
            "correo" to nuevoCorreo,
            "telefono" to nuevoTelefono
        )

        firestore.collection("pacientes").document(idPaciente!!)
            .update(datosActualizados)
            .addOnSuccessListener {
                Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
            }
    }
}
