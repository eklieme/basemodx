package io.basemod.app.evaluation.experiment.biometricsystem.step;

import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/biometricSystemProcessingStep")
public class BiometricProcessingStepRestController {

    private BiometricProcessingStepCRUDRepository biometricProcessingStepRepository;

    @Autowired
    public BiometricProcessingStepRestController(BiometricProcessingStepCRUDRepository biometricProcessingStepRepository) {
        this.biometricProcessingStepRepository = biometricProcessingStepRepository;
    }


}
