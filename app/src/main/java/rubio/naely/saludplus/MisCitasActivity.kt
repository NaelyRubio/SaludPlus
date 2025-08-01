package rubio.naely.saludplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.ui.LoginActivity

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
        btnBack.setOnClickListener { finish() }
        btnNotificaciones.setOnClickListener { /* opcional */ }
        btnAgendarCita.setOnClickListener {
            startActivity(Intent(this, AgendarCitaActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            verificarCitasDelPaciente()
        }
    }


    private fun verificarCitasDelPaciente() {
        val user = auth.currentUser
        if (user?.uid == null) {
            Toast.makeText(
                this,
                "Tu sesión ha expirado. Por favor, inicia sesión de nuevo.",
                Toast.LENGTH_LONG
            ).show()
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val uid = user.uid
        db.collection("citas")
            .whereEqualTo("idPaciente", uid)
            .whereIn("estado", listOf("pendiente", "aceptada")) // ✅ Mostrar solo citas activas
            .get()
            .addOnSuccessListener { documents ->
                layoutListaCitas.removeAllViews()

                if (documents.isEmpty) {
                    layoutSinCitas.visibility = View.VISIBLE
                    layoutListaCitas.visibility = View.GONE
                } else {
                    layoutSinCitas.visibility = View.GONE
                    layoutListaCitas.visibility = View.VISIBLE

                    for (document in documents) {
                        val card = layoutInflater.inflate(
                            R.layout.card_doctorcita,
                            layoutListaCitas,
                            false
                        )

                        val tvNombre = card.findViewById<TextView>(R.id.tvNombreDoctor)
                        val tvFecha = card.findViewById<TextView>(R.id.tvFechaHoraCita)
                        val imgDoctor = card.findViewById<ImageView>(R.id.imgDoctor)
                        val tvEspecialidad = card.findViewById<TextView>(R.id.tvEspecialidad)
                        val btnVerCita = card.findViewById<ImageButton>(R.id.btnVerCita)

                        val nombre = document.getString("nombreDoctor") ?: "Doctor desconocido"
                        val especialidad =
                            document.getString("especialidad") ?: "Especialidad no disponible"
                        val fecha = document.getString("fecha") ?: ""
                        val hora = document.getString("hora") ?: ""
                        val estado = document.getString("estado") ?: "pendiente"
                        val motivo = document.getString("motivo") ?: ""
                        val fotoUrl = document.getString("fotoDoctor")
                        val citaId = document.id

                        tvNombre.text = nombre
                        tvEspecialidad.text = especialidad
                        tvFecha.text = "$fecha - $hora"

                        if (!fotoUrl.isNullOrEmpty()) {
                            Glide.with(this).load(fotoUrl).into(imgDoctor)
                        } else {
                            imgDoctor.setImageResource(R.drawable.perfil)
                        }

                        btnVerCita.setOnClickListener {
                            val intent = Intent(this, DetalleCitaPacienteActivity::class.java)
                            intent.putExtra("idCita", citaId)
                            intent.putExtra("nombreDoctor", nombre)
                            intent.putExtra("especialidad", especialidad)
                            intent.putExtra("fechaHora", "$fecha - $hora")
                            intent.putExtra("motivoConsulta", motivo)
                            intent.putExtra("estado", estado)
                            intent.putExtra("fotoDoctor", fotoUrl)
                            startActivityForResult(intent, 1001)
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