package com.conflict.forecaster.service.predictor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LstmPredictionService implements PredictionService {

    private PredictionResponse predictionResponse;
    private PredictionDataPreparer predictionDataPreparer;

    @Autowired
    public LstmPredictionService(PredictionResponse predictionResponse, PredictionDataPreparer predictionDataPreparer) {
        this.predictionResponse = predictionResponse;
        this.predictionDataPreparer = predictionDataPreparer;
    }

    @Override
    public ObjectNode predict(
            int countryId,
            int violenceType,
            int startYear,
            int startMonth,
            int lastYear,
            int lastMonth,
            int timespan)
    {
        double[] dataArray = predictionDataPreparer.getDataArray(countryId, violenceType, startYear, startMonth, lastYear, lastMonth, timespan);
        double[] allDataArray = predictionDataPreparer.getAllDataArray(countryId, violenceType, startYear, startMonth, lastYear, lastMonth, timespan);

        double[] forecastData = predictNN(dataArray, timespan);

        return predictionResponse.getResponse(forecastData, dataArray, allDataArray);
    }

    private double[] predictNN(double[] dataArray, int timespan) {

        // Подготовка данных для обучения
        int windowSize = 5; // Количество точек, используемых для прогнозирования следующей точки
        int forecastSteps = timespan; // Количество шагов для прогнозирования вперед
        INDArray input = Nd4j.create(new int[]{dataArray.length - windowSize - forecastSteps + 1, windowSize}, 'f');
        INDArray label = Nd4j.create(new int[]{dataArray.length - windowSize - forecastSteps + 1, forecastSteps}, 'f');

        for (int i = 0; i < dataArray.length - windowSize - forecastSteps + 1; i++) {
            for (int j = 0; j < windowSize; j++) {
                input.putScalar(new int[]{i, j}, dataArray[i + j]);
            }
            for (int j = 0; j < forecastSteps; j++) {
                label.putScalar(new int[]{i, j}, dataArray[i + windowSize + j]);
            }
        }

        DataSet dataSet = new DataSet(input, label);

        // Нормализация данных
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler();
        normalizer.fit(dataSet);
        normalizer.transform(dataSet);

        // Построение модели нейронной сети
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Adam(0.01))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(windowSize).nOut(10)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(10).nOut(10)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(10).nOut(forecastSteps).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        // Обучение модели
        for (int i = 0; i < 500; i++) {
            model.fit(dataSet);
        }

        // Прогнозирование
        INDArray lastWindow = Nd4j.create(new int[]{1, windowSize}, 'f');
        for (int j = 0; j < windowSize; j++) {
            lastWindow.putScalar(new int[]{0, j}, dataArray[dataArray.length - windowSize + j]);
        }

        // Нормализация входных данных перед прогнозом
        normalizer.transform(lastWindow);

        INDArray prediction = model.output(lastWindow);

        // Обратная нормализация прогноза
        normalizer.revertLabels(prediction);

        System.out.println("Predicted values for the next " + forecastSteps + " steps:");
        double[] predictions = new double[forecastSteps];
        for (int i = 0; i < forecastSteps; i++) {
            predictions[i] = prediction.getDouble(i);
            System.out.println("Step " + (i + 1) + ": " + predictions[i]);
        }

        return predictions;
    }


}
