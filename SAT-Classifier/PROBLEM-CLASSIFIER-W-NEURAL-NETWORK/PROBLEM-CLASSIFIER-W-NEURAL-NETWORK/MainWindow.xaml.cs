using PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls;
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

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK
{
    public partial class MainWindow : Window
    {
        CreateLearningLabelsControl createLearningLabelsControl;
        CreateLearningSetControl createLearningSetControl;
        MakeNeuralNetworkControl makeNeuralNetworkControl;
        TrainingControl trainingControl;
        TestingControl testingControl;

        List<string> labels;

        public MainWindow()
        {
            InitializeComponent();
            labels = new List<string>();
        }

        private void btn_learningLabels_Click(object sender, RoutedEventArgs e)
        {
            if (createLearningLabelsControl == null) createLearningLabelsControl = new CreateLearningLabelsControl();
            this.userControlHolder.Content = createLearningLabelsControl;
        }

        private void btn_learningSet_Click(object sender, RoutedEventArgs e)
        {
            labels = (createLearningLabelsControl == null)? null: createLearningLabelsControl.GetLabels();

            if (labels != null && labels.Count > 0)
            {
                if (createLearningSetControl == null) createLearningSetControl = new CreateLearningSetControl();
                createLearningSetControl.refreshLabels(this.labels);
                this.userControlHolder.Content = createLearningSetControl;
            }
            else MessageBox.Show("You need to set at least one or more label first!");
        }

        private void btn_makeNeuralNetwork_Click(object sender, RoutedEventArgs e)
        {
            if (makeNeuralNetworkControl == null) makeNeuralNetworkControl = new MakeNeuralNetworkControl();
            this.userControlHolder.Content = makeNeuralNetworkControl;
        }

        private void btn_training_Click(object sender, RoutedEventArgs e)
        {
            if (trainingControl == null) trainingControl = new TrainingControl();
            this.userControlHolder.Content = trainingControl;
        }

        private void btn_testing_Click(object sender, RoutedEventArgs e)
        {
            labels = (createLearningLabelsControl == null) ? null : createLearningLabelsControl.GetLabels();

            if (this.labels != null && this.labels.Count > 0)
            {
                if (testingControl == null) testingControl = new TestingControl(this.labels);
                this.userControlHolder.Content = testingControl;
            }
            else MessageBox.Show("You need to set at least one or more label first!");
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (trainingControl != null) trainingControl.AbortThread();
        }
    }
}
