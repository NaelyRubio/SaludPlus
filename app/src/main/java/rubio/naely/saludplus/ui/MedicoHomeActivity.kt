package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.DetalleCitaDoctorActivity
import rubio.naely.saludplus.R
import rubio.naely.saludplus.model.Cita
import rubio.naely.saludplus.utils.NavMedicoActivity
import java.util.Date
import java.util.Locale

class MedicoHomeActivity : NavMedicoActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var btnVerMasProximas: Button
    private lateinit var btnDia: Button
    private lateinit var btnSemana: Button
    private lateinit var btnRegistrarHorario: Button

    private lateinit var rvCitasMedico: RecyclerView
    private lateinit var tvSinCitas: TextView

    // Navegación inferior
    private lateinit var navHome: ImageView
    private lateinit var navRegistroHorario: ImageView
    private lateinit var navHorarios: ImageView
    private lateinit var navPerfil: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicohome)

        bindViews()
        setupListeners()

        // Barra de navegación inferior
        configurarNavegacionInferior(navHome, navRegistroHorario, navHorarios, navPerfil, "home")

        // Cargar citas del doctor
        cargarCitasDelDoctor()

        //carga el saludo del doctor
        cargarSaludo()
    }

    private fun bindViews() {
        tvSaludo = findViewById(R.id.tvSaludo)
        btnVerMasProximas = findViewById(R.id.btnVerMasProximas)
        btnDia = findViewById(R.id.btnDia)
        btnSemana = findViewById(R.id.btnSemana)
        btnRegistrarHorario = findViewById(R.id.btnRegistrarHorario)
        rvCitasMedico = findViewById(R.id.rvCitasMedico)
        tvSinCitas = findViewById(R.id.tvSinCitas)

        navHome = findViewById(R.id.navHome)
        navRegistroHorario = findViewById(R.id.navRegistroHorario)
        navHorarios = findViewById(R.id.navhorarios)
        navPerfil = findViewById(R.id.navPerfil)
    }

    private fun cargarSaludo() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: "Doctor/a"
                tvSaludo.text = "Hola Dr(a). $nombre !"
            }
    }

    private fun setupListeners() {
        btnDia.setOnClickListener {
            seleccionarFiltro(btnDia, btnSemana)
            // Aquí puedes cargar solo las citas del día
        }

        btnSemana.setOnClickListener {
            seleccionarFiltro(btnSemana, btnDia)
            // Aquí puedes cargar las citas de toda la semana
        }

        btnRegistrarHorario.setOnClickListener {
            val intent = Intent(this, DisponibilidadActivity::class.java)
            startActivity(intent)
        }

        btnVerMasProximas.setOnClickListener {
            // Navegar a la lista completa de citas
        }
    }

    private fun seleccionarFiltro(seleccionado: Button, otro: Button) {
        seleccionado.setBackgroundResource(R.drawable.bg_segmented_selected)
        seleccionado.setTextColor(getColor(R.color.white))
        otro.setBackgroundResource(R.drawable.bg_segmented_unselected)
        otro.setTextColor(getColor(R.color.gris_text))
    }

    private fun cargarCitasDelDoctor() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val idDoctor = auth.currentUser?.uid ?: return

        Log.d("DEBUG_DOC", "UID doctor actual: $idDoctor")

        db.collection("citas")
            .whereEqualTo("idDoctor", idDoctor)
            .whereIn("estado", listOf("pendiente", "aceptada"))
            .get()
            .addOnSuccessListener { resultado ->
                val listaCitas = resultado.documents.mapNotNull { doc ->
                    doc.toObject(Cita::class.java)?.apply { id = doc.id }
                }.onEach { cita ->
                    Log.d("DEBUG_CITAS", "Cita encontrada sin filtro - Fecha: '${cita.fecha}', Hora: '${cita.hora}'")
                }.sortedBy { it.fecha + it.hora }

                Log.d("DEBUG_CITAS", "Citas encontradas (sin filtrar): ${listaCitas.size}")

                if (listaCitas.isEmpty()) {
                    Log.d("DEBUG_CITAS", "No hay citas para mostrar.")
                    tvSinCitas.visibility = TextView.VISIBLE
                    rvCitasMedico.visibility = RecyclerView.GONE
                } else {
                    tvSinCitas.visibility = TextView.GONE
                    rvCitasMedico.visibility = RecyclerView.VISIBLE

                    rvCitasMedico.layoutManager = LinearLayoutManager(this)
                    rvCitasMedico.adapter = CitasDoctorAdapter(listaCitas) { cita ->
                        val intent = Intent(this, DetalleCitaDoctorActivity::class.java)
                        intent.putExtra("idCita", cita.id)
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show()
            }
    }


}
