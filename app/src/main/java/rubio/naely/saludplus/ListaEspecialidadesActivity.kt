package rubio.naely.saludplus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import rubio.naely.saludplus.R

class ListaEspecialidadesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var layoutEspecialidades: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listamedicos)

        btnBack = findViewById(R.id.btnBack)
        layoutEspecialidades = findViewById(R.id.layoutEspecialidades)

        btnBack.setOnClickListener {
            finish()
        }

        mostrarEspecialidades()
    }

    private fun mostrarEspecialidades() {
        val especialidades = listOf(
            "Odontología",
            "Cardiología",
            "Neurología",
            "Pediatría",
            "Dermatología",
            "Psicología",
            "Ginecología",
            "Oftalmología"
        )

        for (nombre in especialidades) {
            val card = LayoutInflater.from(this).inflate(R.layout.card_especialidad, layoutEspecialidades, false)

            val tvNombre = card.findViewById<TextView>(R.id.tvNombreEspecialidad)
            tvNombre.text = nombre

            layoutEspecialidades.addView(card)
        }
    }
}
