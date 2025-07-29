package rubio.naely.saludplus.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import rubio.naely.saludplus.AgendarCitaActivity
import rubio.naely.saludplus.R

class MisCitasActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnNotificaciones: ImageView
    private lateinit var btnAgendarCita: Button
    private lateinit var tvRecordatorio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miscitas)

        bindViews()
        setListeners()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        btnNotificaciones = findViewById(R.id.btnNotificaciones)
        btnAgendarCita = findViewById(R.id.btnAgendarCita)
        tvRecordatorio = findViewById(R.id.tvRecordatorio)
    }

    private fun setListeners() {
        btnBack.setOnClickListener {
            finish() // Regresa a la pantalla anterior
        }

        btnNotificaciones.setOnClickListener {
            // Aquí puedes abrir una pantalla de notificaciones si la tienes
        }

        btnAgendarCita.setOnClickListener {
            startActivity(Intent(this, AgendarCitaActivity::class.java))
        }
    }
}
