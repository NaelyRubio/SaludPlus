package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.EditarPerfilActivity
import rubio.naely.saludplus.ListaMedicosActivity
import rubio.naely.saludplus.ui.MisCitasActivity
import rubio.naely.saludplus.R

class PerfilPacienteActivity : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvFechaNacimiento: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var imgEditarPerfil: ImageView
    private lateinit var btnBack: ImageView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val idPaciente by lazy { auth.currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfilpaciente)

        // Referencias de vista
        tvNombre = findViewById(R.id.tvNombre)
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento)
        tvGenero = findViewById(R.id.tvGenero)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        imgEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnBack = findViewById(R.id.btnBack)

        cargarDatosPaciente()
        configurarBotones()
        configurarModoOscuro()
    }

    private fun cargarDatosPaciente() {
        if (idPaciente.isEmpty()) return

        firestore.collection("pacientes").document(idPaciente)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    tvNombre.text = document.getString("nombre")
                    tvFechaNacimiento.text = document.getString("fechaNacimiento")
                    tvGenero.text = document.getString("sexo")
                    tvCorreo.text = document.getString("correo")
                    tvTelefono.text = document.getString("telefono")
                } else {
                    Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarBotones() {
        imgEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            intent.putExtra("idPaciente", idPaciente)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.navCitas).setOnClickListener {
            startActivity(Intent(this, MisCitasActivity::class.java))
        }

        findViewById<ImageView>(R.id.navMedicos).setOnClickListener {
            startActivity(Intent(this, ListaMedicosActivity::class.java))
        }

        findViewById<ImageView>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilPacienteActivity::class.java))
        }
    }

    private fun configurarModoOscuro() {
        val rbClaro = findViewById<RadioButton>(R.id.rbClaro)
        val rbOscuro = findViewById<RadioButton>(R.id.rbOscuro)

        rbClaro.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        rbOscuro.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
