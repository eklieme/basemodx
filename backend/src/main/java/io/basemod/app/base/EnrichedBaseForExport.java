package io.basemod.app.base;

import io.basemod.app.evaluation.BaseEvaluation;

public class EnrichedBaseForExport extends EnrichedBiometricAuthenticationSystemAndEvaluation {

    private BaseEvaluation originalBaseEvaluation;

    public EnrichedBaseForExport(String name) {
        super(name);
    }

    public EnrichedBaseForExport() {
        super();
    }

    public EnrichedBaseForExport(BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluation) {
        super(biometricAuthenticationSystemAndEvaluation);
    }

    public BaseEvaluation getOriginalBaseEvaluation() {
        return originalBaseEvaluation;
    }

    public void setOriginalBaseEvaluation(BaseEvaluation originalBaseEvaluation) {
        this.originalBaseEvaluation = originalBaseEvaluation;
    }
}
