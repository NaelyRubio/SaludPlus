package rubio.naely.saludplus.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import rubio.naely.saludplus.R
import rubio.naely.saludplus.model.Cita
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HistorialCitasAdapter(private val listaCitas: List<Cita>) :
    RecyclerView.Adapter<HistorialCitasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDoctor: ImageView = view.findViewById(R.id.imgDoctor)
        val tvNombreDoctor: TextView = view.findViewById(R.id.tvNombreDoctor)
        val tvEspecialidad: TextView = view.findViewById(R.id.tvEspecialidad)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnVerCita: ImageButton = view.findViewById(R.id.btnVerCita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_historial, parent, false)
        return ViewHolder(vista)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cita = listaCitas[position]

        holder.tvNombreDoctor.text = cita.nombreDoctor
        holder.tvEspecialidad.text = cita.especialidad
        holder.tvEstado.text = cita.estado

        // Cambia el color según el estado
        when (cita.estado.lowercase()) {
            "completada" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_aceptada)

            "cancelada" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_cancelada)
        }

        // Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(cita.fotoDoctor)
            .placeholder(R.drawable.doc2)
            .into(holder.imgDoctor)

        // Acción del botón
        holder.btnVerCita.setOnClickListener {
            // Aquí puedes abrir detalle de cita si quieres
        }
    }

    override fun getItemCount(): Int = listaCitas.size

}

