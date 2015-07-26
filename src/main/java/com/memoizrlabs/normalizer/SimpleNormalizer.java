package com.memoizrlabs.normalizer;

/**
 * Created by memoizr on 24/07/15.
 */
public class SimpleNormalizer {
    private static final float COMPRESSION_FACTOR = 1.2f;
    private static final float DECAY_RATE = 0.96f;
    private static final float INVERTED_DECAY_RATE = 1 / DECAY_RATE;
    private static final float MIN_VOLUME_RANGE = 0.01f;
    private static final float RESPONSE_FACTOR = 0.8f;
    private static final float VOLUME_LOWER_MAXIMUM = 0.95f;
    private static final float VOLUME_UPPER_MINIMUM = 1.1f;
    private static final int SAMPLE_SIZE = 256;

    private static final float[] sampleSquares = new float[SAMPLE_SIZE];

    private float mMaxVolume = MIN_VOLUME_RANGE;
    private float mMinVolume = 1f;
    private float sum;
    private int cyclicCounter;
    private int firstRunCounter;

    private float getRollingRMS(float value) {
        final float square = value * value;

        if (firstRunCounter < SAMPLE_SIZE) {
            sum += square;
            sampleSquares[firstRunCounter] = square;
            firstRunCounter++;
        } else {
            sum = sum - sampleSquares[cyclicCounter] + square;
            sampleSquares[cyclicCounter] = square;
            cyclicCounter = ++cyclicCounter % SAMPLE_SIZE;
        }

        return (float) Math.sqrt(sum / SAMPLE_SIZE);
    }

    public float normalizeVolume(float volume) {
        mMaxVolume = volume > mMaxVolume ? volume : mMaxVolume;
        mMinVolume = volume < mMinVolume ? volume : mMinVolume;

        final float rms = getRollingRMS(volume);
        final float dynamicResponse = (float) Math.pow(mMaxVolume - mMinVolume, RESPONSE_FACTOR);
        final float dynamicRange = Math.max(dynamicResponse, MIN_VOLUME_RANGE);
        final float normalizedVolume = (volume - mMinVolume) / dynamicRange;

        mMaxVolume = mMaxVolume > rms * VOLUME_UPPER_MINIMUM ? mMaxVolume * DECAY_RATE : mMaxVolume;
        mMinVolume = mMinVolume < rms * VOLUME_LOWER_MAXIMUM ? mMinVolume * INVERTED_DECAY_RATE : mMinVolume;

        return (float) Math.pow(normalizedVolume, COMPRESSION_FACTOR);
    }
}
