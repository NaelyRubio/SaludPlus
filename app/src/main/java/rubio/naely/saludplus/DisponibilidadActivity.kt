package rubio.naely.saludplus.ui

import Horario
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import rubio.naely.saludplus.R
import rubio.naely.saludplus.utils.NavMedicoActivity
import java.util.*

class DisponibilidadActivity : NavMedicoActivity() {

    private lateinit var etHoraInicio: TextView
    private lateinit var etHoraFin: TextView
    private lateinit var btn30min: Button
    private lateinit var btn1hora: Button
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button

    private lateinit var chipsDias: List<Pair<TextView, String>>

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var duracionConsulta = 30
    private val diasSeleccionados = mutableSetOf<String>()


    private var modoEdicion = false
    private var horarioOriginal: Horario? = null
    private lateinit var etPrecioConsulta: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disponibilidadmedico)

        etHoraInicio = findViewById(R.id.etHoraInicio)
        etHoraFin = findViewById(R.id.etHoraFin)
        btn30min = findViewById(R.id.btn30min)
        btn1hora = findViewById(R.id.btn1hora)
        btnAceptar = findViewById(R.id.btnAceptarDisponibilidad)
        btnCancelar = findViewById(R.id.btnCancelarDisponibilidad)
        etPrecioConsulta = findViewById(R.id.etPrecioConsulta)

        val chipDomingo = findViewById<TextView>(R.id.chipDomingo)
        val chipLunes = findViewById<TextView>(R.id.chipLunes)
        val chipMartes = findViewById<TextView>(R.id.chipMartes)
        val chipMiercoles = findViewById<TextView>(R.id.chipMiercoles)
        val chipJueves = findViewById<TextView>(R.id.chipJueves)
        val chipViernes = findViewById<TextView>(R.id.chipViernes)
        val chipSabado = findViewById<TextView>(R.id.chipSabado)

        chipsDias = listOf(
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
                if (chip.isSelected) diasSeleccionados.add(letra) else diasSeleccionados.remove(letra)
            }
        }

        etHoraInicio.isFocusable = false
        etHoraInicio.setOnClickListener { mostrarTimePicker(etHoraInicio) }

        etHoraFin.isFocusable = false
        etHoraFin.setOnClickListener { mostrarTimePicker(etHoraFin) }

        btn30min.setOnClickListener {
            duracionConsulta = 30
            seleccionarIntervalo(btn30min, btn1hora)
        }

        btn1hora.setOnClickListener {
            duracionConsulta = 60
            seleccionarIntervalo(btn1hora, btn30min)
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnAceptar.setOnClickListener { guardarHorario() }
        btnCancelar.setOnClickListener { finish() }

        horarioOriginal = intent.getSerializableExtra("horarioEdit") as? Horario
        horarioOriginal?.let { horario ->
            modoEdicion = true
            etHoraInicio.text = horario.horaInicio
            etHoraFin.text = horario.horaFin
            duracionConsulta = horario.duracion

            if (duracionConsulta == 60) seleccionarIntervalo(btn1hora, btn30min)
            else seleccionarIntervalo(btn30min, btn1hora)

            horario.dias.forEach { letra ->
                chipsDias.find { it.second == letra }?.first?.apply {
                    isSelected = true
                    setTextColor(Color.WHITE)
                }
                diasSeleccionados.add(letra)
            }
        }

        configurarNavegacionInferior(
            findViewById(R.id.navHome),
            findViewById(R.id.navRegistroHorario),
            findViewById(R.id.navhorarios),
            findViewById(R.id.navPerfil),
            "registrar"
        )
    }

    private fun getUidActual(): String = auth.currentUser?.uid ?: ""

    private fun mostrarTimePicker(editText: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val picker = TimePickerDialog(this, { _, h, m ->
            val amPm = if (h >= 12) "PM" else "AM"
            val h12 = if (h % 12 == 0) 12 else h % 12
            editText.text = String.format("%02d:%02d %s", h12, m, amPm)
        }, hour, minute, false)

        picker.show()
    }

    private fun seleccionarIntervalo(seleccionado: Button, otro: Button) {
        seleccionado.setBackgroundResource(R.drawable.bg_segmented_selected)
        seleccionado.setTextColor(getColor(R.color.white))
        otro.setBackgroundResource(R.drawable.bg_segmented_unselected)
        otro.setTextColor(getColor(R.color.gris_text))
    }

    private fun guardarHorario() {
        val uid = getUidActual()

        if (uid.isEmpty()) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
            Log.e("DISPONIBILIDAD", "UID vacío, usuario no autenticado")
            return
        }

        val horaInicio = etHoraInicio.text.toString()
        val horaFin = etHoraFin.text.toString()

        if (diasSeleccionados.isEmpty() || horaInicio.isBlank() || horaFin.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            Log.w("DISPONIBILIDAD", "Campos incompletos")
            return
        }

        val horarioMap = mapOf(
            "dias" to diasSeleccionados.toList(),
            "horaInicio" to horaInicio,
            "horaFin" to horaFin,
            "duracion" to duracionConsulta,
            "precio" to (etPrecioConsulta.text.toString().toDoubleOrNull() ?: 0.0)

        )

        Log.d("DISPONIBILIDAD", "UID actual: $uid")
        Log.d("DISPONIBILIDAD", "Datos a guardar: $horarioMap")

        if (modoEdicion && horarioOriginal != null) {
            db.collection("disponibilidad")
                .document(uid)
                .collection("horarios")
                .whereEqualTo("horaInicio", horarioOriginal!!.horaInicio)
                .whereEqualTo("horaFin", horarioOriginal!!.horaFin)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        Toast.makeText(this, "No se encontró el horario original", Toast.LENGTH_SHORT).show()
                        Log.e("DISPONIBILIDAD", "No se encontró el horario original para editar")
                        return@addOnSuccessListener
                    }
                    for (doc in result.documents) {
                        doc.reference.set(horarioMap)
                        Log.d("DISPONIBILIDAD", "Horario actualizado con ID: ${doc.id}")
                    }
                    Toast.makeText(this, "Horario actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    Log.e("DISPONIBILIDAD", "Error al actualizar: ${e.message}", e)
                }

        } else {
            val idUnico = UUID.randomUUID().toString()
            Log.d("DISPONIBILIDAD", "ID único generado: $idUnico")

            db.collection("disponibilidad")
                .document(uid)
                .collection("horarios")
                .document(idUnico)
                .set(horarioMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Horario guardado correctamente", Toast.LENGTH_SHORT).show()
                    Log.d("DISPONIBILIDAD", "Guardado exitoso en: disponibilidad/$uid/horarios/$idUnico")

                    db.collection("usuarios").document(uid)
                        .update("precio", (etPrecioConsulta.text.toString().toDoubleOrNull() ?: 0.0))

                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("DISPONIBILIDAD", "Error al guardar: ${e.message}", e)
                }

        }
    }

}
