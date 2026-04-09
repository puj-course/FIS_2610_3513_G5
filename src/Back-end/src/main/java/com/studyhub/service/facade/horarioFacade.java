package com.studyhub.service.facade;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.studyhub.dto.horarioDTO;
import com.studyhub.model.Asignatura;
import com.studyhub.repository.HorarioRepository;
import com.studyhub.service.AsignaturaService;

@Service
public class horarioFacade {

	@Autowired private AsignaturaService asigService;
    @Autowired private HorarioRepository horarioRepo;
    
    public List<horarioDTO> obtenerHorarioCompleto(Long userId) {
        List<Asignatura> lista = asigService.findByUserId(userId);
        return horarioRepo.transformToDTO(lista);
    }
}
