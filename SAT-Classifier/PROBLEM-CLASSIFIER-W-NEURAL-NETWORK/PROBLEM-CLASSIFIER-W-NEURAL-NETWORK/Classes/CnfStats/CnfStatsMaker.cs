using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes
{
    static class CnfStatsMaker
    {
        static public void CreateStats(string from, string to, string label, List<string> labels)
        {
            if (getFilenamesWithPath(from) != null)
            {
                List<string> files = getFilenames(from);
                for (int i = 0; i < files.Count; i++)
                {
                    DIMACSStatisticsBuilder sb = new DIMACSStatisticsBuilder(new DIMACSReader(from + "\\" + files[i]));
                    sb.finalizy();
                    sb.printToFile(label, to + "\\" + label + ".txt", labels);
                }
            }
            else
            {
                MessageBox.Show("A mappa üres");
            }
        }

        static public List<string> getFilenamesWithPath(string from)
        {
            return Directory.GetFiles(from).ToList();
        }

        static public List<string> getFilenames(string from)
        {
            List<string> filenames = new List<string>();

            foreach (string filename in Directory.GetFiles(from).Select(Path.GetFileName))
            {
                filenames.Add(filename);
            }

            return filenames;
        }
    }
}
