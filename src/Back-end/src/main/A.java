// Implementación del Builder para HorarioDTO
public class HorarioBuilder {
    private HorarioDTO dto = new HorarioDTO();

    public HorarioBuilder conAsignatura(String nombre) {
        dto.setNombreNombreAsignatura(nombre);
        return this;
    }

    public HorarioBuilder enDia(String dia) {
        dto.setDia(dia);
        return this;
    }

    public HorarioBuilder conFranja(String inicio, String fin) {
        dto.setHoras(inicio, fin);
        return this;
    }

    public HorarioDTO build() {
        return dto;
    }
}