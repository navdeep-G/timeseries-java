package main.java.collect;

import main.java.util.TSUtil;

import java.io.*;
import java.util.ArrayList;
import java.lang.*;
import java.util.Collections;

/**Capture relevant metrics from a time series dataset.
 * These metrics can guide algorithm implementation in the future.
 *
 * @author navdeepgill
 */

public class TSCollect {

    private final String _filepath;
    private final int _k;
    private final int _n;
    private final ArrayList<Double> _data;

    public TSCollect(String filepath, int k, int n) throws IOException{
        _filepath = filepath;
        _k = k;
        _n = n;
        _data = ReadFile();
    }

    /**
     * Read in time series dataset
     *
     * @return time series array list
     */
    public ArrayList<Double> ReadFile() throws IOException{
        return TSUtil.ReadFile(_filepath);
    }


    /**
     * Get average
     *
     * @return Average of the time series
     */
    public double getAverage() throws IOException {
        ArrayList<Double> file = _data;
        double total = 0;
        double nrow = file.size();

            for (int i = 0; i < file.size(); i++) {
                total = total + file.get(i);
            }

       double average = total/nrow;
       return average;
    }

    /**
     * Get variance
     *
     * @return Variance
     */
    public double getVariance() throws IOException {
        ArrayList<Double> file = _data;
        double avg = getAverage();
        double total = 0;
        double nrow = file.size();

            for (int i = 0; i < file.size(); i++) {
                total += (file.get(i) - avg) * (file.get(i) - avg);
            }

            double variance = total/(nrow-1);
            return variance;
        }

    /**
     * Get Standard Deviation
     *
     * @return Standard Deviation
     */
    public double getStandardDeviation() throws IOException{
        return Math.sqrt(getVariance());
    }

    /**
     * Get Minimum Value Index
     *
     * @return Minimum Value Index
     */
    public int getMinIndex () throws IOException{
        ArrayList<Double> file = _data;
        return file.indexOf(Collections.min(file));
    }

    /**
     * Get Maximum Value Index
     *
     * @return Maximum Value Index
     */
    public int getMaxIndex () throws IOException{
        ArrayList<Double> file = _data;
        return file.indexOf(Collections.max(file));
    }

    /**
     * Get Minimum Value Based On Index
     *
     * @return Minimum Value
     */
    public double getMin () throws IOException{
        ArrayList<Double> file = _data;
        int MinIndex = getMinIndex();
        return file.get(MinIndex);
    }

    /**
     * Get Maximum Value Based On Index
     *
     * @return Maximum Value
     */
    public double getMax () throws IOException{
        ArrayList<Double> file = _data;
        int MaxIndex = getMaxIndex();
        return file.get(MaxIndex);
    }

    /**
     * Get auto-covariance
     *
     * @param k Lag
     * @return Auto-Covariance
     */
    public double getAutocovariance(int k) throws IOException {
        ArrayList<Double> file = _data;
        double nrow = file.size();

        if (k == 0) {
            return getVariance();
        }

        double total = 0;
        double avg = getAverage();
            for (int i = k; i < file.size(); i++) {
                total += (file.get(i - k) - avg) * (file.get(i) - avg);
            }
        double autocovariance = total/nrow;
        return autocovariance;
    }

    /**
     * Get auto-correlation
     *
     * @param k Lag
     * @return Auto-correlation
     */
    public double getAutocorrelation(int k) throws IOException {
        double autocovariance = getAutocovariance(k);
        double variance = getVariance();
        double autocorrelation = autocovariance/variance;
        return autocorrelation;
    }

    /**
     * Get ACF
     *
     * @param n lag
     * @return ACF values
     */
    public double[] acf(int n) throws IOException {

        double[] acfValues = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            acfValues[i] = getAutocorrelation(i);
        }

        return acfValues;
    }

    /**
     * Get PACF
     *
     * @param n
     *            lag
     * @return PACF values
     */
    public double[] pacf(int n) throws IOException {

        double[] pacfValues = new double[n + 1];
        double[][] phi = new double[n + 1][n + 1];

        pacfValues[0] = phi[0][0] = 1D;
        pacfValues[1] = phi[1][1] = getAutocorrelation(1);

        for (int i = 2; i <= n; i++) {
            for (int j = 1; j < i - 1; j++) {
                phi[i - 1][j] = phi[i - 2][j] - phi[i - 1][i - 1]
                        * phi[i - 2][i - 1 - j];
            }

            double a = 0D, b = 0D;
            for (int j = 1; j < i; j++) {
                a += phi[i - 1][j] * getAutocorrelation(i - j);
                b += phi[i - 1][j] * getAutocorrelation(j);
            }

            pacfValues[i] = phi[i][i] = (getAutocorrelation(i) - a)
                    / (1 - b);
        }

        return pacfValues;
    }

}




