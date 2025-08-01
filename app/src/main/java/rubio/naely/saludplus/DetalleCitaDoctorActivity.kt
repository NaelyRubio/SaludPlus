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

        db = FirebaseFirestore.getInstance()

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
                    val motivo = document.getString("motivo") ?: "Sin motivo registrado"
                    val estado = document.getString("estado") ?: "desconocido"
                    tvMotivo.setText(motivo)
                    tvEstado.text = estado.replaceFirstChar { it.uppercase() }

                    val nombre = document.getString("nombrePaciente")
                    val edad = document.getLong("edadPaciente")
                    val sexo = document.getString("sexoPaciente")
                    val foto = document.getString("fotoPaciente")
                    val idPaciente = document.getString("idPaciente")

                    if (!nombre.isNullOrBlank()) {
                        tvNombre.text = nombre
                        tvEdad.text = "${edad ?: 0} años"
                        tvSexo.text = sexo ?: "-"
                        Glide.with(this)
                            .load(foto)
                            .placeholder(R.drawable.perfil)
                            .error(R.drawable.perfil)
                            .into(imgPaciente)
                    } else if (!idPaciente.isNullOrBlank()) {
                        db.collection("usuarios").document(idPaciente).get()
                            .addOnSuccessListener { userDoc ->
                                val nombreUser = userDoc.getString("nombre") ?: "Desconocido"
                                val edadUser = userDoc.getLong("edad")?.toInt() ?: 0
                                val sexoUser = userDoc.getString("sexo") ?: "-"
                                val fotoUrl = userDoc.getString("imagenUrl")

                                tvNombre.text = nombreUser
                                tvEdad.text = "$edadUser años"
                                tvSexo.text = sexoUser

                                Glide.with(this)
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.perfil)
                                    .error(R.drawable.perfil)
                                    .into(imgPaciente)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al cargar paciente", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        tvNombre.text = "Paciente desconocido"
                    }

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
