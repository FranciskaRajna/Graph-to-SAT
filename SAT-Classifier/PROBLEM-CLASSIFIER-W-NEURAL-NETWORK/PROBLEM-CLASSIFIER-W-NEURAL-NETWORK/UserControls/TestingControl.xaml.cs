using PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
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
    /// Interaction logic for TestingControl.xaml
    /// </summary>
    public partial class TestingControl : UserControl
    {
        private List<double[]> inputs;
        private NeuralNetwork nn;

        private List<string> labels;
        private Thread t1;

        public TestingControl(List<string> labels)
        {
            InitializeComponent();

            this.inputs = new List<double[]>();
            this.labels = labels;

            t1 = new Thread(() => createStatistics(this.rtb_log));
        }

        private void btn_testingSetsBrowse_Click(object sender, RoutedEventArgs e)
        {
            System.Windows.Forms.OpenFileDialog ofd = new System.Windows.Forms.OpenFileDialog();
            ofd.Filter = "Text files (*.txt)|*.txt";

            if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                this.tb_testingSets.Text = ofd.FileName;
                StreamReader sr = new StreamReader(ofd.FileName);

                this.inputs = new List<double[]>();

                string[] atm;
                double[] atmInput;
                string[] atmInputLine;

                while (!sr.EndOfStream)
                {
                    atm = sr.ReadLine().Split(':');
                    atmInput = new double[atm[0].Split(';').Length];

                    atmInputLine = atm[0].Split(';');
                    for (int i = 0; i < atmInput.Length; i++)
                    {
                        atmInput[i] = Convert.ToDouble(atmInputLine[i]);
                    }

                    inputs.Add(atmInput);
                }
                sr.Close();
            }
        }

        private void btn_neuralNetworkBrowse_Click(object sender, RoutedEventArgs e)
        {
            System.Windows.Forms.OpenFileDialog ofd = new System.Windows.Forms.OpenFileDialog();
            ofd.Filter = "Text files (*.txt)|*.txt";

            if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                this.tb_neuralNetwork.Text = ofd.FileName;
                NetworkHelper.CreateNetworkByTxt(ref nn, ofd.FileName);
            }
        }

        private void btn_start_Click(object sender, RoutedEventArgs e)
        {
            TextRange txt = new TextRange(this.rtb_log.Document.ContentStart, this.rtb_log.Document.ContentEnd);
            txt.Text = "";

            t1.Start();
        }

        private void createStatistics(RichTextBox rtb)
        {
            this.Dispatcher.Invoke(() => rtb.AppendText("Outputs:\n\n"));

            double[] actualResult;
            int[] statistics = new int[this.labels.Count];
            int maxJ;

            for (int i = 0; i < inputs.Count; i++)
            {
                actualResult = nn.FeedForward(inputs[i]);
                maxJ = 0;
                for (int j = 1; j < actualResult.Length; j++)
                {
                    if (actualResult[j] > actualResult[maxJ]) maxJ = j;
                }

                statistics[maxJ]++;
                this.Dispatcher.Invoke(() => rtb.AppendText(String.Format("{0}. -> {1}\n", i+1, this.labels[maxJ])));
                this.Dispatcher.Invoke(() => rtb_log.ScrollToEnd());
            }

            for (int i = 0; i < statistics.Length; i++)
            {
                if(statistics[i] > 0) this.Dispatcher.Invoke(() => rtb.AppendText(String.Format("{0}({1}) ", labels[i], statistics[i])));
            }
            this.Dispatcher.Invoke(() => rtb.AppendText("Completed!"));
            t1 = new Thread(() => createStatistics(this.rtb_log));
        }
    }
}
