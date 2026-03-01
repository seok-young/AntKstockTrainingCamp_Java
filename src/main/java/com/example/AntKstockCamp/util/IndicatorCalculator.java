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

    public double[] calculateRSI(List<Double> prices, int period){
        double[] rsi = new double[prices.size()];
        if(prices.size() < period) return rsi;

        double avgGain = 0, avgLoss = 0;

        for (int i = 1; i<= period; i++){
            double diff = prices.get(i) - prices.get(i-1);
            if(diff > 0) avgGain += diff;
            else avgLoss -= diff;
        }

        avgGain /= period;
        avgLoss /= period;
        rsi[period] = 100 - (100 / (1 + (avgGain / avgLoss)));

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
        double[] middle = calculateMA(prices, window);
        double[] upper = new double[prices.size()];
        double[] lower = new double[prices.size()];

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
