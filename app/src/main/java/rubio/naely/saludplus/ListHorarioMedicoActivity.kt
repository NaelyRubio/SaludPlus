package rubio.naely.saludplus

import Horario
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.adapter.HorariosAdapter
import rubio.naely.saludplus.ui.DisponibilidadActivity
import rubio.naely.saludplus.ui.MedicoHomeActivity
import rubio.naely.saludplus.ui.PerfilMedicoActivity

class ListHorarioMedicoActivity : AppCompatActivity() {

    private lateinit var recyclerHorarios: RecyclerView
    private lateinit var btnRegistrarHorario: Button
    private lateinit var btnRegistrarHorarioVacio: Button
    private lateinit var btnBack: ImageView
    private lateinit var layoutSinHorarios: LinearLayout

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val listaHorarios = mutableListOf<Horario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listhorariomedico)

        bindViews()
        setupListeners()
        cargarHorarios()
    }

    private fun bindViews() {
        recyclerHorarios = findViewById(R.id.recyclerHorarios)
        btnRegistrarHorario = findViewById(R.id.btnRegistrarHorario)
        btnRegistrarHorarioVacio = findViewById(R.id.btnRegistrarHorarioVacio)
        btnBack = findViewById(R.id.btnBack)
        layoutSinHorarios = findViewById(R.id.layoutSinHorarios)
        recyclerHorarios.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        btnRegistrarHorario.setOnClickListener {
            startActivity(Intent(this, DisponibilidadActivity::class.java))
        }

        btnRegistrarHorarioVacio.setOnClickListener {
            startActivity(Intent(this, DisponibilidadActivity::class.java))
        }

        btnBack.setOnClickListener {
            finish()
        }

        // Navegación inferior
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MedicoHomeActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.navRegistroHorario).setOnClickListener {
            // Ya estamos en esta pantalla, no hacemos nada o se puede refrescar
        }

        findViewById<ImageView>(R.id.navhorarios).setOnClickListener {
            startActivity(Intent(this, ListHorarioMedicoActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilMedicoActivity::class.java))
            finish()
        }
    }

    private fun cargarHorarios() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("disponibilidad")
            .document(uid)
            .collection("horarios")
            .get()
            .addOnSuccessListener { result ->
                listaHorarios.clear()
                for (document in result) {
                    val horario = document.toObject(Horario::class.java)
                    listaHorarios.add(horario)
                }

                if (listaHorarios.isEmpty()) {
                    recyclerHorarios.visibility = View.GONE
                    layoutSinHorarios.visibility = View.VISIBLE
                    btnRegistrarHorario.visibility = View.GONE
                } else {
                    recyclerHorarios.visibility = View.VISIBLE
                    layoutSinHorarios.visibility = View.GONE
                    btnRegistrarHorario.visibility = View.VISIBLE

                    val adapter = HorariosAdapter(
                        listaHorarios,
                        onEditar = { horario ->
                            val intent = Intent(this, DisponibilidadActivity::class.java)
                            intent.putExtra("horarioEdit", horario)
                            startActivity(intent)
                        },
                        onEliminar = { horario ->
                            eliminarHorario(uid, horario)
                        }
                    )
                    recyclerHorarios.adapter = adapter
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar horarios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarHorario(uid: String, horario: Horario) {
        db.collection("disponibilidad")
            .document(uid)
            .collection("horarios")
            .whereEqualTo("horaInicio", horario.horaInicio)
            .whereEqualTo("horaFin", horario.horaFin)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                }
                Toast.makeText(this, "Horario eliminado", Toast.LENGTH_SHORT).show()
                cargarHorarios()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }
}
