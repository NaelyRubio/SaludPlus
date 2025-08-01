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

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detallescitaspaciente)
        db = FirebaseFirestore.getInstance()

        // Recuperar datos del intent
        val nombreDoctor = intent.getStringExtra("nombreDoctor") ?: "Nombre no disponible"
        val especialidad = intent.getStringExtra("especialidad") ?: "Especialidad no disponible"
        val fechaHora = intent.getStringExtra("fechaHora") ?: ""
        val estado = intent.getStringExtra("estado") ?: ""
        val motivo = intent.getStringExtra("motivoConsulta") ?: ""
        val fotoDoctor = intent.getStringExtra("fotoDoctor") ?: ""
        val citaId = intent.getStringExtra("idCita")

        if (citaId.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo abrir la cita (ID inválido)", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("DETALLE_CITA", "Abriendo detalle de cita con ID: $citaId")

        // Enlazar vistas
        val tvNombreDoctor = findViewById<TextView>(R.id.tvNombreDoctor)
        val tvEspecialidad = findViewById<TextView>(R.id.tvEspecialidad)
        val tvFechaHora = findViewById<TextView>(R.id.tvFechaHora)
        val tvEstado = findViewById<TextView>(R.id.tvEstadoCita)
        val tvMotivo = findViewById<TextView>(R.id.tvMotivo)
        val imgDoctor = findViewById<ImageView>(R.id.imgDoctor)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarCita)
        val btnReprogramar = findViewById<Button>(R.id.btnReprogramar)

        // Asignar datos
        tvNombreDoctor.text = nombreDoctor
        tvEspecialidad.text = especialidad
        tvFechaHora.text = fechaHora
        tvEstado.text = estado
        tvMotivo.text = motivo

        // Cargar foto del doctor
        if (fotoDoctor.isNotEmpty()) {
            Glide.with(this).load(fotoDoctor).into(imgDoctor)
        } else {
            imgDoctor.setImageResource(R.drawable.perfil)
        }

        // Cancelar cita
        btnCancelar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancelar cita")
                .setMessage("¿Estás segura que deseas cancelar esta cita?")
                .setPositiveButton("Sí") { _, _ -> cancelarCita(citaId) }
                .setNegativeButton("No", null)
                .show()
        }

        // Reprogramar cita
        btnReprogramar.setOnClickListener {
            startActivity(Intent(this, AgendarCitaActivity::class.java))
        }
    }

    private fun cancelarCita(citaId: String) {
        db.collection("citas").document(citaId)
            .update("estado", "cancelada")
            .addOnSuccessListener {
                Toast.makeText(this, "Cita cancelada correctamente", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // 👈 indica a MisCitasActivity que debe actualizar
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cancelar la cita", Toast.LENGTH_SHORT).show()
            }
    }
}
