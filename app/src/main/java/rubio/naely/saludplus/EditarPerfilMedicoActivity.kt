package rubio.naely.saludplus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.storage.FirebaseStorage

class EditarPerfilMedicoActivity : AppCompatActivity() {

    private lateinit var imgFotoPerfil: ImageView
    private lateinit var btnCambiarFoto: TextView
    private lateinit var etemail: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var etNombre: EditText
    private lateinit var etEspecialidad: EditText
    private lateinit var etGenero: EditText
    private lateinit var etCedula: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageView

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    // private val storage = FirebaseStorage.getInstance()
    private val idMedico by lazy { auth.currentUser?.uid ?: return@lazy "" }

    private var uriFotoSeleccionada: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editarperfilmedico)


        imgFotoPerfil = findViewById(R.id.imgPerfil)
        btnCambiarFoto = findViewById(R.id.tvCambiarFoto)
        etemail = findViewById(R.id.etemail)
        etTelefono = findViewById(R.id.etTelefono)
        etUbicacion = findViewById(R.id.etUbicacion)
        etNombre = findViewById(R.id.etNombreCompleto)
        etEspecialidad = findViewById(R.id.etEspecialidad)
        etGenero = findViewById(R.id.etGenero)
        etCedula = findViewById(R.id.etCedula)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnBack = findViewById(R.id.btnBack)

        cargarDatos()

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

    private fun cargarDatos() {
        if (idMedico.isEmpty()) return

        firestore.collection("usuarios").document(idMedico)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("nombre") ?: ""
                    val apellido = document.getString("apellido") ?: ""
                    etNombre.setText("$nombre $apellido")
                    etEspecialidad.setText(document.getString("especialidad") ?: "")
                    etGenero.setText(document.getString("genero") ?: "")
                    etCedula.setText(document.getString("cedula") ?: "")
                    etFechaNacimiento.setText(document.getString("fechaNacimiento") ?: "")
                    etemail.setText(document.getString("email") ?: "")
                    etTelefono.setText(document.getString("telefono") ?: "")
                    etUbicacion.setText(document.getString("direccion") ?: "")

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
            uriFotoSeleccionada = data?.data
            Glide.with(this).load(uriFotoSeleccionada).into(imgFotoPerfil)
        }
    }

    private fun guardarCambios() {
        val nuevoemail = etemail.text.toString().trim()
        val nuevoTelefono = etTelefono.text.toString().trim()
        val nuevaUbicacion = etUbicacion.text.toString().trim()

        if (nuevoemail.isEmpty() || nuevoTelefono.isEmpty() || nuevaUbicacion.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cambios = mapOf(
            "email" to nuevoemail,
            "telefono" to nuevoTelefono,
            "direccion" to nuevaUbicacion
        )


        firestore.collection("usuarios").document(idMedico)
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
