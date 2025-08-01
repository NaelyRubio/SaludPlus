package rubio.naely.saludplus

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DetalleCitaDoctorActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detallecitavistadoctor)

        val nombreDoctor = intent.getStringExtra("nombreDoctor") ?: "Nombre no disponible"
        val especialidad = intent.getStringExtra("especialidad") ?: "Especialidad no disponible"
        val fechaHora = intent.getStringExtra("fechaHora") ?: ""
        val motivoConsulta = intent.getStringExtra("motivoConsulta") ?: ""
        val estado = intent.getStringExtra("estado") ?: ""

        db = FirebaseFirestore.getInstance()

        // Obtener ID de cita desde intent
        val citaId = intent.getStringExtra("citaId")
        if (!citaId.isNullOrEmpty()) {
            cargarDatosCita(citaId)
        } else {
            Toast.makeText(this, "ID de cita no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarDatosCita(citaId: String) {
        val tvNombre = findViewById<TextView>(R.id.tvNombrePaciente)
        val tvEdad = findViewById<TextView>(R.id.tvEdadPaciente)
        val tvSexo = findViewById<TextView>(R.id.tvSexoPaciente)
        val tvMotivo = findViewById<EditText>(R.id.etMotivoConsulta)
        val imgPaciente = findViewById<ImageView>(R.id.imgPerfil)
        val tvEstado = findViewById<TextView>(R.id.tvEstadoCita)

        db.collection("citas").document(citaId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    tvNombre.text = document.getString("nombrePaciente") ?: "Desconocido"
                    tvEdad.text = "${document.getLong("edadPaciente") ?: 0} años"
                    tvSexo.text = document.getString("sexoPaciente") ?: "-"
                    tvMotivo.setText(document.getString("motivo") ?: "Sin motivo registrado")
                    tvEstado.text = document.getString("estado")?.replaceFirstChar { it.uppercase() } ?: "Desconocido"

                    val foto = document.getString("fotoPaciente")
                    Glide.with(this)
                        .load(foto)
                        .placeholder(R.drawable.perfil)
                        .error(R.drawable.perfil)
                        .into(imgPaciente)
                } else {
                    Toast.makeText(this, "No se encontró la cita", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }
    }
}
