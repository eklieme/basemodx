package io.basemod.app.evaluation.implementation;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;

import java.util.ArrayList;
import java.util.List;

public class EnrichedImplementationSpecificEvaluationResult extends ImplementationSpecificEvaluationResult{

    private ImplementationSpecificEvaluationCriteria criteria;
    private List<DeviceCategory> affectedDeviceCategories;

    public EnrichedImplementationSpecificEvaluationResult() {
        this.affectedDeviceCategories = new ArrayList<>();
    }

    public EnrichedImplementationSpecificEvaluationResult(ImplementationSpecificEvaluationCriteria criteria, List<DeviceCategory> affectedDeviceCategories) {
        this.criteria = criteria;
        this.affectedDeviceCategories = affectedDeviceCategories;
    }

    public EnrichedImplementationSpecificEvaluationResult(String criteriaId, List<String> affectedDeviceCategoryIds, String description, ImplementationSpecificEvaluationCriteria criteria, List<DeviceCategory> affectedDeviceCategories) {
        super(criteriaId, affectedDeviceCategoryIds, description);
        this.criteria = criteria;
        this.affectedDeviceCategories = affectedDeviceCategories;
    }

    public ImplementationSpecificEvaluationCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(ImplementationSpecificEvaluationCriteria criteria) {
        this.criteria = criteria;
    }

    public List<DeviceCategory> getAffectedDeviceCategories() {
        return affectedDeviceCategories;
    }

    public void setAffectedDeviceCategories(List<DeviceCategory> affectedDeviceCategories) {
        this.affectedDeviceCategories = affectedDeviceCategories;
    }

    public void addDeviceCategory(DeviceCategory deviceCategoryToAdd) {
        if(this.affectedDeviceCategories==null) {
            this.affectedDeviceCategories = new ArrayList<>();
        }
        this.affectedDeviceCategories.add(deviceCategoryToAdd);
    }
}
