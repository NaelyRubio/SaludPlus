package rubio.naely.saludplus.ui

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.R
import java.util.*

class DisponibilidadActivity : AppCompatActivity() {

    private lateinit var etHoraInicio: TextView
    private lateinit var etHoraFin: TextView
    private lateinit var btn30min: Button
    private lateinit var btn1hora: Button
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var duracionConsulta = 30

    private val diasSeleccionados = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disponibilidadmedico)

        etHoraInicio = findViewById(R.id.etHoraInicio)
        etHoraFin = findViewById(R.id.etHoraFin)
        btn30min = findViewById(R.id.btn30min)
        btn1hora = findViewById(R.id.btn1hora)
        btnAceptar = findViewById(R.id.btnAceptarDisponibilidad)
        btnCancelar = findViewById(R.id.btnCancelarDisponibilidad)

        val chipDomingo = findViewById<TextView>(R.id.chipDomingo)
        val chipLunes = findViewById<TextView>(R.id.chipLunes)
        val chipMartes = findViewById<TextView>(R.id.chipMartes)
        val chipMiercoles = findViewById<TextView>(R.id.chipMiercoles)
        val chipJueves = findViewById<TextView>(R.id.chipJueves)
        val chipViernes = findViewById<TextView>(R.id.chipViernes)
        val chipSabado = findViewById<TextView>(R.id.chipSabado)

        val chipsDias = listOf(
            chipDomingo to "D",
            chipLunes to "L",
            chipMartes to "M",
            chipMiercoles to "X",
            chipJueves to "J",
            chipViernes to "V",
            chipSabado to "S"
        )

        chipsDias.forEach { (chip, letra) ->
            chip.setOnClickListener {
                chip.isSelected = !chip.isSelected
                chip.setTextColor(if (chip.isSelected) Color.WHITE else Color.BLACK)

                if (chip.isSelected) {
                    diasSeleccionados.add(letra)
                } else {
                    diasSeleccionados.remove(letra)
                }
            }
        }

        // Desactivar teclado manual
        etHoraInicio.isFocusable = false
        etHoraInicio.isClickable = true
        etHoraFin.isFocusable = false
        etHoraFin.isClickable = true

        // Mostrar TimePicker al hacer click
        etHoraInicio.setOnClickListener {
            mostrarTimePicker(etHoraInicio)
        }

        etHoraFin.setOnClickListener {
            mostrarTimePicker(etHoraFin)
        }

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

    private fun mostrarTimePicker(editText: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(
            this,
            { _, hourOfDay, selectedMinute ->
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                val horaFormateada = String.format("%02d:%02d %s", hour12, selectedMinute, amPm)
                editText.setText(horaFormateada)
            },
            hour,
            minute,
            false
        )
        picker.show()
    }

    private fun seleccionarIntervalo(seleccionado: Button, otro: Button) {
        seleccionado.setBackgroundResource(R.drawable.bg_segmented_selected)
        seleccionado.setTextColor(getColor(R.color.white))
        otro.setBackgroundResource(R.drawable.bg_segmented_unselected)
        otro.setTextColor(getColor(R.color.gris_text))
    }

    private fun guardarHorario() {
        val uid = auth.currentUser?.uid ?: return

        val horaInicio = etHoraInicio.text.toString()
        val horaFin = etHoraFin.text.toString()

        if (diasSeleccionados.isEmpty() || horaInicio.isBlank() || horaFin.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val horario = mapOf(
            "dias" to diasSeleccionados.toList(),
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
    }

}

