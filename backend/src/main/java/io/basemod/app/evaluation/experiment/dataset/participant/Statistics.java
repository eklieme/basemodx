package io.basemod.app.evaluation.experiment.dataset.participant;

public class Statistics {

    private int min;
    private int max;
    private float mean;
    private float stdDeviation;

    public Statistics(int min, int max, float mean, float stdDeviation) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stdDeviation = stdDeviation;
    }

    public Statistics() {
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getMean() {
        return mean;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }

    public float getStdDeviation() {
        return stdDeviation;
    }

    public void setStdDeviation(float stdDeviation) {
        this.stdDeviation = stdDeviation;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "min=" + min +
                ", max=" + max +
                ", mean=" + mean +
                ", stdDeviation=" + stdDeviation +
                '}';
    }
}
