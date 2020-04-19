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
    /// Interaction logic for CreateLearningLabelsControl.xaml
    /// </summary>
    public partial class CreateLearningLabelsControl : UserControl
    {
        private List<string> labels;

        public CreateLearningLabelsControl()
        {
            InitializeComponent();
            labels = new List<string>();
        }

        private void btn_addLabel_Click(object sender, RoutedEventArgs e)
        {
            this.wp_labels.Items.Add(new LearningLabelControl(this.tb_label.Text));
            labels.Add(this.tb_label.Text);

            this.tb_label.Text = "";
        }

        public List<string> GetLabels()
        {
            return this.labels;
        }
    }
}
