using PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls
{
    /// <summary>
    /// Interaction logic for MakeNeuralNetworkControl.xaml
    /// </summary>
    public partial class MakeNeuralNetworkControl : UserControl
    {
        public MakeNeuralNetworkControl()
        {
            InitializeComponent();

            this.ucInputLayer.Content = new NetwokLayerSizeControl();
            this.ucOutputLayer.Content = new NetwokLayerSizeControl();
            this.wp_layers.Items.Add(new NetwokLayerSizeControl());

            this.cb_activationFunctions.Items.Add("Sigmoid");
            this.cb_activationFunctions.Items.Add("TanH");
            this.cb_activationFunctions.Items.Add("ReLU");
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            this.wp_layers.Items.Add(new NetwokLayerSizeControl());
        }

        private void btn_createNetwork_Click(object sender, RoutedEventArgs e)
        {
            int[] layers = new int[this.wp_layers.Items.Count + 2];
            layers[0] = Convert.ToInt32((this.ucInputLayer.Content as NetwokLayerSizeControl).GetLayerSize());

            for (int i = 1; i < layers.Length - 1; i++)
            {
                layers[i] = Convert.ToInt32((this.wp_layers.Items[i-1] as NetwokLayerSizeControl).GetLayerSize());
            }

            layers[layers.Length-1] = Convert.ToInt32((this.ucOutputLayer.Content as NetwokLayerSizeControl).GetLayerSize());

            ActivationStrategy acti = new Sigmoid();
            if (this.cb_activationFunctions.SelectedIndex == 0) acti = new Sigmoid();
            else if (this.cb_activationFunctions.SelectedIndex == 1) acti = new TanH();
            else if (this.cb_activationFunctions.SelectedIndex == 2) acti = new ReLU();

            NetworkHelper.SaveNetToFile(new NeuralNetwork(layers, acti), "neuralNetwork.txt");
            MessageBox.Show("Success!");
        }
    }
}
