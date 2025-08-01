package rubio.naely.saludplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import rubio.naely.saludplus.R

class HorasDisponiblesAdapter(
    private val horas: List<String>,
    private val onHoraSeleccionada: (String) -> Unit
) : RecyclerView.Adapter<HorasDisponiblesAdapter.HoraViewHolder>() {

    private var horaSeleccionada: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hora, parent, false)
        return HoraViewHolder(view)
    }

    override fun getItemCount(): Int = horas.size

    override fun onBindViewHolder(holder: HoraViewHolder, position: Int) {
        val hora = horas[position]
        holder.bind(hora, hora == horaSeleccionada)

        holder.itemView.setOnClickListener {
            horaSeleccionada = hora
            notifyDataSetChanged()
            onHoraSeleccionada(hora)
        }
    }

    class HoraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        private val cardHora: CardView = itemView.findViewById(R.id.cardHora)

        fun bind(hora: String, seleccionado: Boolean) {
            tvHora.text = hora
            if (seleccionado) {
                cardHora.setCardBackgroundColor(itemView.context.getColor(R.color.black))
                tvHora.setTextColor(itemView.context.getColor(android.R.color.white))
            } else {
                cardHora.setCardBackgroundColor(itemView.context.getColor(R.color.white))
                tvHora.setTextColor(itemView.context.getColor(R.color.black))
            }
        }
    }
}
