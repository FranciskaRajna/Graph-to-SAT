using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork
{
    class Layer
    {
        public static double learningRate = 0.033f;

        private ActivationStrategy activationStrategy;
        public ActivationStrategy ActivationStrategy
        {
            get { return activationStrategy; }
            set { activationStrategy = value; }
        }

        protected int numberOfInput;
        public int NumberOfInput
        {
            get { return numberOfInput; }
            set { numberOfInput = value; }
        }

        protected int numberOfOutput;
        public int NumberOfOutput
        {
            get { return numberOfOutput; }
            set { numberOfOutput = value; }
        }

        private double[] input;
        public double[] Input
        {
            get { return input; }
            set { input = value; }
        }

        private double[] output;
        public double[] Output
        {
            get { return output; }
            set { output = value; }
        }

        protected double[,] weights;
        public double[,] Weights
        {
            get { return weights; }
            set { weights = value; }
        }

        private double[,] weightsDelta;
        public double[,] WeightsDelta
        {
            get { return weightsDelta; }
            set { weightsDelta = value; }
        }

        private double[] gamma;
        public double[] Gamma
        {
            get { return gamma; }
            set { gamma = value; }
        }

        private double[] error;
        public double[] Error
        {
            get { return error; }
            set { error = value; }
        }

        public double[] FeedForward(double[] Input)
        {
            this.Input = Input;

            for (int i = 0; i < NumberOfOutput; i++)
            {
                Output[i] = 0;
                for (int j = 0; j < NumberOfInput; j++)
                {
                    Output[i] += this.Input[j] * weights[i, j];
                }
                Output[i] = ActivationStrategy.Activation(Output[i]);
            }
            return Output;
        }

        public void BackPropHidden(double[] gammaForward, double[,] weightsForward)
        {
            for (int i = 0; i < NumberOfOutput; i++)
            {
                Gamma[i] = 0;

                for (int j = 0; j < gammaForward.Length; j++)
                {
                    Gamma[i] += gammaForward[j] * weightsForward[j, i];
                }
                Gamma[i] *= ActivationStrategy.DeActivation(Output[i]);
            }

            for (int i = 0; i < NumberOfOutput; i++)
            {
                for (int j = 0; j < NumberOfInput; j++)
                {
                    WeightsDelta[i, j] = Gamma[i] * Input[j];
                }
            }
        }

        public void BackPropOutput(double[] expected)
        {
            for (int i = 0; i < NumberOfOutput; i++)
            {
                Error[i] = Output[i] - expected[i];
            }

            for (int i = 0; i < NumberOfOutput; i++)
            {
                Gamma[i] = Error[i] * ActivationStrategy.DeActivation(Output[i]);
            }

            for (int i = 0; i < NumberOfOutput; i++)
            {
                for (int j = 0; j < NumberOfInput; j++)
                {
                    WeightsDelta[i, j] = Gamma[i] * Input[j];
                }
            }
        }

        protected void InitializeWeights()
        {
            Random rnd = new Random();

            for (int i = 0; i < NumberOfOutput; i++)
            {
                for (int j = 0; j < NumberOfInput; j++)
                {
                    weights[i, j] = rnd.NextDouble() - 0.5f;
                }
            }
        }

        public void UpdateWeights()
        {
            for (int i = 0; i < NumberOfOutput; i++)
            {
                for (int j = 0; j < NumberOfInput; j++)
                {
                    weights[i, j] -= WeightsDelta[i, j] * learningRate;
                }
            }
        }

        public Layer(int NumberOfInput, int NumberOfOutput, ActivationStrategy ActivationStrategy)
        {
            this.NumberOfInput = NumberOfInput;
            this.NumberOfOutput = NumberOfOutput;
            this.ActivationStrategy = ActivationStrategy;
            this.Input = new double[numberOfInput];
            this.Output = new double[NumberOfOutput];
            this.Weights = new double[numberOfOutput, numberOfInput];
            this.Gamma = new double[numberOfOutput];
            this.WeightsDelta = new double[numberOfOutput, numberOfInput];
            this.Error = new double[NumberOfOutput];
            InitializeWeights();
        }
    }
}
