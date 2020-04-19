using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork
{
    class NetworkHelper
    {
        public static void SaveNetToFile(NeuralNetwork net, string fileName)
        {
            StreamWriter sw = new StreamWriter(fileName);

            for (int i = 0; i < net.Layers.Length; i++)
            {
                sw.Write(net.Layers[i].NumberOfInput.ToString());
                sw.Write(":");
                if (i == net.Layers.Length - 1) sw.Write(net.Layers[i].NumberOfOutput.ToString());
            }
            sw.WriteLine(":" + net.Activation);

            foreach (Layer layer in net.Layers)
            {
                sw.WriteLine(layer.NumberOfInput.ToString() + ":" + layer.NumberOfOutput.ToString());

                for (int i = 0; i < layer.Output.Length; i++)
                {
                    sw.Write(layer.Output[i].ToString());
                    if (i < layer.Output.Length - 1) sw.Write(":");
                }
                sw.WriteLine();

                for (int i = 0; i < layer.Input.Length; i++)
                {
                    sw.Write(layer.Input[i].ToString());
                    if (i < layer.Input.Length - 1) sw.Write(":");
                }
                sw.WriteLine();

                for (int i = 0; i < layer.NumberOfOutput; i++)
                {
                    for (int j = 0; j < layer.NumberOfInput; j++)
                    {
                        sw.Write(layer.Weights[i, j].ToString());
                        if (j < layer.NumberOfInput - 1) sw.Write(":");
                    }
                    sw.WriteLine();
                }
            }
            sw.Close();
        }

        public static void CreateNetworkByTxt(ref NeuralNetwork net, string fileName)
        {
            net = null;
            StreamReader sr;
            try
            {
                sr = new StreamReader(fileName);
            }
            catch (FileNotFoundException)
            {
                MessageBox.Show("File not found!");
                return;
            }
            catch (Exception)
            {
                MessageBox.Show("Unknow exception!");
                return;
            }

            string[] atm = sr.ReadLine().Split(':');

            string actiInString = atm[atm.Length - 1];
            ActivationStrategy acti = new Sigmoid();
            if (actiInString == "TanH") acti = new TanH();
            else if (actiInString == "ReLU") acti = new ReLU();

            int[] layers = new int[atm.Length - 1];
            for (int i = 0; i < atm.Length - 1; i++)
            {
                layers[i] = Convert.ToInt32(atm[i]);
            }

            net = new NeuralNetwork(layers, acti);

            List<Layer> atmLayers = new List<Layer>();
            while (!sr.EndOfStream)
            {
                string[] atm2 = sr.ReadLine().Split(':');
                int atmNumberOfInputs = Convert.ToInt32(atm2[0].ToString());
                int atmNumberOfOutputs = Convert.ToInt32(atm2[1].ToString());

                Layer atmLayer = new Layer(atmNumberOfInputs, atmNumberOfOutputs, acti);

                double[] atmOutputs = new double[atmNumberOfOutputs];
                atm = sr.ReadLine().Split(':');
                for (int i = 0; i < atmOutputs.Length; i++)
                {
                    atmOutputs[i] = Convert.ToDouble(atm[i].ToString());
                }
                atmLayer.Output = atmOutputs;

                double[] atmInputs = new double[atmNumberOfInputs];
                atm = sr.ReadLine().Split(':');
                for (int i = 0; i < atmInputs.Length; i++)
                {
                    atmInputs[i] = Convert.ToDouble(atm[i].ToString());
                }
                atmLayer.Input = atmInputs;

                double[,] atmWeights = new double[atmNumberOfOutputs, atmNumberOfInputs];

                for (int i = 0; i < atmNumberOfOutputs; i++)
                {
                    atm = sr.ReadLine().Split(':');
                    for (int k = 0; k < atmNumberOfInputs; k++)
                    {
                        atmWeights[i, k] = Convert.ToDouble(atm[k].ToString());
                    }
                }
                atmLayer.Weights = atmWeights;

                atmLayers.Add(atmLayer);
            }

            net.Layers = atmLayers.ToArray();

            if (sr != null)
            {
                sr.Close();
            }
        }
    }
}
