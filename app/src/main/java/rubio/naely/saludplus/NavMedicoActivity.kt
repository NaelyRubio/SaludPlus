package rubio.naely.saludplus.utils

import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rubio.naely.saludplus.ListHorarioMedicoActivity
import rubio.naely.saludplus.ui.DisponibilidadActivity
import rubio.naely.saludplus.ui.MedicoHomeActivity

import rubio.naely.saludplus.ui.PerfilMedicoActivity

open class NavMedicoActivity : AppCompatActivity() {

    fun configurarNavegacionInferior(
        navHome: ImageView,
        navRegistroHorario: ImageView,
        navHorarios: ImageView,
        navPerfil: ImageView,
        actual: String
    ) {
        navHome.setOnClickListener {
            if (actual != "home") {
                startActivity(Intent(this, MedicoHomeActivity::class.java))
                finish()
            }
        }

        navRegistroHorario.setOnClickListener {
            if (actual != "registrar") {
                startActivity(Intent(this, DisponibilidadActivity::class.java)) // O la que uses
                finish()
            }
        }

        navHorarios.setOnClickListener {
            if (actual != "horarios") {
                startActivity(Intent(this, ListHorarioMedicoActivity::class.java))
                finish()
            }
        }

        navPerfil.setOnClickListener {
            if (actual != "perfil") {
                startActivity(Intent(this, PerfilMedicoActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Ya estás en perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
