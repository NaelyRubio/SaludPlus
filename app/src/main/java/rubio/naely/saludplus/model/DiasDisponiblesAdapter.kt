package rubio.naely.saludplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rubio.naely.saludplus.R

class DiasDisponiblesAdapter(
    private val dias: List<String>,
    private val onDiaSeleccionado: (String) -> Unit
) : RecyclerView.Adapter<DiasDisponiblesAdapter.DiaViewHolder>() {

    private var diaSeleccionado: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_disponible, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val dia = dias[position]
        holder.bind(dia, dia == diaSeleccionado)

        holder.itemView.setOnClickListener {
            diaSeleccionado = dia
            notifyDataSetChanged() // Actualiza los estilos visuales
            onDiaSeleccionado(dia)
        }
    }

    override fun getItemCount(): Int = dias.size

    inner class DiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDia: TextView = itemView.findViewById(R.id.tvDia)

        fun bind(dia: String, seleccionado: Boolean) {
            tvDia.text = dia
            tvDia.setBackgroundResource(
                if (seleccionado) R.drawable.bg_chip_dia_selected else R.drawable.bg_chip_dia_selector
            )
            tvDia.setTextColor(
                itemView.context.getColor(
                    if (seleccionado) android.R.color.white else android.R.color.black
                )
            )
        }
    }
}
