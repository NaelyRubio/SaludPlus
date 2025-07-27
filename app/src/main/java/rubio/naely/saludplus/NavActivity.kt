package rubio.naely.saludplus

import android.content.Intent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

open class NavActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        // HOME
        findViewById<ImageView?>(R.id.navHome)?.setOnClickListener {
            if (this !is PacienteHomeActivity) {
                startActivity(Intent(this, PacienteHomeActivity::class.java))
            }
        }

        // CITAS  Agendar cita
        findViewById<ImageView?>(R.id.navCitas)?.setOnClickListener {
            if (this !is AgendarCitaActivity) {
                startActivity(Intent(this, AgendarCitaActivity::class.java))
            }
        }

        // MÉDICOS  Lista de médicos
        findViewById<ImageView?>(R.id.navMedicos)?.setOnClickListener {
            if (this !is ListaMedicosActivity) {
                startActivity(Intent(this, ListaMedicosActivity::class.java))
            }
        }

        // PERFIL  Perfil paciente
        findViewById<ImageView?>(R.id.navPerfil)?.setOnClickListener {
            if (this !is PerfilPacienteActivity) {
                startActivity(Intent(this, PerfilPacienteActivity::class.java))
            }
        }
    }
}
