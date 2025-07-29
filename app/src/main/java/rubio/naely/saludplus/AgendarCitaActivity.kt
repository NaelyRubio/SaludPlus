package rubio.naely.saludplus

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.utils.NavActivity

class AgendarCitaActivity : NavActivity() {

    private lateinit var btnCancelar: Button
    private lateinit var btnAgendar: Button
    private lateinit var etMotivo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendarcita)

        // Referencias a los botones y campos
        btnCancelar = findViewById(R.id.btnCancelar)
        btnAgendar = findViewById(R.id.btnAgendar)
        etMotivo = findViewById(R.id.etMotivo)

        // Acción Cancelar
        btnCancelar.setOnClickListener {
            Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Acción Agendar
        btnAgendar.setOnClickListener {
            val motivo = etMotivo.text.toString().trim()
            if (motivo.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe el motivo de la consulta", Toast.LENGTH_SHORT).show()
            } else {
                guardarCitaEnFirestore(motivo)
            }
        }
    }

    private fun guardarCitaEnFirestore(motivo: String) {
        val db = FirebaseFirestore.getInstance()
        val uidPaciente = FirebaseAuth.getInstance().currentUser?.uid

        if (uidPaciente == null) {
            Toast.makeText(this, "No se pudo obtener el ID del paciente", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulación de datos que debes recibir por Intent más adelante
        val idDoctor = "DOCTOR_001"
        val nombreDoctor = "Dra. Laura Torres"
        val fechaSeleccionada = "2025-07-30"
        val horaSeleccionada = "10:30 AM"

        val cita = hashMapOf(
            "idPaciente" to uidPaciente,
            "idDoctor" to idDoctor,
            "nombreDoctor" to nombreDoctor,
            "fecha" to fechaSeleccionada,
            "hora" to horaSeleccionada,
            "motivo" to motivo,
            "estado" to "pendiente"
        )

        db.collection("citas")
            .add(cita)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita agendada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la cita", Toast.LENGTH_SHORT).show()
            }
    }
}
