package io.basemod.app.config;

import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.evaluation.criteria.EvaluationCriteria;
import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystem;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.Feature;
import io.basemod.app.evaluation.experiment.dataset.*;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.Sensor;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.SensorDimension;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

@Component
public class RestRepositoryConfigurator implements RepositoryRestConfigurer {

        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(
                BiometricAuthenticationSystemAndEvaluation.class,
                SampleDevice.class,
                SensorDimension.class,
                Sensor.class,
                SamplingContext.class,
                Feature.class,
                DataSet.class,
                ResultMetric.class,
                BiometricSystem.class,
                ImplementationSpecificEvaluationCriteria.class,
                ExperimentSpecificEvaluationCriteria.class,
                EvaluationCriteria.class);
    }


}
