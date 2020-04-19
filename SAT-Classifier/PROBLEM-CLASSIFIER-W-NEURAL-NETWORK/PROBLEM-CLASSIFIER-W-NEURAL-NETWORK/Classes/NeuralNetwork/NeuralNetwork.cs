using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork
{
    class NeuralNetwork
    {
        private Layer[] layers;
        public Layer[] Layers
        {
            get { return layers; }
            set { layers = value; }
        }

        private string layerBuild;
        public string GetLayerBuild
        {
            get { return layerBuild; }
        }

        private ActivationStrategy activation;
        public ActivationStrategy Activation
        {
            get { return activation; }
            set { activation = value; }
        }

        public double[] FeedForward(double[] input)
        {
            double[] Output = input;
            for (int i = 0; i < Layers.Length; i++)
            {
                Output = layers[i].FeedForward(Output);
            }
            return Output;
        }

        public void BackProp(double[] expected)
        {
            Layers[Layers.Length - 1].BackPropOutput(expected);
            for (int i = Layers.Length - 2; i >= 0; i--)
            {
                Layers[i].BackPropHidden(Layers[i + 1].Gamma, Layers[i + 1].Weights);
            }

            for (int i = 0; i < Layers.Length; i++)
            {
                Layers[i].UpdateWeights();
            }
        }

        public NeuralNetwork(int[] Layer, ActivationStrategy ActivationFunction)
        {
            Layers = new Layer[Layer.Length - 1];
            for (int i = 0; i < Layers.Length; i++)
            {
                Layers[i] = new Layer(Layer[i], Layer[i+1], ActivationFunction);

                //Layers[i].InitializeLayer(Layer[i], Layer[i + 1], ActivationFunction);

                if (i < Layers.Length - 1) this.layerBuild += Layer[i] + "-";
                else this.layerBuild += Layer[i];
            }

            this.Activation = ActivationFunction;
        }
    }
}
