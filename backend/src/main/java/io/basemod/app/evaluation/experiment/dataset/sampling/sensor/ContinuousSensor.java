package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

public class ContinuousSensor extends Sensor {

    private int samplingFrequenceInHertz;


    public ContinuousSensor(int samplingFrequenceInHertz) {
        this.samplingFrequenceInHertz = samplingFrequenceInHertz;
    }

    public ContinuousSensor() {
    }

    public int getSamplingFrequenceInHertz() {
        return samplingFrequenceInHertz;
    }

    public void setSamplingFrequenceInHertz(int samplingFrequenceInHertz) {
        this.samplingFrequenceInHertz = samplingFrequenceInHertz;
    }
}
