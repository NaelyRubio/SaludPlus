package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.AgendarCitaActivity
import rubio.naely.saludplus.R

class MisCitasActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnNotificaciones: ImageView
    private lateinit var btnAgendarCita: Button
    private lateinit var tvRecordatorio: TextView
    private lateinit var layoutSinCitas: LinearLayout
    private lateinit var layoutListaCitas: LinearLayout

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miscitaspaciente)

        bindViews()
        setListeners()
        verificarCitasDelPaciente()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        btnNotificaciones = findViewById(R.id.btnNotificaciones)
        btnAgendarCita = findViewById(R.id.btnAgendarCita)
        tvRecordatorio = findViewById(R.id.tvRecordatorio)
        layoutSinCitas = findViewById(R.id.layoutSinCitas)
        layoutListaCitas = findViewById(R.id.layoutListaCitas)
    }

    private fun setListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnNotificaciones.setOnClickListener {
            // Aquí puedes abrir una pantalla de notificaciones si la tienes
        }

        btnAgendarCita.setOnClickListener {
            startActivity(Intent(this, AgendarCitaActivity::class.java))
        }
    }

    private fun verificarCitasDelPaciente() {
        val user = auth.currentUser
        if (user?.uid == null) {
            Toast.makeText(this, "Tu sesión ha expirado. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val uid = user.uid

        db.collection("citas")
            .whereEqualTo("idPaciente", uid)
            .whereIn("estado", listOf("pendiente", "aceptada"))
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    layoutSinCitas.visibility = View.VISIBLE
                    layoutListaCitas.visibility = View.GONE
                } else {
                    layoutSinCitas.visibility = View.GONE
                    layoutListaCitas.visibility = View.VISIBLE
                    layoutListaCitas.removeAllViews()

                    for (cita in documents) {
                        val card = layoutInflater.inflate(R.layout.card_doctorcita, layoutListaCitas, false)

                        val tvNombre = card.findViewById<TextView>(R.id.tvNombreDoctor)
                        val tvEspecialidad = card.findViewById<TextView>(R.id.tvEspecialidad)
                        val tvFecha = card.findViewById<TextView>(R.id.tvFechaHoraCita)
                        val tvCalificacion = card.findViewById<TextView>(R.id.tvCalificacion)
                        val imgDoctor = card.findViewById<ImageView>(R.id.imgDoctor)

                        tvNombre.text = cita.getString("nombreDoctor") ?: ""
                        tvEspecialidad.text = cita.getString("especialidad") ?: ""
                        tvFecha.text = cita.getString("fechaHora") ?: ""
                        tvCalificacion.text = cita.getDouble("calificacion")?.toString() ?: "5.0"

                        val fotoUrl = cita.getString("fotoDoctor")
                        if (!fotoUrl.isNullOrEmpty()) {
                            Glide.with(this).load(fotoUrl).into(imgDoctor)
                        }

                        layoutListaCitas.addView(card)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show()
            }
    }
}

