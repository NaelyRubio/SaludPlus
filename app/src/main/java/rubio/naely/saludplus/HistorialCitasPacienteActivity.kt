package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.*
import rubio.naely.saludplus.model.Cita

class HistorialCitasPacienteActivity : AppCompatActivity() {

    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var layoutSinHistorial: LinearLayout

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historialcitaspaciente)

        recyclerHistorial = findViewById(R.id.rvHistorialCitas)
        layoutSinHistorial = findViewById(R.id.layoutSinHistorial)

        recyclerHistorial.layoutManager = LinearLayoutManager(this)

        obtenerHistorialCitas()

        Log.d("HistorialCitas", "Entrando al historial correctamente")
    }

    private fun obtenerHistorialCitas() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("citas")
            .whereEqualTo("idPaciente", uid)
            .whereIn("estado", listOf("completada", "cancelada"))
            .get()
            .addOnSuccessListener { result ->

                val listaHistorial = result.documents.mapNotNull { doc ->
                    Cita(
                        id = doc.id,
                        nombreDoctor = doc.getString("nombreDoctor") ?: "",
                        especialidad = doc.getString("especialidad") ?: "",
                        estado = doc.getString("estado") ?: "",
                        fotoDoctor = doc.getString("fotoDoctor") ?: "",
                        fecha = doc.getString("fecha") ?: "",
                        hora = doc.getString("hora") ?: "",
                        motivo = doc.getString("motivoConsulta") ?: "",
                        nombrePaciente = doc.getString("nombrePaciente") ?: "",
                        fotoPaciente = doc.getString("fotoPaciente") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }


                if (listaHistorial.isNotEmpty()) {
                    recyclerHistorial.adapter = HistorialCitasAdapter(listaHistorial)
                    layoutSinHistorial.visibility = View.GONE
                } else {
                    layoutSinHistorial.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener historial", Toast.LENGTH_SHORT).show()
                Log.e("HistorialCitas", "Error Firebase: ${e.message}")
            }
    }


}
