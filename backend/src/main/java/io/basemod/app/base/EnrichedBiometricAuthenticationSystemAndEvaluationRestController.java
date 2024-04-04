package io.basemod.app.base;


import io.basemod.app.security.authentication.domain.BaseUser;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/base")
public class EnrichedBiometricAuthenticationSystemAndEvaluationRestController {

    Logger logger = LoggerFactory.getLogger(EnrichedBiometricAuthenticationSystemAndEvaluationRestController.class);

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;
    private EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService;

    @Autowired
    public EnrichedBiometricAuthenticationSystemAndEvaluationRestController(BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService) {
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.enrichedBiometricAuthenticationSystemAndEvaluationService = enrichedBiometricAuthenticationSystemAndEvaluationService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<EnrichedBaseForExport> getAllBase(){
        return enrichedBiometricAuthenticationSystemAndEvaluationService.transformBasesToReturn(biometricAuthenticationSystemAndEvaluationRepository.findAll());
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON, value = "/modellingProgress/{modellingProgress}")
    public List<EnrichedBaseForExport> getAllBaseByModellingProgress(@PathVariable String modellingProgress){
        logger.debug("\t...got request to get all BASE with modelling state '{}'", modellingProgress);
        return enrichedBiometricAuthenticationSystemAndEvaluationService
                .transformBasesToReturn(biometricAuthenticationSystemAndEvaluationRepository.findByModellingProgress(modellingProgress));
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON, value = "/name/{name}")
    public Response getBaseByName(@PathVariable String name, Principal principal){
        BiometricAuthenticationSystemAndEvaluation baseToEnrich = biometricAuthenticationSystemAndEvaluationRepository.findByNameEqualsIgnoreCase(name);
        if(baseToEnrich==null) {
            logger.debug("...base with name '{}' does not exist, create a new one just with name", name);
            EnrichedBiometricAuthenticationSystemAndEvaluation emptyEnrichedBase = new EnrichedBiometricAuthenticationSystemAndEvaluation(name);
            return Response.status(Response.Status.NOT_FOUND).entity(emptyEnrichedBase).build();
        } else {
            logger.debug("...base with name '{}' exists, check whether user is allowed to edit", name);
            if(principal==null) {
                logger.debug("\t...no principal found, return 401");
                return Response.status(401).build();
            } else {
                UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
                BaseUser loggedInUser = (BaseUser) authToken.getPrincipal();
                if(!loggedInUser.getUniqueId().equals(baseToEnrich.getModelledElementDetail().getModelledInitiallyBy())) {
                    logger.debug("\t...base was originally created by '{}', but now '{}' tries editing, return 401",
                            baseToEnrich.getModelledElementDetail().getModelledInitiallyBy(),
                            loggedInUser.getUniqueId());
                    return Response.status(401).build();
                }
            }

            EnrichedBaseForExport enrichedBase =
                    enrichedBiometricAuthenticationSystemAndEvaluationService.transformBasesToReturn(baseToEnrich);

            logger.debug("...base with name '{}' exists, user is allowed to edit, return!", name);
            return Response.ok().entity(enrichedBase).build();
        }
    }


}
