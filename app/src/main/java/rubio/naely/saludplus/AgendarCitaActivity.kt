package rubio.naely.saludplus

import Horario
import Medico
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.adapters.DiasDisponiblesAdapter
import rubio.naely.saludplus.adapters.HorasDisponiblesAdapter
import rubio.naely.saludplus.utils.NavPacienteActivity

class AgendarCitaActivity : NavPacienteActivity() {

    private lateinit var tvNombreDoctor: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var imgDoctor: ImageView
    private lateinit var etMotivo: EditText
    private lateinit var rvDias: RecyclerView
    private lateinit var rvHoras: RecyclerView
    private lateinit var btnAgendar: Button
    private lateinit var btnCancelar: Button

    private var horarios: List<Horario> = emptyList()
    private var diaSeleccionado: String? = null
    private var horaSeleccionada: String? = null
    private var medicoId: String? = null
    private lateinit var medico: Medico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendarcitapaciente)

        bindViews()

        medicoId = intent.getStringExtra("medicoId")

        if (medicoId == null) {
            Toast.makeText(this, "Error: No se encontró el ID del médico", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosMedico()
        cargarHorarios()

        btnCancelar.setOnClickListener { finish() }

        btnAgendar.setOnClickListener {
            val motivo = etMotivo.text.toString().trim()
            if (diaSeleccionado == null || horaSeleccionada == null || motivo.isEmpty()) {
                Toast.makeText(this, "Selecciona un día, hora y escribe el motivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            guardarCita(motivo)
        }
    }

    private fun bindViews() {
        tvNombreDoctor = findViewById(R.id.tvNombreDoctor)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        imgDoctor = findViewById(R.id.imgDoctor)
        etMotivo = findViewById(R.id.etMotivo)
        rvDias = findViewById(R.id.rvDiasDisponibles)
        rvHoras = findViewById(R.id.rvHorariosDisponibles)
        btnAgendar = findViewById(R.id.btnAgendar)
        btnCancelar = findViewById(R.id.btnCancelar)

        rvDias.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvHoras.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun cargarDatosMedico() {
        FirebaseFirestore.getInstance().collection("usuarios").document(medicoId!!)
            .get()
            .addOnSuccessListener { doc ->
                medico = doc.toObject(Medico::class.java)!!.copy(id = doc.id)
                tvNombreDoctor.text = medico.nombre
                tvEspecialidad.text = medico.especialidad
                Glide.with(this).load(medico.imagenUrl).into(imgDoctor)
            }
    }


    private fun cargarHorarios() {
        FirebaseFirestore.getInstance()
            .collection("disponibilidad")
            .document(medicoId!!)
            .collection("horarios")
            .get()
            .addOnSuccessListener { result ->
                horarios = result.map { it.toObject(Horario::class.java) }

                val diasUnicos = horarios.flatMap { it.dias }.distinct()

                rvDias.adapter = DiasDisponiblesAdapter(diasUnicos) { dia ->
                    diaSeleccionado = dia
                    cargarHorasParaDia(dia)
                }
            }
    }

    private fun cargarHorasParaDia(dia: String) {
        Log.d("AgendarCita", "Día seleccionado: $dia")

        val horasGeneradas = mutableListOf<String>()

        val horariosFiltrados = horarios.filter { it.dias.contains(dia) }
        Log.d("AgendarCita", "Horarios encontrados para $dia: ${horariosFiltrados.size}")

        horariosFiltrados.forEach { horario ->
            Log.d("AgendarCita", "Procesando horario: ${horario.horaInicio} - ${horario.horaFin}")
            val horaInicio = convertirHora(horario.horaInicio)
            val horaFin = convertirHora(horario.horaFin)
            val intervalo = horario.duracion

            var actual = horaInicio
            while (actual < horaFin) {
                val horaFormateada = formatearHora(actual)
                horasGeneradas.add(horaFormateada)
                Log.d("AgendarCita", "Generada: $horaFormateada")
                actual += intervalo
            }
        }

        // Simula fecha por ahora
        val fecha = "2025-07-31"
        FirebaseFirestore.getInstance().collection("citas")
            .whereEqualTo("idDoctor", medicoId)
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { result ->
                val horasOcupadas = result.mapNotNull { it.getString("hora") }
                Log.d("AgendarCita", "Horas ocupadas: $horasOcupadas")

                val horasDisponibles = horasGeneradas.filter { it !in horasOcupadas }
                Log.d("AgendarCita", "Final: ${horasDisponibles.size} horas disponibles")

                rvHoras.adapter = HorasDisponiblesAdapter(horasDisponibles) { hora ->
                    horaSeleccionada = hora
                }
            }
    }


    private fun convertirHora(hora12: String): Int {
        val parts = hora12.split(" ", ":")
        var h = parts[0].toInt()
        val m = parts[1].toInt()
        val amPm = parts[2]
        if (amPm == "PM" && h != 12) h += 12
        if (amPm == "AM" && h == 12) h = 0
        return h * 60 + m
    }

    private fun formatearHora(minutos: Int): String {
        val h = minutos / 60
        val m = minutos % 60
        val amPm = if (h < 12) "AM" else "PM"
        val h12 = if (h % 12 == 0) 12 else h % 12
        return String.format("%02d:%02d %s", h12, m, amPm)
    }

    private fun guardarCita(motivo: String) {
        val idPaciente = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val fecha = "2025-07-31"
        Log.d("CITA", "Guardando cita con especialidad: ${medico.especialidad}")

        val cita = hashMapOf(
            "idPaciente" to idPaciente,
            "idDoctor" to medicoId,
            "nombreDoctor" to medico.nombre,
            "especialidad" to medico.especialidad,
            "fotoDoctor" to medico.imagenUrl,
            "fecha" to fecha,
            "hora" to horaSeleccionada,
            "fechaHora" to "$fecha - $horaSeleccionada",
            "motivo" to motivo,
            "estado" to "pendiente"
        )

        db.collection("citas")
            .add(cita)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita agendada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agendar", Toast.LENGTH_SHORT).show()
            }
    }


}

