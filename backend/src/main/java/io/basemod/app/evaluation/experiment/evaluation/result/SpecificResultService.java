package io.basemod.app.evaluation.experiment.evaluation.result;

import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricService;
import io.basemod.app.repository.ModelledElementLifecycleAwareRestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecificResultService {

    Logger logger = LoggerFactory.getLogger(SpecificResultService.class);

    private ResultMetricCRUDRepository resultMetricCRUDRepository;
    private ResultMetricService resultMetricService;

    @Autowired
    public SpecificResultService(ResultMetricCRUDRepository resultMetricCRUDRepository, ResultMetricService resultMetricService) {
        this.resultMetricCRUDRepository = resultMetricCRUDRepository;
        this.resultMetricService = resultMetricService;
    }

    public EnrichedSpecificResult enrichSpecificResult(SpecificResult specificResultToEnrich) {

        logger.debug("\t...enriching specific result with result metric id '{}'", specificResultToEnrich.getResultMetricId());

        EnrichedSpecificResult enrichedSpecificResult = new EnrichedSpecificResult();
        enrichedSpecificResult.setDescription(specificResultToEnrich.getDescription());
        enrichedSpecificResult.setFrom(specificResultToEnrich.getFrom());
        enrichedSpecificResult.setTo(specificResultToEnrich.getTo());
        enrichedSpecificResult.setResult(specificResultToEnrich.getResult());
        enrichedSpecificResult.setResultGranularity(specificResultToEnrich.getResultGranularity());
        enrichedSpecificResult.setResultMetricId(specificResultToEnrich.getResultMetricId());

        enrichedSpecificResult.setResultMetric(
                resultMetricService.enrichResultMetric(
                    resultMetricCRUDRepository.findById(specificResultToEnrich.getResultMetricId()).get()));

        return enrichedSpecificResult;
    }
}
