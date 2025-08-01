package rubio.naely.saludplus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarPerfilPacienteActivity : AppCompatActivity() {

    private lateinit var imgFotoPerfil: ImageView
    private lateinit var btnCambiarFoto: TextView
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val idPaciente by lazy { auth.currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editarperfilpaciente)

        imgFotoPerfil = findViewById(R.id.imgPerfil)
        btnCambiarFoto = findViewById(R.id.tvCambiarFoto)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnBack = findViewById(R.id.btnBack)

        cargarDatosPaciente()

        btnCambiarFoto.setOnClickListener {
            seleccionarImagen()
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosPaciente() {
        if (idPaciente.isEmpty()) return

        firestore.collection("usuarios").document(idPaciente)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etCorreo.setText(document.getString("email") ?: "")
                    etTelefono.setText(document.getString("telefono") ?: "")

                    val urlImagen = document.getString("foto")
                    if (!urlImagen.isNullOrEmpty()) {
                        Glide.with(this).load(urlImagen).into(imgFotoPerfil)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            Glide.with(this).load(uri).into(imgFotoPerfil)
        }
    }

    private fun guardarCambios() {
        val nuevoCorreo = etCorreo.text.toString().trim()
        val nuevoTelefono = etTelefono.text.toString().trim()

        if (nuevoCorreo.isEmpty() || nuevoTelefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cambios = mapOf(
            "email" to nuevoCorreo,
            "telefono" to nuevoTelefono
        )

        firestore.collection("usuarios").document(idPaciente)
            .update(cambios)
            .addOnSuccessListener {
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }
}
