package com.conflict.forecaster.service.LSTM;

import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import com.conflict.forecaster.service.PredictionService;

import java.util.Arrays;
import java.util.List;

import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
public class LSTMPredictionService{
    private UCDPEventCountRepository ucdpEventCountRepository;
    private MultiLayerNetwork lstmModel;
    private PredictionService predictionService;

    @Autowired
    public LSTMPredictionService(UCDPEventCountRepository ucdpEventCountRepository, LSTMConfig lstmConfig, PredictionService predictionService) {
        this.ucdpEventCountRepository = ucdpEventCountRepository;
        this.predictionService = predictionService;
        this.lstmModel = lstmConfig.buildLSTMModel();
        this.lstmModel.init();
    }

    public void test2()
    {
        int countryId = 520;
        int violenceType = 3;
        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId, violenceType);

        double[] dataArray = predictionService.getArrayOfViolenceCounts(ucdpEventCounts);
        System.out.print("Data Array: ");
        for (double data : dataArray) {
            System.out.print(data + " ");
        }
        System.out.println();

        int trainingDataPercentage = 80;
        int totalDataSize = dataArray.length;
        int testStartIndex = (int) (totalDataSize * (trainingDataPercentage / 100.0));

        double[] trainingDataArray = Arrays.copyOfRange(dataArray, 0, testStartIndex);
        double[] testDataArray = Arrays.copyOfRange(dataArray, testStartIndex, totalDataSize);

        this.trainModel(trainingDataArray, 10);
        // Предсказание
        List<Double> predictions = predict(dataArray, 10);

        System.out.println("Predictions: " + predictions);

        return;
    }

    public void test() {
        int lstmLayerSize = 50;
        int numEpochs = 100;
        int nInput = 1;
        int nOutput = 1;

        INDArray dataX = generateRandomData(100, nInput);
        INDArray dataY = generateRandomData(5, nOutput);

        MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.NONE)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue) // проверить
                .gradientNormalizationThreshold(10.0)
                .list()
                .layer(0, new LSTM.Builder().nIn(nInput).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(2, new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(lstmLayerSize).nOut(nOutput).build())
                .backpropType(BackpropType.TruncatedBPTT)
                .tBPTTForwardLength(10)
                .tBPTTBackwardLength(10)
                .pretrain(false)
                .backprop(true)
                .build()
        );

        net.init();
        net.setListeners(new ScoreIterationListener(1));

        for (int epoch = 0; epoch < numEpochs; epoch++) {
            net.fit(dataX, dataY);
            net.rnnClearPreviousState();
        }

        // предсказание
        INDArray inputData = generateRandomData(5, nInput); // 5 временных точек для предсказания
        INDArray predicted = net.rnnTimeStep(inputData);
        System.out.println("inputData values: " + inputData);
        System.out.println("Predicted values: " + predicted);
    }

    private static INDArray generateRandomData(int numPoints, int numFeatures) {
        Random rand = new Random();
        INDArray randomData = Nd4j.zeros(numPoints, numFeatures);

        for (int i = 0; i < numPoints; i++) {
            for (int j = 0; j < numFeatures; j++) {
                randomData.putScalar(i, j, rand.nextDouble() * 100);
            }
        }

        return randomData;
    }


    protected List<Double> getResponse(INDArray forecastData) {
        List<Double> forecasts = new ArrayList<>();

        for (int i = 0; i < forecastData.length(); i++) {
            forecasts.add(forecastData.getDouble(i));
        }

        return forecasts;
    }

    public List<Double> predict(double[] inputData, int numTimeSteps) {
        INDArray inputArray = Nd4j.create(inputData).reshape(1, 1, inputData.length);

        // Прогноз
        INDArray forecastArray = lstmModel.rnnTimeStep(inputArray);

        return getResponse(forecastArray);
    }

    public void trainModel(double[] trainingData, int numEpochs) {
        INDArray inputArray = Nd4j.create(trainingData).reshape(1, 1, trainingData.length);
        INDArray labelArray = Nd4j.create(trainingData).reshape(1, 1, trainingData.length);

        // Обучение
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            lstmModel.fit(inputArray, labelArray);
        }
    }




}
