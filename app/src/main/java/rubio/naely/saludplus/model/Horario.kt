import java.io.Serializable

data class Horario(
    val dias: List<String> = listOf(),
    val horaInicio: String = "",
    val horaFin: String = "",
    val duracion: Int = 30,
    val precio: Double = 0.0
) : Serializable
