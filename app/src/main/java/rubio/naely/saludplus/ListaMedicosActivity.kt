package rubio.naely.saludplus

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListaMedicosActivity: AppCompatActivity(){

    private lateinit var btnBack: ImageView
    private lateinit var btnNotificaciones: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listamedicos)

        //Vincula las vistas
        btnBack = findViewById(R.id.btnBack)
        btnNotificaciones = findViewById(R.id.btnNotificaciones)

        //Boton de regresar
        btnBack.setOnClickListener{
            finish()//Cierra la actividad y regresa a la anterior.
        }

        //Boton de notificaciones
        btnNotificaciones.setOnClickListener{
            Toast.makeText(this,  "Aquí iría la pantalla de notificaciones", Toast.LENGTH_SHORT).show()
        }
    }
}