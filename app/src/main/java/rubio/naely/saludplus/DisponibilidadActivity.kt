package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.R

class DisponibilidadActivity : AppCompatActivity() {

    private lateinit var etHoraInicio: EditText
    private lateinit var etHoraFin: EditText
    private lateinit var btn30min: Button
    private lateinit var btn1hora: Button
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var duracionConsulta = 30 // default

    private lateinit var chipsDias: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disponibilidadmedico)

        etHoraInicio = findViewById(R.id.etHoraInicio)
        etHoraFin = findViewById(R.id.etHoraFin)
        btn30min = findViewById(R.id.btn30min)
        btn1hora = findViewById(R.id.btn1hora)
        btnAceptar = findViewById(R.id.btnAceptarDisponibilidad)
        btnCancelar = findViewById(R.id.btnCancelarDisponibilidad)

        chipsDias = listOf(
            findViewById(R.id.chipLunes)
            // Agrega aquí los demás chips si los agregas visualmente
        )

        btn30min.setOnClickListener {
            duracionConsulta = 30
            seleccionarIntervalo(btn30min, btn1hora)
        }

        btn1hora.setOnClickListener {
            duracionConsulta = 60
            seleccionarIntervalo(btn1hora, btn30min)
        }

        btnAceptar.setOnClickListener {
            guardarHorario()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun seleccionarIntervalo(seleccionado: Button, otro: Button) {
        seleccionado.setBackgroundResource(R.drawable.bg_segmented_selected)
        seleccionado.setTextColor(getColor(R.color.white))
        otro.setBackgroundResource(R.drawable.bg_segmented_unselected)
        otro.setTextColor(getColor(R.color.gris_text))
    }

    private fun guardarHorario() {
        val uid = auth.currentUser?.uid ?: return

        val diasSeleccionados = chipsDias.filter {
            it.background.constantState == getDrawable(R.drawable.bg_chip_dia_selected)?.constantState
        }.map { it.text.toString() }

        val horaInicio = etHoraInicio.text.toString()
        val horaFin = etHoraFin.text.toString()

        if (diasSeleccionados.isEmpty() || horaInicio.isBlank() || horaFin.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val horario = mapOf(
            "dias" to diasSeleccionados,
            "horaInicio" to horaInicio,
            "horaFin" to horaFin,
            "duracion" to duracionConsulta
        )

        db.collection("disponibilidad").document(uid).set(horario)
            .addOnSuccessListener {
                Toast.makeText(this, "Horario guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }

        val btnRegistrarHorario = findViewById<Button>(R.id.btnRegistrarHorario)
        btnRegistrarHorario.setOnClickListener {
            val intent = Intent(this, DisponibilidadActivity::class.java)
            startActivity(intent)
        }

    }

}
