package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.EditarPerfilPacienteActivity
import rubio.naely.saludplus.R
import rubio.naely.saludplus.utils.NavPacienteActivity


class PerfilPacienteActivity : NavPacienteActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvExpediente: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvFechaNacimiento: TextView
    private lateinit var tvGenero: TextView
    private lateinit var btnEditarPerfil: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnHistorial: ImageButton


    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfilpaciente)

        // Referencias de vistas
        tvSaludo = findViewById(R.id.tvSaludo)
        tvExpediente = findViewById(R.id.tvExpediente)
        tvNombre = findViewById(R.id.tvNombre)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento)
        tvGenero = findViewById(R.id.tvGenero)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnBack = findViewById(R.id.btnBack)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)


        btnHistorial = findViewById(R.id.btnHistorialCitas)
        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialCitasPacienteActivity::class.java)
            startActivity(intent)
        }


        cargarDatosPaciente()
        configurarBotones()
        configurarModoOscuro()

        configurarNavegacionInferior(
            findViewById(R.id.navHome),
            findViewById(R.id.navCitas),
            findViewById(R.id.navMedicos),
            findViewById(R.id.navPerfil),
            "perfil"
        )
    }

    override fun onResume() {
        super.onResume()
        cargarDatosPaciente()
    }

    private fun getUidActual(): String = auth.currentUser?.uid ?: ""

    private fun cargarDatosPaciente() {
        val uid = getUidActual()
        if (uid.isEmpty()) return

        firestore.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val nombre = document.getString("nombre") ?: ""
                if (document.exists() && nombre.isNotBlank()) {
                    val nombre = document.getString("nombre") ?: ""
                    val apellido = document.getString("apellido") ?: ""
                    val correo = document.getString("email") ?: ""
                    val telefono = document.getString("telefono") ?: ""
                    val fechaNacimiento = document.getString("fechaNacimiento") ?: ""
                    val sexo = document.getString("genero") ?: ""
                    val expediente = document.getString("expediente") ?: "Sin asignar"

                    val nombreCompleto = "$nombre $apellido"

                    tvSaludo.text = "Hola $nombre!"
                    tvExpediente.text = "Expediente #$expediente"
                    tvNombre.text = "$nombre $apellido"
                    tvCorreo.text = correo
                    tvTelefono.text = telefono
                    tvFechaNacimiento.text = fechaNacimiento
                    tvGenero.text = sexo

                } else {
                    // No mostramos mensaje si no hay nombre, pero sí log por si quieres rastrear
                    // Log.w("PerfilPaciente", "Documento sin datos clave: ${document.data}")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }
    }


    private fun configurarBotones() {
        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilPacienteActivity::class.java)
            intent.putExtra("idPaciente", getUidActual())
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
