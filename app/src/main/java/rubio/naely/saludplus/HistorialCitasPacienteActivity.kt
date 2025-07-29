package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.R
import rubio.naely.saludplus.model.Cita
import rubio.naely.saludplus.ListaMedicosActivity

class HistorialCitasPacienteActivity : AppCompatActivity() {

    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var layoutSinHistorial: LinearLayout

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historialcitaspaciente)

        val recyclerHistorial = findViewById<RecyclerView>(R.id.rvHistorialCitas)
        layoutSinHistorial = findViewById(R.id.layoutSinHistorial)

        recyclerHistorial.layoutManager = LinearLayoutManager(this)

        obtenerHistorialCitas()

        // Barra de navegación inferior
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, PacienteHomeActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navCitas).setOnClickListener {
            startActivity(Intent(this, MisCitasActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navMedicos).setOnClickListener {
            startActivity(Intent(this, ListaMedicosActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilPacienteActivity::class.java))
        }
    }

    private fun obtenerHistorialCitas() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("citas")
            .whereEqualTo("idPaciente", uid)
            .get()
            .addOnSuccessListener { result ->
                val listaHistorial = result.documents.mapNotNull { doc ->
                    val estado = doc.getString("estado") ?: ""
                    if (estado == "completada" || estado == "cancelada") {
                        Cita(
                            id = doc.id,
                            nombreDoctor = doc.getString("nombreDoctor") ?: "",
                            especialidad = doc.getString("especialidad") ?: "",
                            estado = estado,
                            fotoDoctor = doc.getString("fotoDoctor") ?: ""
                        )
                    } else null
                }

                if (listaHistorial.isNotEmpty()) {
                    recyclerHistorial.adapter = HistorialCitasAdapter(listaHistorial)
                    layoutSinHistorial.visibility = View.GONE
                } else {
                    layoutSinHistorial.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener historial", Toast.LENGTH_SHORT).show()
            }
    }
}
