package rubio.naely.saludplus

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FiltroEspecialidades(
    private val especialidades: List<String>,
    private val especialidadesSeleccionadas: List<String>,
    private val onAplicarFiltro: (List<String>) -> Unit
) : DialogFragment() {

    private val seleccionadas = mutableSetOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.filtro_especialidades, null)

        val layoutEspecialidades = view.findViewById<LinearLayout>(R.id.layoutEspecialidades)
        val btnAplicar = view.findViewById<Button>(R.id.btnAplicarFiltro)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarFiltro)

        // Crear dinámicamente los checkboxes
        especialidades.forEach { especialidad ->
            val checkBox = CheckBox(requireContext())
            checkBox.text = especialidad
            checkBox.isChecked = especialidadesSeleccionadas.contains(especialidad)

            if (checkBox.isChecked) {
                seleccionadas.add(especialidad)
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    seleccionadas.add(especialidad)
                } else {
                    seleccionadas.remove(especialidad)
                }
            }

            layoutEspecialidades.addView(checkBox)
        }

        // Botón aplicar
        btnAplicar.setOnClickListener {
            onAplicarFiltro(seleccionadas.toList())
            dismiss()
        }

        // Botón cancelar
        btnCancelar.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}
