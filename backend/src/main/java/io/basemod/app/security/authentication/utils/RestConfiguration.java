package io.basemod.app.security.authentication.utils;

import io.basemod.app.architecture.ResourceToProtect;
import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.characteristic.BehavioralBiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.characteristic.PhysiologicalBiometricCharacteristic;
import io.basemod.app.characteristic.SoftBiometricCharacteristic;
import io.basemod.app.evaluation.experiment.biometricsystem.step.MachineLearningProcessingStep;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystem;
import io.basemod.app.evaluation.experiment.biometricsystem.step.BiometricSystemProcessingStep;
import io.basemod.app.evaluation.experiment.biometricsystem.step.DecisionProcessingStep;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.Feature;
import io.basemod.app.evaluation.experiment.dataset.DataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.Sensor;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.SensorDimension;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;
import io.basemod.app.evaluation.extension.BaseEvaluationExtension;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        // since we have spring security included, we need to enable that again
        config.exposeIdsFor(BehavioralBiometricCharacteristic.class,
                PhysiologicalBiometricCharacteristic.class,
                SoftBiometricCharacteristic.class,
                BiometricCharacteristic.class,
                BiometricAuthenticationSystemAndEvaluation.class,
                ResourceToProtect.class,
                DeviceCategory.class,
                Sensor.class,
                SampleDevice.class,
                SensorDimension.class,
                SamplingContext.class,
                DataSet.class,
                Feature.class,
                BiometricSystem.class,
                BiometricSystemProcessingStep.class,
                MachineLearningProcessingStep.class,
                DecisionProcessingStep.class,
                DecisionProcessingStep.class,
                ExperimentSpecificEvaluationCriteria.class,
                ImplementationSpecificEvaluationCriteria.class,
                ResultMetric.class,
                BaseEvaluationExtension.class);
    }
}