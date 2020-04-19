using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork
{
    class Sigmoid : ActivationStrategy
    {
        public override double Activation(double input)
        {
            return (1.0 / (1.0 + Math.Pow(Math.E, -input)));
        }

        public override double DeActivation(double output)
        {
            return (output * (1.0 - output));
        }

        public override string ToString()
        {
            return "Sigmoid";
        }
    }
}
