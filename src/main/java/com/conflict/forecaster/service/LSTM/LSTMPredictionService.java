package com.conflict.forecaster.service.LSTM;

import com.conflict.forecaster.database.UCDPEventCountRepository;
import com.conflict.forecaster.database.entity.UCDPEventCount;
import com.conflict.forecaster.service.PredictionService;

import java.util.*;

import org.datavec.api.records.reader.impl.collection.CollectionRecordReader;
import org.datavec.api.records.reader.impl.collection.ListStringRecordReader;
import org.datavec.api.split.ListStringSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.preprocessor.CnnToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToCnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.ViewIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.RmsPropUpdater;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LSTMPredictionService{
    private UCDPEventCountRepository ucdpEventCountRepository;
    private MultiLayerNetwork lstmModel;
    private PredictionService predictionService;

    //Random number generator seed, for reproducibility
    public static final int seed = 12345;
    //Number of iterations per minibatch
    public static final int iterations = 35;
    //Number of epochs (full passes of the data)
    public static final int nEpochs = 80;
    //Number of data points
    public static int nSamples = 25;
    //Network learning rate
    public static final double learningRate = 0.0001;


    @Autowired
    public LSTMPredictionService(UCDPEventCountRepository ucdpEventCountRepository, LSTMConfig lstmConfig, PredictionService predictionService) {
        this.ucdpEventCountRepository = ucdpEventCountRepository;
        this.predictionService = predictionService;
        this.lstmModel = lstmConfig.buildLSTMModel();
        this.lstmModel.init();
    }

    public void test() {
        //Generate the training data
        DataSet trainingData = getTrainingData();
        trainingData.shuffle();
        System.out.println(trainingData);
        System.out.println();
        DataSet testData = getTestData();
        System.out.println(testData);

        //Create the network
        int numInput = 1;
        int numOutputs = 1;
        int nHidden = 30;

//        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
//        builder.seed(seed);
//        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
//        builder.updater(Updater.RMSPROP);
//        builder.gradientNormalization(GradientNormalization.ClipL2PerLayer);
//        builder.gradientNormalizationThreshold(0.00001);
//
//        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();
//
//        listBuilder.layer(0, new GravesLSTM.Builder().nIn(numInput).nOut(nHidden)
//                .activation(Activation.TANH).l2(0.0001).weightInit(WeightInit.XAVIER)
//                .build());
//        listBuilder.layer(1, new GravesLSTM.Builder().nIn(nHidden).nOut(nHidden)
//                .activation(Activation.TANH).l2(0.0001).weightInit(WeightInit.XAVIER)
//                .build());
//        listBuilder.layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
//                .activation(Activation.IDENTITY).l2(0.0001).weightInit(WeightInit.XAVIER)
//                .nIn(nHidden).nOut(numOutputs).build());
//        listBuilder.backpropType(BackpropType.Standard);
//
//        MultiLayerConfiguration conf = listBuilder.build();
//        MultiLayerNetwork net = new MultiLayerNetwork(conf);
//        net.init();
//        //net.setListeners(new HistogramIterationListener(1));
//
//        INDArray output;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .seed(seed)
                .updater(new Adam())
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(numInput)
                        .nOut(nHidden)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new LSTM.Builder()
                        .nIn(nHidden)
                        .nOut(nHidden)
                        .activation(Activation.TANH)
                        .build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(nHidden)
                        .nOut(numOutputs)
                        .gradientNormalization(GradientNormalization.ClipL2PerLayer)
                        .gradientNormalizationThreshold(0.00001)
                        .build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        INDArray output;

        //Train the network on the full data set
        for( int i = 0; i < nEpochs; i++ ) {
            // train the model
            net.fit(trainingData);
            output = net.rnnTimeStep(trainingData.getFeatures());
            //System.out.println(output);
            net.rnnClearPreviousState();
        }


//        System.out.println("Result on training data: ");
//        System.out.println(net.rnnTimeStep(trainingData.getFeatures()));
//        System.out.println(trainingData.getFeatures());
//
//        System.out.println();
//
//        System.out.println("Result on test data: ");
//        System.out.println(net.rnnTimeStep(testData.getFeatures()));
//        System.out.println(testData.getFeatures());


        INDArray test = Nd4j.zeros(1, 1, 1);
        test.putScalar(0, 1.00);
        for (int i = 0; i < nSamples; i++) {
            output = net.rnnTimeStep(test);
            test.putScalar(0, output.getDouble(0));
            System.out.print(" " + output);
        }

    }

    /*
        Generate the training data. The sequence to train is out = 1, 2, 3, ..., 100.
        This corresponds to having as input the sequence seq = 0, 1, 2, ..., 99, so for this
        training data set the input attribute sequence is seq and the class/target attribute is out.
        The RNN should then be able to predict 101, 102, ... given the input 100, 101, ...
        That is: the last output is the next input.
     */
    private DataSet getTrainingData() {

        int countryId = 520;
        int violenceType = 3;
        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId, violenceType);

        double[] dataArray = predictionService.getArrayOfViolenceCounts(ucdpEventCounts);
        System.out.print("Data Array: ");
        for (double data : dataArray) {
            System.out.print(data + " ");
        }
        System.out.println();

        // Размер исходного массива данных
        int dataSize = dataArray.length;
        nSamples = dataSize - 1;

        // Размер массива данных без меток
        int inputDataSize = dataSize - 1;

        // Создаем массив для хранения входных данных
        double[] seq = Arrays.copyOfRange(dataArray, 0, inputDataSize);

        // Создаем массив для хранения меток
        double[] out = Arrays.copyOfRange(dataArray, 1, dataSize);

        System.out.println("Массив входных данных:");
        System.out.println(Arrays.toString(seq));

        System.out.println("Массив меток:");
        System.out.println(Arrays.toString(out));

        // Scaling to [0, 1] based on the training output
//        int min = 1;
//        int max = nSamples;
//        for(int i = 0; i < nSamples; i++) {
//            seq[i] = (seq[i] - min)/(max - min);
//            out[i] = (out[i] - min)/(max - min);
//        }

        INDArray seqNDArray = Nd4j.create(seq, new int[]{nSamples,1});
        INDArray inputNDArray = Nd4j.zeros(1,1,nSamples);
        //inputNDArray.putRow(0, seqNDArray.transpose());
        inputNDArray.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all()}, seqNDArray.transpose());

        INDArray outNDArray = Nd4j.create(out, new int[]{nSamples,1});
        INDArray outputNDArray = Nd4j.zeros(1,1,nSamples);
        //outputNDArray.putRow(0, outNDArray.transpose());
        outputNDArray.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all()}, outNDArray.transpose());

        DataSet dataSet = new DataSet(inputNDArray, outputNDArray);
        return dataSet;
    }

    private DataSet getTestData() {

        return getTrainingData();
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

//    public List<Double> predict(double[] inputData, int numTimeSteps) {
//        INDArray inputArray = Nd4j.create(inputData).reshape(1, 1, inputData.length);
//
//        // Прогноз
//        INDArray forecastArray = lstmModel.rnnTimeStep(inputArray);
//
//        return getResponse(forecastArray);
//    }

    public void trainModel(int numEpochs) {
//
//
//
//        int countryId = 520;
//        int violenceType = 3;
//        List<UCDPEventCount> ucdpEventCounts = ucdpEventCountRepository.findByCountryIdAndTypeOfViolence(countryId, violenceType);
//
//        double[] dataArray = predictionService.getArrayOfViolenceCounts(ucdpEventCounts);
//        System.out.print("Data Array: ");
//        for (double data : dataArray) {
//            System.out.print(data + " ");
//        }
//        System.out.println();
//
//        int trainingDataPercentage = 80;
//        int totalDataSize = dataArray.length;
//        int testStartIndex = (int) (totalDataSize * (trainingDataPercentage / 100.0));
//
//        double[] trainingDataArray = Arrays.copyOfRange(dataArray, 0, testStartIndex);
//        double[] testDataArray = Arrays.copyOfRange(dataArray, testStartIndex, totalDataSize);
//
//        val numSkipLines = 1;
//        val regression = true;
//        val batchSize = 32;
//
//        val trainFeatures = new CSVSequenceRecordReader(numSkipLines, ",");
//        trainFeatures.initialize( new NumberedFileInputSplit(featureBaseDir + "/%d.csv", 1, 1600));
//        val trainTargets = new CSVSequenceRecordReader(numSkipLines, ",");
//        trainTargets.initialize(new NumberedFileInputSplit(targetsBaseDir + "/%d.csv", 1, 1600));
//
//        val train = new SequenceRecordReaderDataSetIterator(trainFeatures, trainTargets, batchSize,
//                10, regression, SequenceRecordReaderDataSetIterator.AlignmentMode.EQUAL_LENGTH);
//
//        new SequenceRecordReaderDataSetIterator(SequenceRecordReader featuresReader, SequenceRecordReader labels,
//        int miniBatchSize, int numPossibleLabels)
//new ViewIterator();
//        train = new SequenceRecordReaderDataSetIterator(trainFeatures, trainTargets, batchSize,
//                10, true, SequenceRecordReaderDataSetIterator.AlignmentMode.EQUAL_LENGTH)
//        //INDArray labelArray = Nd4j.create(trainingData).reshape(1, 1, trainingData.length);
//        lstmModel.fit( inputArray, numEpochs );
        // Обучение
//        for (int epoch = 0; epoch < numEpochs; epoch++) {
//            lstmModel.fit(inputArray, labelArray);
//        }
    }




}
