package com.memoizrlabs.normalizer;

/**
 * A simple normalizer implementation.
 *
 * @author memoizr
 */
final public class SimpleNormalizer {
    private static final float COMPRESSION_FACTOR = 1.15f;
    private static final float DECAY_RATE = 0.96f;
    private static final float INVERTED_DECAY_RATE = 1 / DECAY_RATE;
    private static final float MIN_VOLUME_RANGE = 0.01f;
    private static final float RESPONSE_FACTOR = 0.65f;
    private static final float VOLUME_LOWER_MAXIMUM = 0.95f;
    private static final float VOLUME_UPPER_MINIMUM = 1.4f;
    private static final int SAMPLE_SIZE = 256;
    private static final float DEFAULT_MAX_VOLUME = 1f;
    private static final float DEFAULT_MIN_VOLUME = 0f;

    private static final float[] mSampleSquares = new float[SAMPLE_SIZE];

    private float mMaxVolume = DEFAULT_MAX_VOLUME;
    private float mMinVolume = DEFAULT_MIN_VOLUME;
    private float mSum;
    private int mCyclicCounter;
    private int mFirstRunCount;

    private float getRollingRMS(float value) {
        final float square = value * value;

        if (mFirstRunCount < SAMPLE_SIZE) {
            mSum += square;
            mSampleSquares[mFirstRunCount] = square;
            mFirstRunCount++;
        } else {
            mSum = mSum - mSampleSquares[mCyclicCounter] + square;
            mSampleSquares[mCyclicCounter] = square;
            mCyclicCounter = ++mCyclicCounter % SAMPLE_SIZE;
        }

        return (float) Math.sqrt(mSum / SAMPLE_SIZE);
    }

    /**
     * Normalizes a raw volume input stream. The normalized amplitude will be bound by the maximum amplitude and the minimum amplitude recorded. These limits
     * will exponentially converge towards the average volume (RMS) over time when the new peaks are consistently of a lesser magnitude than previous ones.
     *
     * @param volume The raw volume input.
     * @return The normalized volume.
     */
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

    /**
     * Resets the state of the normalizer to default values.
     */
    public void reset() {
        mMaxVolume = DEFAULT_MAX_VOLUME;
        mMinVolume = DEFAULT_MIN_VOLUME;
        mSum = 0;
        mCyclicCounter = 0;
        mFirstRunCount = 0;

        for (int i = 0; i < mSampleSquares.length; i++) {
            mSampleSquares[i] = 0f;
        }
    }
}
