import java.io.Serializable

data class Medico(
    val id: String = "",
    val nombre: String = "",
    val especialidad: String = "",
    val direccion: String = "",
    val calificacion: Double = 0.0,
    val imagenUrl: String = "",
    val precio: Double = 0.0
) : Serializable
