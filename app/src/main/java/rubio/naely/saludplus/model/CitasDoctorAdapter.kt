package rubio.naely.saludplus.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rubio.naely.saludplus.R
import rubio.naely.saludplus.model.Cita

class CitasDoctorAdapter(
    private val listaCitas: List<Cita>,
    private val onVerCitaClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitasDoctorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPaciente: ImageView = view.findViewById(R.id.imgPaciente)
        val tvNombrePaciente: TextView = view.findViewById(R.id.tvNombrePaciente)
        val tvMotivo: TextView = view.findViewById(R.id.tvMotivo)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnVerCita: ImageButton = view.findViewById(R.id.btnVerCita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_citadoctor, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cita = listaCitas[position]

        holder.tvNombrePaciente.text = cita.nombrePaciente
        holder.tvMotivo.text = cita.motivo
        holder.tvEstado.text = cita.estado

        // Colores según estado
        when (cita.estado.lowercase()) {
            "pendiente" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_pendiente)
            "aceptada" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_aceptada)
            "cancelada" -> holder.tvEstado.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_estado_cancelada)
        }

        // Imagen del paciente
        Glide.with(holder.itemView.context)
            .load(cita.fotoPaciente)
            .placeholder(R.drawable.perfil)
            .into(holder.imgPaciente)

        holder.btnVerCita.setOnClickListener {
            onVerCitaClick(cita)
        }
    }

    override fun getItemCount(): Int = listaCitas.size
}
