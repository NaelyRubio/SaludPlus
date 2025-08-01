package rubio.naely.saludplus.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import rubio.naely.saludplus.R
import rubio.naely.saludplus.model.Cita
import com.bumptech.glide.Glide
import rubio.naely.saludplus.DetalleCitaPacienteActivity

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

        // Cambiar color del estado
        when (cita.estado.lowercase()) {
            "completada" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_aceptada)
            "cancelada" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_cancelada)
        }

        // Imagen del doctor
        Glide.with(holder.itemView.context)
            .load(cita.fotoDoctor)
            .placeholder(R.drawable.doc2)
            .into(holder.imgDoctor)

        // Acción al hacer clic en Ver Cita
        holder.btnVerCita.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleCitaPacienteActivity::class.java)

            intent.putExtra("idCita", cita.id)
            intent.putExtra("nombreDoctor", cita.nombreDoctor)
            intent.putExtra("especialidad", cita.especialidad)
            intent.putExtra("fechaHora", "${cita.fecha} - ${cita.hora}")
            intent.putExtra("motivoConsulta", cita.motivo)
            intent.putExtra("estado", cita.estado)
            intent.putExtra("nombrePaciente", cita.nombrePaciente)
            intent.putExtra("fotoPaciente", cita.fotoPaciente)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaCitas.size
}
