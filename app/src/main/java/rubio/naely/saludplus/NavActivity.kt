package rubio.naely.saludplus.utils

import rubio.naely.saludplus.ui.PacienteHomeActivity
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rubio.naely.saludplus.ListaMedicosActivity
import rubio.naely.saludplus.ui.*

open class NavActivity : AppCompatActivity() {

    fun configurarNavegacionInferior(
        navHome: ImageView,
        navCitas: ImageView,
        navMedicos: ImageView,
        navPerfil: ImageView,
        actual: String
    ) {
        navHome.setOnClickListener {
            if (actual != "home") {
                startActivity(Intent(this, PacienteHomeActivity::class.java))
            }
        }

        navCitas.setOnClickListener {
            if (actual != "citas") {
                startActivity(Intent(this, MisCitasActivity::class.java))
            }
        }

        navMedicos.setOnClickListener {
            if (actual != "medicos") {
                startActivity(Intent(this, ListaMedicosActivity::class.java))
            }
        }

        navPerfil.setOnClickListener {
            if (actual != "perfil") {
                startActivity(Intent(this, PerfilPacienteActivity::class.java))
            } else {
                Toast.makeText(this, "Ya estás en perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
