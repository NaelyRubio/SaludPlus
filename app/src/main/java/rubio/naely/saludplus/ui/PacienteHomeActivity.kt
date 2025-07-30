package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.*
import rubio.naely.saludplus.utils.NavActivity

class PacienteHomeActivity : NavActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var btnVerMasServicios: Button
    private lateinit var btnVerMasCitas: Button
    private lateinit var btnFiltro: ImageView
    private lateinit var btnAgendarPrimeraCita: Button

    // Navegación inferior
    private lateinit var navHome: ImageView
    private lateinit var navCitas: ImageView
    private lateinit var navMedicos: ImageView
    private lateinit var navPerfil: ImageView

    // Layout sin citas
    private lateinit var layoutSinCitas: LinearLayout

    // Vista inflada desde include
    private lateinit var includeView: View
    private lateinit var imgDoctor: ImageView
    private lateinit var tvNombreDoctor: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvFechaHoraCita: TextView
    private lateinit var tvCalificacion: TextView
    private lateinit var btnVerCita: ImageButton

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pacientehome)

        bindViews()
        setClicks()
        configurarNavegacionInferior(navHome, navCitas, navMedicos, navPerfil, actual = "home")
        cargarNombrePaciente()
        verificarCitasDelPaciente()
    }

    private fun bindViews() {
        tvSaludo = findViewById(R.id.tvSaludo)
        btnVerMasServicios = findViewById(R.id.btnVerMasServicios)
        btnVerMasCitas = findViewById(R.id.btnVerMasCitas)
        btnFiltro = findViewById(R.id.btnFiltro)
        btnAgendarPrimeraCita = findViewById(R.id.btnAgendarPrimeraCita)

        // Navegación inferior
        navHome = findViewById(R.id.navHome)
        navCitas = findViewById(R.id.navCitas)
        navMedicos = findViewById(R.id.navMedicos)
        navPerfil = findViewById(R.id.navPerfil)

        // Layout sin citas
        layoutSinCitas = findViewById(R.id.layoutSinCitas)

        // Include card_doctorcita
        includeView = findViewById(R.id.includeCardCita)
        imgDoctor = includeView.findViewById(R.id.imgDoctor)
        tvNombreDoctor = includeView.findViewById(R.id.tvNombreDoctor)
        tvEspecialidad = includeView.findViewById(R.id.tvEspecialidad)
        tvFechaHoraCita = includeView.findViewById(R.id.tvFechaHoraCita)
        tvCalificacion = includeView.findViewById(R.id.tvCalificacion)
        btnVerCita = includeView.findViewById(R.id.btnVerCita)
    }

    private fun setClicks() {
        btnVerMasServicios.setOnClickListener {
            startActivity(Intent(this, ListaEspecialidadesActivity::class.java))
        }

        btnVerMasCitas.setOnClickListener {
            startActivity(Intent(this, MisCitasActivity::class.java))
        }

        btnAgendarPrimeraCita.setOnClickListener {
            startActivity(Intent(this, ListaMedicosActivity::class.java))
        }

        btnFiltro.setOnClickListener {
            startActivity(Intent(this, ListaEspecialidadesActivity::class.java))
        }
    }

    private fun cargarNombrePaciente() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val nombre = document.getString("nombre") ?: "Paciente"
                tvSaludo.text = "Hola, $nombre "
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo cargar tu nombre", Toast.LENGTH_SHORT).show()
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
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    layoutSinCitas.visibility = View.VISIBLE
                    includeView.visibility = View.GONE
                    Log.d("PACIENTE_HOME", "No hay citas activas, se muestra layout vacío.")
                } else {
                    layoutSinCitas.visibility = View.GONE
                    includeView.visibility = View.VISIBLE

                    val cita = documents.first()
                    val nombreDoctor = cita.getString("nombreDoctor") ?: ""
                    val especialidad = cita.getString("especialidad") ?: ""
                    val fechaHora = cita.getString("fechaHora") ?: ""
                    val calificacion = cita.getDouble("calificacion") ?: 5.0
                    val citaId = cita.id
                    val fotoUrl = cita.getString("fotoDoctor")

                    tvNombreDoctor.text = nombreDoctor
                    tvEspecialidad.text = especialidad
                    tvFechaHoraCita.text = fechaHora
                    tvCalificacion.text = calificacion.toString()

                    if (!fotoUrl.isNullOrEmpty()) {
                        Glide.with(this).load(fotoUrl).into(imgDoctor)
                    }

                    btnVerCita.setOnClickListener {
                        val intent = Intent(this, DetalleCitaPacienteActivity::class.java)
                        intent.putExtra("citaId", citaId)
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("PACIENTE_HOME", "Error al cargar citas: ${e.message}")
                Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show()
            }
    }
}
