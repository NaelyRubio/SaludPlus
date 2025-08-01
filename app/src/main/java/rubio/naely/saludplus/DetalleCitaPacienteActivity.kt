package rubio.naely.saludplus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DetalleCitaPacienteActivity : AppCompatActivity() {

    private lateinit var imgDoctor: ImageView
    private lateinit var tvNombreDoctor: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvFechaHora: TextView
    private lateinit var tvEstadoCita: TextView
    private lateinit var etMotivo: EditText
    private lateinit var btnCancelarCita: Button
    private lateinit var btnBack: ImageView

    private lateinit var citaId: String

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detallescitaspaciente)

        imgDoctor = findViewById(R.id.imgDoctor)
        tvNombreDoctor = findViewById(R.id.tvNombreDoctor)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvFechaHora = findViewById(R.id.tvFechaHora)
        tvEstadoCita = findViewById(R.id.tvEstadoCita)
        etMotivo = findViewById(R.id.etMotivoConsulta)
        btnCancelarCita = findViewById(R.id.btnCancelarCita)
        btnBack = findViewById(R.id.btnBack)

        citaId = intent.getStringExtra("idCita") ?: ""

        val nombreDoctor = intent.getStringExtra("nombreDoctor") ?: "Sin nombre"
        val especialidad = intent.getStringExtra("especialidad") ?: ""
        val fechaHora = intent.getStringExtra("fechaHora") ?: ""
        val motivo = intent.getStringExtra("motivoConsulta") ?: ""
        val estado = intent.getStringExtra("estado") ?: "pendiente"
        val fotoDoctor = intent.getStringExtra("fotoDoctor") ?: ""

        tvNombreDoctor.text = nombreDoctor
        tvEspecialidad.text = especialidad
        tvFechaHora.text = fechaHora
        etMotivo.setText(motivo)
        tvEstadoCita.text = estado.uppercase()

        when (estado.lowercase()) {
            "aceptada" -> tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_aceptada)
            "cancelada" -> tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_cancelada)
            "pendiente" -> tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_pendiente)
        }

        Glide.with(this)
            .load(fotoDoctor)
            .placeholder(R.drawable.doc2)
            .into(imgDoctor)

        btnCancelarCita.setOnClickListener {
            if (estado.lowercase() == "cancelada") {
                Toast.makeText(this, "Esta cita ya está cancelada", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("citas").document(citaId)
                .update("estado", "cancelada")
                .addOnSuccessListener {
                    Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()
                    tvEstadoCita.text = "CANCELADA"
                    tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_cancelada)
                    btnCancelarCita.isEnabled = false
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cancelar cita", Toast.LENGTH_SHORT).show()
                }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
