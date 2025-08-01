package rubio.naely.saludplus.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rubio.naely.saludplus.DetalleCitaDoctorActivity
import rubio.naely.saludplus.R
import rubio.naely.saludplus.model.Cita

class DetalleCitaMedicoAdapter(private val listaCitas: List<Cita>) :
    RecyclerView.Adapter<DetalleCitaMedicoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPaciente: ImageView = view.findViewById(R.id.imgDoctor)
        val tvNombrePaciente: TextView = view.findViewById(R.id.tvNombreDoctor)
        val tvMotivo: TextView = view.findViewById(R.id.tvEspecialidad)
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

        holder.tvNombrePaciente.text = cita.nombrePaciente
        holder.tvMotivo.text = cita.motivo
        holder.tvEstado.text = cita.estado

        Glide.with(holder.itemView.context)
            .load(cita.fotoPaciente)
            .placeholder(R.drawable.perfil)
            .into(holder.imgPaciente)

        holder.btnVerCita.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleCitaDoctorActivity::class.java)

            intent.putExtra("citaId", cita.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaCitas.size
}
