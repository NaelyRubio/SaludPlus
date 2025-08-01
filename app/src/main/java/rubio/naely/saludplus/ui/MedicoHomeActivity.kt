package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import rubio.naely.saludplus.R
import rubio.naely.saludplus.utils.NavMedicoActivity

class MedicoHomeActivity : NavMedicoActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var btnVerMasProximas: Button
    private lateinit var btnDia: Button
    private lateinit var btnSemana: Button
    private lateinit var btnRegistrarHorario: Button

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

        // Configurar barra de navegación inferior del médico
        configurarNavegacionInferior(navHome, navRegistroHorario, navHorarios, navPerfil, "home")
    }

    private fun bindViews() {
        tvSaludo = findViewById(R.id.tvSaludo)
        btnVerMasProximas = findViewById(R.id.btnVerMasProximas)
        btnDia = findViewById(R.id.btnDia)
        btnSemana = findViewById(R.id.btnSemana)
        btnRegistrarHorario = findViewById(R.id.btnRegistrarHorario)

        navHome = findViewById(R.id.navHome)
        navRegistroHorario = findViewById(R.id.navRegistroHorario)
        navHorarios = findViewById(R.id.navhorarios)
        navPerfil = findViewById(R.id.navPerfil)
    }

    private fun setupListeners() {
        btnDia.setOnClickListener {
            seleccionarFiltro(btnDia, btnSemana)
            // Aquí puedes cargar las citas del día
        }

        btnSemana.setOnClickListener {
            seleccionarFiltro(btnSemana, btnDia)
            // Aquí puedes cargar las citas de la semana
        }

        btnRegistrarHorario.setOnClickListener {
            val intent = Intent(this, DisponibilidadActivity::class.java)
            startActivity(intent)
        }

        btnVerMasProximas.setOnClickListener {
            // Por si luego agregas lista completa de citas
        }
    }

    private fun seleccionarFiltro(seleccionado: Button, otro: Button) {
        seleccionado.setBackgroundResource(R.drawable.bg_segmented_selected)
        seleccionado.setTextColor(getColor(R.color.white))
        otro.setBackgroundResource(R.drawable.bg_segmented_unselected)
        otro.setTextColor(getColor(R.color.gris_text))
    }
}
