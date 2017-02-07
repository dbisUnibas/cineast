package org.vitrivr.cineast.core.util.fft;

import org.apache.commons.math3.complex.Complex;
import org.vitrivr.cineast.core.data.Pair;
import org.vitrivr.cineast.core.util.fft.windows.WindowFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author rgasser
 * @version 1.0
 * @created 05.02.17
 */
public class Spectrum implements Iterable<Pair<Float,Double>>{
    /**
     * Available type of spectra.
     */
    public enum Type {
        POWER, MAGNITUDE;
    }

    /** Values of the spectrum. They depend on the kind of spectrum being used. */
    private final double[] spectrum;

    /** Frequency labels of the spectrum (usually the x-axis). */
    private final float[] frequencies;

    /** Length of the spectrum (i.e. the number of bins). */
    private final int length;

    /** Type of spectrum as defined in the Type enum. */
    private final Type type;

    /** */
    private Double maximum;

    /** */
    private Double minimum;

    /**
     * Calculates and returns the power spectrum for the provided, complex data input (usually
     * stemming from a FFT).
     *
     * @param data Array of complex values to forward the spectrum from.
     * @param samplingrate Sampling rate at which the original samples were obtained.
     * @param windowFunction Window function that was used when calculating the FFT.
     * @return A power spectrum.
     */
    public static Spectrum createPowerSpectrum(Complex[] data, int samplingrate, WindowFunction windowFunction) {
        int bins = data.length / 2;
        double normalizationFactor = data.length * windowFunction.normalization(data.length);

        double[] powerSpectrum = new double[bins];
        powerSpectrum[0] = (Math.pow(data[0].getReal(),2) + Math.pow(data[0].getImaginary(),2)) / normalizationFactor;
        for(int i = 1; i < bins; i++) {
            powerSpectrum[i] = (2 * (Math.pow(data[i].getReal(),2) + Math.pow(data[i].getImaginary(),2))) / normalizationFactor;
        }

        return new Spectrum(Type.POWER, powerSpectrum, FFTUtil.binFrequencies(data.length, samplingrate));
    }

    /**
     * Calculates and returns t magnitude spectrum for the provided, complex data input (usually
     * stemming from a FFT).
     *
     * @param data Array of complex values to forward the spectrum from.
     * @param samplingrate Samplingrate at which the original samples were obtained.
     * @param windowFunction Window function that was used when calculating the FFT.
     * @return A magnitude spectrum.
     */
    public static Spectrum createMagnitudeSpectrum(Complex[] data, int samplingrate, WindowFunction windowFunction) {
        int bins = data.length / 2;
        double normalizationFactor = data.length * windowFunction.normalization(data.length);

        double[] magnitudeSpectrum = new double[bins];
        magnitudeSpectrum[0] = (Math.pow(data[0].getReal(),2) + Math.pow(data[0].getImaginary(),2)) / normalizationFactor;
        for(int i = 1; i < bins; i++) {
            magnitudeSpectrum[i] = (2 * (Math.pow(data[i].getReal(),2) + Math.pow(data[i].getImaginary(),2))) / normalizationFactor;
        }

        return new Spectrum(Type.MAGNITUDE, magnitudeSpectrum, FFTUtil.binFrequencies(data.length, samplingrate));
    }

    /**
     *
     * @param type
     * @param spectrum
     * @param frequencies
     */
    private Spectrum(Type type, double[] spectrum, float[] frequencies) {
        /* Check if the length of both array is the same. */
        if (spectrum.length != frequencies.length) throw new IllegalArgumentException("The length of the spectrum and the frequency-label array must be the same!");

        /* Store values for further refrence. */
        this.length = spectrum.length;
        this.spectrum = spectrum;
        this.frequencies = frequencies;
        this.type = type;
    }

    /**
     * Getter for the type of spectrum.
     *
     * @return Type of the spectrum.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the size of the spectrum (i.e. the number of bins).
     *
     * @return Size of the spectrum.
     */
    public int size() {
        return length;
    }

    /**
     * Returns a pair of frequency and the associated value at the specified
     * index.
     *
     * @param idx Index, zero-based and smaller than this.length
     * @return Frequency at specified index.
     */
    public Pair<Float,Double> get(int idx) {
        if (idx < this.frequencies.length && idx < this.spectrum.length) {
            return new Pair<>(this.frequencies[idx], this.spectrum[idx]);
        } else {
            return null;
        }
    }

    /**
     * Returns the frequency at the specified index.
     *
     * @param idx Index, zero-based and smaller than this.length
     * @return Frequency at specified index.
     */
    public Float getFrequeny(int idx) {
        if (idx < this.length) {
            return this.frequencies[idx];
        } else {
            return null;
        }
    }

    /**
     * Returns the value at the specified index.
     *
     * @param idx Index, zero-based and smaller than this.length
     * @return Value at specified index.
     */
    public Double getValue(int idx) {
        if (idx < this.length) {
            return this.spectrum[idx];
        } else {
            return null;
        }
    }

    /**
     * Returns the maximum value in the spectrum.
     *
     * @return
     */
    public Double getMaximum() {
        if (this.maximum == null) {
            this.maximum = Double.MIN_VALUE;
            for (double entry : this.spectrum) {
                this.maximum = Math.max(this.maximum, entry);
            }
        }
        return this.maximum;
    }

    /**
     * Returns the minimum value in the spectrum.
     *
     * @return
     */
    public Double getMinimum() {
        if (this.minimum == null) {
            this.minimum = Double.MAX_VALUE;
            for (double entry : this.spectrum) {
                this.minimum = Math.min(this.minimum, entry);
            }
        }
        return this.minimum;
    }

    /**
     * Returns the double array that holds the spectrum data.
     *
     * @return Double array with spectrum data.
     */
    public double[] array() {
        return this.spectrum;
    }

    /**
     * Returns a reduced version of the spectrum, limiting the view to the specified frequency-range.
     *
     * @param minFreq Minimum frequency to consider. Frequencies < minFreq will be discarded.
     * @param maxFreq Maximum frequency to consider. Frequencies > maxFreq will be discarded.
     * @return Reduced spectrum.
     */
    public Spectrum reduced(float minFreq, float maxFreq) {
        if (minFreq >= maxFreq) throw new IllegalArgumentException("Minimum frequency must be smaller than maximum frequency!");

        int[] range = new int[2];

        for (int i=0; i < this.frequencies.length; i++) {
            if (this.frequencies[i] <= minFreq) {
                range[0] = i;
            }
            if (this.frequencies[i] < maxFreq) {
                range[1] = i;
            }

            if (this.frequencies[i] > maxFreq) break;
        }

        return new Spectrum(this.type, Arrays.copyOfRange(this.spectrum, range[0], range[1]),  Arrays.copyOfRange(this.frequencies, range[0], range[1]));
    }

    /**
     * Find local maxima in the spectrum and returns the indices of those maxima as integer
     * array.
     *
     * @param threshold Threshold for search. Values bellow that threshold won't be considered.
     * @return Array containing indices (zero-based) of local maxima.
     */
    public int[] findLocalMaxima(double threshold) {
        List<Integer> candidates = new ArrayList<>();
        for (int i=1;i<this.spectrum.length-1;i++) {
            if (this.spectrum[i] < threshold) continue;
            if (this.spectrum[i+1] < this.spectrum[i] && this.spectrum[i-1] < this.spectrum[i]) candidates.add(i);
        }

        int[] results = new int[candidates.size()];
        for (int i=0; i<candidates.size();i++) {
            results[i] = candidates.get(i);
        }

        return results;
    }

    /**
     * Returns the minimum frequency in the spectrum.
     *
     * @return Minimum frequency
     */
    public float minFreq() {
        return this.frequencies[0];
    }

    /**
     * Returns the maximum frequency in the spectrum.
     *
     * @return Maximum frequency
     */
    public float maxFreq() {
        return this.frequencies[this.frequencies.length-1];
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Pair<Float, Double>> iterator() {
        return new Iterator<Pair<Float, Double>>() {

            private int idx = 0;

            @Override
            public boolean hasNext() {
                return this.idx < Spectrum.this.spectrum.length;
            }

            @Override
            public Pair<Float, Double> next() {
                return new Pair<>(Spectrum.this.frequencies[this.idx], Spectrum.this.spectrum[this.idx++]);
            }
        };
    }
}