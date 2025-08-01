package rubio.naely.saludplus

import Medico
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.adapters.MedicosAdapter

class ListaMedicosActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnNotificaciones: ImageView
    private lateinit var recyclerMedicos: RecyclerView

    private val listaMedicos = mutableListOf<Medico>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listamedicospaciente)

        // Vincula vistas
        btnBack = findViewById(R.id.btnBack)
        btnNotificaciones = findViewById(R.id.btnNotificaciones)
        recyclerMedicos = findViewById(R.id.recyclerMedicos)

        recyclerMedicos.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        btnNotificaciones.setOnClickListener {
            Toast.makeText(this, "Aquí iría la pantalla de notificaciones", Toast.LENGTH_SHORT).show()
        }

        cargarMedicos()
    }

    private fun cargarMedicos() {
        db.collection("usuarios")
            .whereEqualTo("rol", "Médico")
            .get()
            .addOnSuccessListener { result ->
                listaMedicos.clear()
                for (doc in result) {
                    val medico = doc.toObject(Medico::class.java).copy(id = doc.id)

                    // Buscamos su disponibilidad para sacar el precio
                    db.collection("disponibilidad")
                        .document(medico.id)
                        .collection("horarios")
                        .limit(1)
                        .get()
                        .addOnSuccessListener { horarios ->
                            if (!horarios.isEmpty) {
                                val precio = horarios.first().getDouble("precio") ?: 0.0
                                val medicoConPrecio = medico.copy(precio = precio)
                                listaMedicos.add(medicoConPrecio)
                            } else {
                                listaMedicos.add(medico) // Sin precio
                            }

                            // Solo refrescamos cuando ya esté todo cargado
                            if (listaMedicos.size == result.size()) {
                                recyclerMedicos.adapter = MedicosAdapter(listaMedicos) { medicoSeleccionado ->
                                    val intent = Intent(this, AgendarCitaActivity::class.java)
                                    intent.putExtra("medicoId", medicoSeleccionado.id)
                                    startActivity(intent)
                                }
                            }
                        }
                        .addOnFailureListener {
                            listaMedicos.add(medico) // Error al cargar precio, usamos sin precio
                            if (listaMedicos.size == result.size()) {
                                recyclerMedicos.adapter = MedicosAdapter(listaMedicos) { medicoSeleccionado ->
                                    val intent = Intent(this, AgendarCitaActivity::class.java)
                                    intent.putExtra("medicoId", medicoSeleccionado.id)
                                    startActivity(intent)
                                }
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar médicos", Toast.LENGTH_SHORT).show()
            }
    }

}
