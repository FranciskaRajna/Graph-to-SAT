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
    /// Interaction logic for LearningLabelControl.xaml
    /// </summary>
    public partial class LearningLabelControl : UserControl
    {
        public LearningLabelControl(string label)
        {
            InitializeComponent();
            this.label.Content = label;
        }
    }
}
