package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.*
import rubio.naely.saludplus.utils.NavActivity // ⬅️ Hereda de tu clase base

class PacienteHomeActivity : NavActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var btnVerMasServicios: Button
    private lateinit var btnVerMasCitas: Button
    private lateinit var btnFiltro: ImageView
    private lateinit var layoutSinCitas: LinearLayout
    private lateinit var btnAgendarPrimeraCita: Button

    // Navegación inferior
    private lateinit var navHome: ImageView
    private lateinit var navCitas: ImageView
    private lateinit var navMedicos: ImageView
    private lateinit var navPerfil: ImageView

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pacientehome)

        bindViews()
        setClicks()
        configurarNavegacionInferior(navHome, navCitas, navMedicos, navPerfil, actual = "home")
        cargarNombrePaciente()
    }

    private fun bindViews() {
        tvSaludo = findViewById(R.id.tvSaludo)
        btnVerMasServicios = findViewById(R.id.btnVerMasServicios)
        btnVerMasCitas = findViewById(R.id.btnVerMasCitas)
        btnFiltro = findViewById(R.id.btnFiltro)

        layoutSinCitas = findViewById(R.id.layoutSinCitas)
        btnAgendarPrimeraCita = findViewById(R.id.btnAgendarPrimeraCita)

        // barra inferior
        navHome = findViewById(R.id.navHome)
        navCitas = findViewById(R.id.navCitas)
        navMedicos = findViewById(R.id.navMedicos)
        navPerfil = findViewById(R.id.navPerfil)
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
                tvSaludo.text = "Hola, $nombre 👋"
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo cargar tu nombre", Toast.LENGTH_SHORT).show()
            }
    }
}
