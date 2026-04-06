// Fachada que simplifica el acceso al horario
@Service
public class HorarioFacade {
    @Autowired private AsignaturaService asigService;
    @Autowired private HorarioRepository horarioRepo;

    public List<HorarioDTO> obtenerHorarioCompleto(Long userId) {
        // La fachada orquesta la lógica compleja internamente
        List<Asignatura> lista = asigService.findByUserId(userId);
        return horarioRepo.transformToDTO(lista);
    }
}