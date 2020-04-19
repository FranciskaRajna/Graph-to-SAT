using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork
{
    class TanH : ActivationStrategy
    {
        public override double Activation(double input)
        {
            return (float)Math.Tanh(input);
        }

        public override double DeActivation(double output)
        {
            return 1 - (output * output);
        }

        public override string ToString()
        {
            return "TanH";
        }
    }
}
