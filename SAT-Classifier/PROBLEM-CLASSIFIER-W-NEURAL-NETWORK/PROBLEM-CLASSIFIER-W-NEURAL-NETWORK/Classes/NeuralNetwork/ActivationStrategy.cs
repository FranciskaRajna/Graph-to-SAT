using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork
{
    public abstract class ActivationStrategy
    {
        public abstract double Activation(double input);

        public abstract double DeActivation(double output);

        public abstract override string ToString();
    }
}
