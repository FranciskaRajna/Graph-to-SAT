using PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes.NeuralNetwork;
using System;
using System.Collections.Generic;
using System.ComponentModel;
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
    /// Interaction logic for TrainingControl.xaml
    /// </summary>
    public partial class TrainingControl : UserControl
    {
        NeuralNetwork nn;
        List<double[]> inputs;
        List<double[]> outputs;
        Thread t1;
        int reps;


        public TrainingControl()
        {
            InitializeComponent();

            inputs = new List<double[]>();
            outputs = new List<double[]>();
            t1 = new Thread(() => Learning(this.rtb_log, this.pb_training, this.btn_start));
            reps = 0;
        }

        private void btn_trainingSetsBrowse_Click(object sender, RoutedEventArgs e)
        {
            System.Windows.Forms.OpenFileDialog ofd = new System.Windows.Forms.OpenFileDialog();
            ofd.Filter = "Text files (*.txt)|*.txt";

            if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                this.tb_trainingSets.Text = ofd.FileName;
                StreamReader sr = new StreamReader(ofd.FileName);

                this.inputs = new List<double[]>();
                this.outputs = new List<double[]>();

                string[] atm;
                double[] atmInput;
                double[] atmOutput;
                string[] atmInputLine;
                string[] atmOutputLine;

                while (!sr.EndOfStream)
                {
                    atm = sr.ReadLine().Split(':');
                    atmInput = new double[atm[0].Split(';').Length];
                    atmOutput = new double[atm[1].Split(';').Length];

                    atmInputLine = atm[0].Split(';');
                    for (int i = 0; i < atmInput.Length; i++)
                    {
                        atmInput[i] = Convert.ToDouble(atmInputLine[i]);
                    }

                    atmOutputLine = atm[1].Split(';');
                    for (int i = 0; i < atmOutput.Length; i++)
                    {
                        atmOutput[i] = Convert.ToDouble(atmOutputLine[i]);
                    }

                    inputs.Add(atmInput);
                    outputs.Add(atmOutput);
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
            if (this.tb_trainingSets.Text.Length > 0 && this.tb_neuralNetwork.Text.Length > 0 && this.tb_reps.Text.Length > 0)
            {
                this.btn_start.IsEnabled = false;
                this.btn_saveNetwork.IsEnabled = true;

                this.reps = Convert.ToInt32(this.tb_reps.Text.ToString());
                this.pb_training.Value = 0;
                this.pb_training.Minimum = 0;
                this.pb_training.Maximum = this.reps;

                TextRange txt = new TextRange(this.rtb_log.Document.ContentStart, this.rtb_log.Document.ContentEnd);
                txt.Text = "";

                t1.Start();
            }
            else MessageBox.Show("Browse the files and set the reps first!");
        }

        private void Learning(RichTextBox rtb, ProgressBar pb, Button btn_start)
        {
            this.Dispatcher.Invoke(() => rtb.AppendText(String.Format("Training...(0/{0})\n\n", this.reps)));

            for (int j = 0; j < this.reps; j++)
            {
                for (int i = 0; i < inputs.Count; i++)
                {
                    try
                    {
                        lock (nn)
                        {
                            nn.FeedForward(inputs[i]);
                            nn.BackProp(outputs[i]);
                        }
                    }
                    catch (Exception)
                    {
                        this.Dispatcher.Invoke(() => rtb.AppendText("Failed"));
                    }
                }
                this.Dispatcher.Invoke(() => rtb.AppendText(String.Format("Training...({0}/{1})\n", j + 1, this.reps)));
                this.Dispatcher.Invoke(() => rtb_log.ScrollToEnd());

                this.Dispatcher.Invoke(() => pb.Value = j + 1);
            }

            this.Dispatcher.Invoke(() => btn_start.IsEnabled = true);
            this.Dispatcher.Invoke(() => pb.Value = 0);
            this.Dispatcher.Invoke(() => rtb_log.AppendText("Completed!\n"));

            t1 = new Thread(() => Learning(this.rtb_log, this.pb_training, this.btn_start));
        }

        private void btn_saveNetwork_Click(object sender, RoutedEventArgs e)
        {
            lock (nn)
            {
                NetworkHelper.SaveNetToFile(nn, "neuralNetwork.txt");
                this.rtb_log.AppendText("Network saved! \n");
            }
        }

        public void AbortThread()
        {
            t1.Abort();
        }
    }
}
