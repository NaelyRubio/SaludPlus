package rubio.naely.saludplus

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DetalleCitaDoctorActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detallecitavistadoctor)

        db = FirebaseFirestore.getInstance()

        // Obtener ID de cita desde intent
        val citaId = intent.getStringExtra("citaId")
        if (citaId != null) {
            cargarDatosCita(citaId)
        } else {
            // Manejo de error si no hay ID
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
                    if (document != null) {
                        val nombre = document.getString("nombrePaciente") ?: ""
                        val edad = document.getLong("edadPaciente")?.toString() ?: ""
                        val sexo = document.getString("sexoPaciente") ?: ""
                        val motivo = document.getString("motivo") ?: ""
                        val estado = document.getString("estado") ?: ""
                        val urlFoto = document.getString("fotoPaciente")

                        tvNombre.text = nombre
                        tvEdad.text = "$edad años"
                        tvSexo.text = sexo
                        tvMotivo.setText(motivo)
                        tvEstado.text = estado.capitalize()

                        if (!urlFoto.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(urlFoto)
                                .into(imgPaciente)
                        }
                    }
                }
                .addOnFailureListener {
                    // Manejar error de carga
                }
        }


    }