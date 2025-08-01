package rubio.naely.saludplus.adapter

import Horario
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rubio.naely.saludplus.R

class HorariosAdapter(
    private val listaHorarios: List<Horario>,
    private val onEditar: (Horario) -> Unit,
    private val onEliminar: (Horario) -> Unit
) : RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder>() {

    class HorarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDias: TextView = view.findViewById(R.id.tvDias)
        val tvHorario: TextView = view.findViewById(R.id.tvHorario)
        val tvDuracion: TextView = view.findViewById(R.id.tvDuracion)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditarHorario)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarHorario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_horariotrabajomedico, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val horario = listaHorarios[position]

        holder.tvDias.text = horario.dias.joinToString(", ")
        holder.tvHorario.text = "${horario.horaInicio} - ${horario.horaFin}"
        holder.tvDuracion.text = "Duración: ${horario.duracion} minutos"
        holder.tvPrecio.text = "Precio: $${horario.precio}"

        holder.btnEditar.setOnClickListener {
            onEditar(horario)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(horario)
        }
    }

    override fun getItemCount(): Int = listaHorarios.size
}
