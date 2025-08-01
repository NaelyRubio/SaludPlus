package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.EditarPerfilMedicoActivity
import rubio.naely.saludplus.R
import rubio.naely.saludplus.utils.NavMedicoActivity

class PerfilMedicoActivity : NavMedicoActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvCedulaTitulo: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var imgEditarPerfil: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var btnCerrarSesion: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfilmedico)

        // Referencias
        tvSaludo = findViewById(R.id.tvSaludo)
        tvCedulaTitulo = findViewById(R.id.tvCedulaTitulo)
        tvNombre = findViewById(R.id.tvNombre)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        imgEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnBack = findViewById(R.id.btnBack)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        cargarDatosMedico()
        configurarBotones()
        configurarModoOscuro()

        configurarNavegacionInferior(
            findViewById(R.id.navHome),
            findViewById(R.id.navRegistroHorario),
            findViewById(R.id.navhorarios),
            findViewById(R.id.navPerfil),
            "perfil"
        )
    }

    override fun onResume() {
        super.onResume()
        cargarDatosMedico()
    }

    private fun getUidActual(): String = auth.currentUser?.uid ?: ""

    private fun cargarDatosMedico() {
        val uid = getUidActual()
        if (uid.isEmpty()) return

        Log.d("PerfilMedicoActivity", "UID actual: $uid")

        firestore.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("nombre") ?: ""
                    val apellido = document.getString("apellido") ?: ""
                    val especialidad = document.getString("especialidad") ?: ""
                    val correo = document.getString("email") ?: ""
                    val telefono = document.getString("telefono") ?: ""
                    val direccion = document.getString("direccion") ?: ""
                    val cedula = document.getString("cedula") ?: ""

                    tvNombre.text = "$nombre $apellido"
                    tvEspecialidad.text = especialidad
                    tvCorreo.text = correo
                    tvTelefono.text = telefono
                    tvUbicacion.text = direccion
                    tvCedulaTitulo.text = "Cédula Profesional #$cedula"
                    tvSaludo.text = "Hola Dr. $nombre!"
                } else {
                    Toast.makeText(this, "No se encontraron datos del médico", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarBotones() {
        imgEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilMedicoActivity::class.java)
            intent.putExtra("idMedico", getUidActual())
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
