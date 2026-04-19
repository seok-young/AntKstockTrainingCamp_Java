package com.example.AntKstockCamp.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class IndicatorCalculator {
    public  double[] calculateMA(List<Double> prices, int window){
        double[] ma = new double[prices.size()];
        double sum = 0;

        for (int i =0; i < prices.size(); i++) {
            sum += prices.get(i);
            if(i>= window){
                sum -= prices.get(i - window);
            }
            if(i >= window-1){
                ma[i] = sum / window;
            }
        }
        return ma;
    }

    // List용
    public  double[] calculateEMA(List<Double> prices, int span){
        double[] ema = new double[prices.size()];
        double multiplier = 2.0 / (span+1);

        for (int i = 0; i < prices.size(); i++){
            if(i==0){
                ema[i] = prices.get(i);
            } else {
                ema[i] = (prices.get(i) - ema[i-1]) * multiplier + ema[i-1];
            }
        }
        return ema;
    }

    //array용
    public  double[] calculateEMA(double[] prices, int span){
        double[] ema = new double[prices.length];
        double multiplier = 2.0 / (span+1);

        for (int i = 0; i < prices.length; i++){
            if(i==0){
                ema[i] = prices[i];
            } else {
                ema[i] = (prices[i] - ema[i-1]) * multiplier + ema[i-1];
            }
        }
        return ema;
    }
    public Map<String, double[]> calculateMACD(List<Double> prices){
        int size = prices.size();

        if (size < 2) {
            return Map.of("macd",new double[size],
                    "signal",new double[size],
                    "hist",new double[size]);
        }

        double[] ema12 = calculateEMA(prices,12);
        double[] ema26 = calculateEMA(prices,26);

        double[] macd = new double[size];
        for (int i = 0; i < size; i++){
            macd[i] = ema12[i] - ema26[i];
        }

        double[] macdSignal = calculateEMA(macd,9);

        double[] macdHist = new double[size];
        for (int i=0; i< size; i++){
            macdHist[i] = macd[i] - macdSignal[i];
        }
        return Map.of(
                "macd",macd,
                "signal",macdSignal,
                "hist",macdHist
        );
    }

    public double[] calculateRSI(List<Double> prices, int period){
        double[] rsi = new double[prices.size()];
        if(prices.size() <= period) return rsi;

        double avgGain = 0, avgLoss = 0;

        for (int i = 1; i<= period; i++){
            double diff = prices.get(i) - prices.get(i-1);
            if(diff > 0) avgGain += diff;
            else avgLoss -= diff;
        }

        avgGain /= period;
        avgLoss /= period;

        double initialRs = (avgLoss == 0) ? 0 : (avgGain/avgLoss);
        rsi[period] = 100 - (100 / (1 + initialRs));

        for (int i = period +1; i < prices.size(); i++){
            double diff = prices.get(i) - prices.get(i-1);
            double gain = diff > 0 ? diff:0;
            double loss = diff < 0 ? -diff:0;

            avgGain = (avgGain * (period -1) + gain) / period;
            avgLoss = (avgLoss * (period -1) + loss) / period;

            double rs = avgGain / (avgLoss == 0 ? 1 : avgLoss);
            rsi[i] = 100 - (100 / (1 + rs));
        }
        return rsi;
    }

    public Map<String, double[]> calculateBB(List<Double> prices, int window, double stdDev){
        int size = prices.size();
        double[] middle = calculateMA(prices, window);
        double[] upper = new double[size];
        double[] lower = new double[size];

        if (size < window) {
            return  Map.of("middle", middle, "upper", upper, "lower", lower);
        }

        for (int i = window - 1; i < prices.size(); i++ ){
            double sum =0;
            for (int j = i - window +1; j <= i; j++){
                sum += Math.pow(prices.get(j) - middle[i], 2);
            }
            double std = Math.sqrt(sum / window);
            upper[i] = middle[i] + (std * stdDev);
            lower[i] = middle[i] - (std * stdDev);
        }
        return Map.of("middle",middle,"upper",upper,"lower",lower);
    }

}
