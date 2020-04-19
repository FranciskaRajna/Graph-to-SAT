using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes
{
    class DIMACSReader
    {
        int numberOfVariables;
        int numberOfClauses;
        public List<int[]> cs = new List<int[]>();
        List<int> units = new List<int>();
        int[] copyBuffer;

        public DIMACSReader(string fileName)
        {
            try
            {
                StreamReader sr = new StreamReader(fileName);
                string clause = sr.ReadLine();
                while (clause[0] != 'p')
                {
                    clause = sr.ReadLine();
                }

                readPLine(clause);
                initDataStructure();
                clause = sr.ReadLine();

                while (clause != null)
                {
                    addCNFClause(clause);
                    clause = sr.ReadLine();
                }

                sr.Close();
            }
            catch (Exception)
            {

                MessageBox.Show("Hiba");
            }
        }

        private void readPLine(string cnfClause)
        {
            int i1 = 6;
            int i2 = cnfClause.IndexOf(" ", i1);

            numberOfVariables = int.Parse(javaStyleSubstring(cnfClause, i1, i2));

            while (cnfClause[i2] == ' ')
            {
                i2++;
            }

            int i3 = cnfClause.IndexOf(" ", i2);
            if (i3 == -1) i3 = cnfClause.Length;

            numberOfClauses = int.Parse(javaStyleSubstring(cnfClause, i2, i3));
        }

        private void initDataStructure()
        {
            copyBuffer = new int[numberOfVariables];
        }

        private void addCNFClause(String cnfClause)
        {
            if (cnfClause.Length == 0 || cnfClause[0] == '0' ||
                cnfClause[0] == 'c' || cnfClause[0] == '%')
            {
                return;
            }

            int i = 0;
            int i1 = 0;

            cnfClause = cnfClause.Replace("\t", " ");

            while (i1 < cnfClause.Length)
            {
                int i2 = cnfClause.IndexOf(" ", i1);
                if (i1 == i2)
                {
                    i1++;
                    continue;
                }

                if (i2 == -1) break;

                string lit = javaStyleSubstring(cnfClause, i1, i2);
                i1 = i2;
                int literal = int.Parse(lit);

                if (literal == 0) break;

                copyBuffer[i] = literal;
                i++;
            }

            int[] literals = new int[i];
            for (int index = 0; index < i; index++)
            {
                literals[index] = copyBuffer[index];
            }

            cs.Add(literals);

            if (literals.Length == 1 && !units.Contains(literals[0]))
            {
                units.Add(literals[0]);
            }
        }

        private string javaStyleSubstring(string s, int beginIndex, int endIndex)
        {
            return s.Substring(beginIndex, endIndex - beginIndex);
        }
    }
}
