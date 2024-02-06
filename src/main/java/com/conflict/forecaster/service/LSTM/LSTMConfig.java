package com.conflict.forecaster.service.LSTM;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
public class LSTMConfig {

    public MultiLayerNetwork buildLSTMModel() {
        int numInputs = 1;  // Входной размер (временные шаги)
        int numHiddenNodes = 50;  // Количество скрытых узлов в LSTM
        int numOutputs = 1;  // Выходной размер (прогноз)

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(org.nd4j.linalg.learning.config.Adam.builder().learningRate(0.001).build())
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(numInputs)
                        .nOut(numHiddenNodes)
                        .activation(Activation.TANH)
                        .weightInit(WeightInit.XAVIER)
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(10.0)
                        .build())
                .layer(1, new OutputLayer.Builder()
                        .nIn(numHiddenNodes)
                        .nOut(numOutputs)
                        .activation(Activation.IDENTITY)
                        .lossFunction(org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR)
                        .build())
                .backpropType(BackpropType.Standard)
                .build();

        return new MultiLayerNetwork(conf);
    }
}