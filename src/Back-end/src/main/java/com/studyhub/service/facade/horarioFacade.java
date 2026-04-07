package com.studyhub.service.facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.studyhub.repository.HorarioRepository;
import com.studyhub.service.AsignaturaService;

@Service
public class horarioFacade {

	@Autowired private AsignaturaService asigService;
    @Autowired private HorarioRepository horarioRepo;

}
