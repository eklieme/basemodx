package io.basemod.app.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

    Logger logger = LoggerFactory.getLogger(BaseService.class);

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;

    @Autowired
    public BaseService(BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository) {
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
    }

    public void resetBaseExperimentalEvaluation(BiometricAuthenticationSystemAndEvaluation baseToChange) {

        logger.debug("...reset experimental evaluation of base '{}'", baseToChange.getName());
        baseToChange.getBaseEvaluation().setExperimentSpecificEvaluation(null);
        if(baseToChange.getBaseEvaluation().getImplementationSpecificEvaluationResults()==null
                || baseToChange.getBaseEvaluation().getImplementationSpecificEvaluationResults().size()==0) {
            logger.debug("\t...base '{}' has no implementation based evaluation, reset modelling state",
                    baseToChange.getName());
            baseToChange.setBaseEvaluation(null);
            baseToChange.setModellingProgress(ModellingProgress.TARGETARCHITECTURE);
        }
        logger.debug("\t...updating base '{}' in DB", baseToChange.getName());
        biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);
    }

    public void resetBaseImplementationBasedEvaluation(BiometricAuthenticationSystemAndEvaluation baseToChange) {

        logger.debug("...reset implementation-based evaluation of base '{}'", baseToChange.getName());
        baseToChange.getBaseEvaluation().setImplementationSpecificEvaluationResults(null);
        if(baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation()==null
            || baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size()==0) {
            logger.debug("\t...base '{}' has no experiment based evaluation, reset modelling state",
                    baseToChange.getName());
            baseToChange.setBaseEvaluation(null);
            baseToChange.setModellingProgress(ModellingProgress.TARGETARCHITECTURE);
        }
        logger.debug("\t...updating base '{}' in DB", baseToChange.getName());
        biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);
    }
}
