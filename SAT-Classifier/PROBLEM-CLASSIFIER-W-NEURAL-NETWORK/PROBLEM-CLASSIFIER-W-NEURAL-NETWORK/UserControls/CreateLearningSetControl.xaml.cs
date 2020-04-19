using PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes;
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
    /// Interaction logic for CreateLearningSetControl.xaml
    /// </summary>
    public partial class CreateLearningSetControl : UserControl
    {
        private string fromFolderPath;
        private string toFolderPath;
        private List<string> labels;

        public CreateLearningSetControl()
        {
            InitializeComponent();

            fromFolderPath = "";
            toFolderPath = "";
            labels = new List<string>();
        }

        private void btn_fromBrowse_Click(object sender, RoutedEventArgs e)
        {
            System.Windows.Forms.FolderBrowserDialog folderDialog = new System.Windows.Forms.FolderBrowserDialog();
            folderDialog.ShowDialog();
            this.fromFolderPath = folderDialog.SelectedPath;

            this.tb_from.Text = this.fromFolderPath;
        }

        private void btn_toBrowse_Click(object sender, RoutedEventArgs e)
        {
            System.Windows.Forms.FolderBrowserDialog folderDialog = new System.Windows.Forms.FolderBrowserDialog();
            folderDialog.ShowDialog();
            this.toFolderPath = folderDialog.SelectedPath;

            this.tb_to.Text = this.toFolderPath;
        }

        private void btn_create_Click(object sender, RoutedEventArgs e)
        {
            if (this.tb_from.Text.Length > 0 && this.tb_to.Text.Length > 0 && this.cb_label.SelectedIndex != -1)
            {
                CnfStatsMaker.CreateStats(this.fromFolderPath, this.toFolderPath, this.labels[this.cb_label.SelectedIndex], this.labels);
                MessageBox.Show("Success!");
            }
            else MessageBox.Show("Please select the path and the label first!");
        }

        public string getFromPath()
        {
            return fromFolderPath;
        }

        public string getToPath()
        {
            return toFolderPath;
        }

        public void refreshLabels(List<string> labels)
        {
            this.cb_label.Items.Clear();
            this.labels = labels;

            for (int i = 0; i < labels.Count; i++)
            {
                this.cb_label.Items.Add(labels[i]);
            }
        }
    }
}
