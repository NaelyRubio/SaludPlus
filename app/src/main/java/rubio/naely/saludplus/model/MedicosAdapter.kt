package rubio.naely.saludplus.adapters

import Medico
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rubio.naely.saludplus.R

class MedicosAdapter(
    private val listaMedicos: List<Medico>,
    private val onMedicoClick: (Medico) -> Unit
) : RecyclerView.Adapter<MedicosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgDoctor: ImageView = view.findViewById(R.id.imgDoctor)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreDoctor)
        val tvEspecialidad: TextView = view.findViewById(R.id.tvEspecialidad)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)
        val tvCalificacion: TextView = view.findViewById(R.id.tvCalificacion)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val btnVerDoctor: ImageButton = view.findViewById(R.id.btnVerDoctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_doctorinfo, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount(): Int = listaMedicos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medico = listaMedicos[position]

        holder.tvNombre.text = medico.nombre
        holder.tvEspecialidad.text = medico.especialidad
        holder.tvDireccion.text = medico.direccion
        holder.tvCalificacion.text = medico.calificacion.toString()
        holder.tvPrecio.text = "$${medico.precio} MXN"


        Glide.with(holder.itemView.context)
            .load(medico.imagenUrl)
            .placeholder(R.drawable.perfil)
            .error(R.drawable.perfil)
            .into(holder.imgDoctor)

        holder.btnVerDoctor.setOnClickListener {
            onMedicoClick(medico)
        }
    }
}
