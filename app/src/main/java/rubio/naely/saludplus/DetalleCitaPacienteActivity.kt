package rubio.naely.saludplus.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.R

class DetalleCitaPacienteActivity : AppCompatActivity() {

    private lateinit var tvNombreDoctor: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvFechaHora: TextView
    private lateinit var tvMotivo: TextView
    private lateinit var tvEstado: TextView
    private lateinit var btnCancelarCita: Button
    private lateinit var btnBack: ImageView

    private lateinit var idCita: String
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detallescitaspaciente)

        // Referencias UI
        tvNombreDoctor = findViewById(R.id.tvNombreDoctor)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvFechaHora = findViewById(R.id.tvFechaHora)
        tvMotivo = findViewById(R.id.tvMotivo)
        tvEstado = findViewById(R.id.tvEstadoCita)
        btnCancelarCita = findViewById(R.id.btnCancelar)
        btnBack = findViewById(R.id.btnBack)

        // Datos recibidos por Intent
        idCita = intent.getStringExtra("idCita") ?: ""
        val nombreDoctor = intent.getStringExtra("nombreDoctor") ?: ""
        val especialidad = intent.getStringExtra("especialidad") ?: ""
        val fechaHora = intent.getStringExtra("fechaHora") ?: ""
        val motivo = intent.getStringExtra("motivoConsulta") ?: ""
        val estado = intent.getStringExtra("estado") ?: ""

        // Mostrar datos
        tvNombreDoctor.text = nombreDoctor
        tvEspecialidad.text = especialidad
        tvFechaHora.text = fechaHora
        tvMotivo.text = motivo
        tvEstado.text = estado

        // Cambiar color según estado
        when (estado.lowercase()) {
            "pendiente" -> tvEstado.setBackgroundResource(R.drawable.bg_estado_pendiente)
            "aceptada" -> tvEstado.setBackgroundResource(R.drawable.bg_estado_aceptada)
            "cancelada" -> tvEstado.setBackgroundResource(R.drawable.bg_estado_cancelada)
        }

        // Mostrar botón solo si está pendiente
        if (estado == "pendiente") {
            btnCancelarCita.visibility = View.VISIBLE
        } else {
            btnCancelarCita.visibility = View.GONE
        }

        btnCancelarCita.setOnClickListener {
            cancelarCita()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun cancelarCita() {
        firestore.collection("citas").document(idCita)
            .update("estado", "cancelada")
            .addOnSuccessListener {
                Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cancelar cita", Toast.LENGTH_SHORT).show()
            }
    }
}
