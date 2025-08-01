package rubio.naely.saludplus.model

data class Cita(
    var id: String = "",
    val nombreDoctor: String = "",
    val especialidad: String = "",
    val estado: String = "", // aceptada, pendiente, cancelada
    val motivo: String = "",
    val fecha: String = "",
    val hora: String = "",
    val fotoDoctor: String = "",
    val nombrePaciente: String = "",
    val fotoPaciente: String = "",
    var timestamp: Long = 0L

)
