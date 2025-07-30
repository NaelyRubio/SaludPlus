package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import rubio.naely.saludplus.R
import rubio.naely.saludplus.utils.NavActivity

class MedicoHomeActivity : NavActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var btnVerMasProximas: Button
    private lateinit var btnDia: Button
    private lateinit var btnSemana: Button
    private lateinit var btnRegistrarHorario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicohome)

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        tvSaludo = findViewById(R.id.tvSaludo)
        btnVerMasProximas = findViewById(R.id.btnVerMasProximas)
        btnDia = findViewById(R.id.btnDia)
        btnSemana = findViewById(R.id.btnSemana)
        btnRegistrarHorario = findViewById(R.id.btnRegistrarHorario)
    }

    private fun setupListeners() {
        btnDia.setOnClickListener {
            seleccionarFiltro(btnDia, btnSemana)
            // Aquí puedes cambiar la vista a citas del día
        }

        btnSemana.setOnClickListener {
            seleccionarFiltro(btnSemana, btnDia)
            // Aquí puedes cambiar la vista a citas de la semana
        }

        btnRegistrarHorario.setOnClickListener {
            val intent = Intent(this, DisponibilidadActivity::class.java)
            startActivity(intent)
        }

        btnVerMasProximas.setOnClickListener {
            // Redirigir a lista completa de citas si decides implementarlo
          //  val intent = Intent(this, ListaCitasActivity::class.java)
            // startActivity(intent)
        }

        btnRegistrarHorario.setOnClickListener {
            val intent = Intent(this, DisponibilidadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun seleccionarFiltro(seleccionado: Button, otro: Button) {
        seleccionado.setBackgroundResource(R.drawable.bg_segmented_selected)
        seleccionado.setTextColor(getColor(R.color.white))
        otro.setBackgroundResource(R.drawable.bg_segmented_unselected)
        otro.setTextColor(getColor(R.color.gris_text))
    }


}
