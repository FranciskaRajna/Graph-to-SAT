using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes
{
    class DIMACSStatisticsBuilder
    {
        int numberOfVariables = 0;
        int numberOfClauses = 0;
        int[] numberOfKClauses = new int[14]; // the 13.th counts 13 or even lengther clauses 
        int numberOfBlackClauses = 0;
        int numberOfWhiteClauses = 0;
        int numberOfDefiniteHornClauses = 0;
        int numberOfStraitClauses = 0;
        int numberOfPositiveLiterals = 0;
        int numberOfNegativeLiterals = 0;
        double ratioOfClausesAndVariables;
        double[] ratioOfKClauses = new double[14];
        double ratioOfBlackClauses;
        double ratioOfWhiteClauses;
        double ratioOfDefiniteHornClauses;
        double ratioOfStraitClauses;
        double ratioOfPositiveLiterals;
        double ratioOfNegativeLiterals;
        bool mayBePigeonHole;
        bool mayBeRandom3SAT;
        bool mayBeRandomAIM;
        bool mayBeNemesisFormula;
        bool mayBeDubois;

        public DIMACSStatisticsBuilder(DIMACSReader reader)
        {
            List<int[]> cs = reader.cs;
            for (int i = 0; i < cs.Count; i++)
            {
                Clause c = new Clause(cs[i]);
                addClause(c);
            }
        }

        public DIMACSStatisticsBuilder addClause(Clause c)
        {
            this.numberOfClauses++;

            int length = c.vars.Length;
            int numberOfPos = 0;
            for (int i = 0; i < length; i++)
            {
                if (c.vars[i] > numberOfVariables) numberOfVariables = c.vars[i];
                if (c.isPos[i])
                {
                    numberOfPos++;
                    numberOfPositiveLiterals++;
                }
                else { numberOfNegativeLiterals++; }
            }
            int numberOfNeg = length - numberOfPos;
            if (length < 13) numberOfKClauses[length]++; else numberOfKClauses[13]++;
            if (numberOfPos == length) numberOfWhiteClauses++;
            if (numberOfNeg == length) numberOfBlackClauses++;
            if (numberOfPos == 1 && numberOfNeg > 0) numberOfDefiniteHornClauses++;
            if (numberOfNeg == 1 && numberOfPos > 0) numberOfStraitClauses++;
            return this;
        }

        public DIMACSStatisticsBuilder finalizy()
        {
            ratioOfClausesAndVariables = (double)numberOfClauses / numberOfVariables;
            for (int i = 0; i < numberOfKClauses.Length; i++)
            {
                ratioOfKClauses[i] = (double)numberOfKClauses[i] / numberOfClauses;
            }
            ratioOfBlackClauses = (double)numberOfBlackClauses / numberOfClauses;
            ratioOfWhiteClauses = (double)numberOfWhiteClauses / numberOfClauses;
            ratioOfDefiniteHornClauses = (double)numberOfDefiniteHornClauses / numberOfClauses;
            ratioOfStraitClauses = (double)numberOfStraitClauses / numberOfClauses;
            ratioOfPositiveLiterals = (double)numberOfPositiveLiterals / (numberOfPositiveLiterals + numberOfNegativeLiterals);
            ratioOfNegativeLiterals = (double)numberOfNegativeLiterals / (numberOfPositiveLiterals + numberOfNegativeLiterals);
            mayBePigeonHole = mayItBePigeonHole();
            mayBeRandom3SAT = mayItBeRandom3SAT();
            mayBeRandomAIM = mayItBeRandomAIM();
            mayBeNemesisFormula = mayItBeNemesisFormula();
            mayBeDubois = mayItBeDubois();

            return this;
        }

        private bool mayItBePigeonHole()
        {
            int binary = numberOfKClauses[2];
            if (binary != numberOfBlackClauses) return false;
            if (numberOfBlackClauses + numberOfWhiteClauses != numberOfClauses) return false;
            int nonBinary = numberOfClauses - binary;
            if (nonBinary < 4) return false;
            int k = nonBinary - 1;
            k = k > 12 ? 13 : k;
            if (numberOfKClauses[k] != k + 1) return false;
            return true;
        }

        private bool mayItBeRandom3SAT()
        {
            if (numberOfKClauses[3] != numberOfClauses) return false;
            if (Math.Abs(ratioOfClausesAndVariables - 4.267) > 0.3) return false;
            return true;
        }

        private bool mayItBeRandomAIM()
        {
            if (numberOfKClauses[2] > 1) return false;
            if (numberOfKClauses[2] + numberOfKClauses[3] != numberOfClauses) return false;
            if (Math.Abs(ratioOfPositiveLiterals - 0.5) > 0.2) return false;
            if (Math.Abs(ratioOfNegativeLiterals - 0.5) > 0.2) return false;
            if (numberOfPositiveLiterals == numberOfNegativeLiterals) return false;
            return true;
        }

        private bool mayItBeNemesisFormula()
        {
            if (ratioOfKClauses[1] > 0.1) return false;
            if (ratioOfKClauses[1] > 0.1) return false;
            if (ratioOfKClauses[2] < 0.4) return false;
            if (ratioOfKClauses[3] < 0.2) return false;
            if (ratioOfKClauses[2] <= ratioOfKClauses[3]) return false;
            return true;
        }

        private bool mayItBeDubois()
        {
            if (numberOfKClauses[3] != numberOfClauses) return false;
            if (numberOfBlackClauses != 1) return false;
            if (numberOfPositiveLiterals != numberOfNegativeLiterals) return false;
            return true;
        }

        /**
         * Előállítja a probléma statisztikája alapján a neurális háló által elfogadott bemenetet
         * @param problemType A probléma típusa
         * @param toFile A cél file neve
         */
        public void printToFile(string label, string toFile, List<string> labels)
        {            
            try
            {
                StreamWriter sw = new StreamWriter(toFile, true);
                StringBuilder builder = new StringBuilder();

                builder.Append(numberOfVariables).Append(";");
                builder.Append(numberOfClauses).Append(";");

                sw.Flush();

                for (int i = 0; i < numberOfKClauses.Length; i++)
                {
                    builder.Append(numberOfKClauses[i]).Append(";");
                    sw.Flush();
                }


                builder.Append(numberOfBlackClauses).Append(";");
                builder.Append(numberOfWhiteClauses).Append(";");
                builder.Append(numberOfDefiniteHornClauses).Append(";");
                builder.Append(numberOfStraitClauses).Append(";");
                builder.Append(numberOfPositiveLiterals).Append(";");
                builder.Append(numberOfNegativeLiterals).Append(";");
                builder.Append(ratioOfClausesAndVariables).Append(";");

                sw.Flush();

                for (int i = 0; i < ratioOfKClauses.Length; i++)
                {
                    builder.Append(ratioOfKClauses[i]).Append(";");
                }

                builder.Append(ratioOfBlackClauses).Append(";");
                builder.Append(ratioOfWhiteClauses).Append(";");
                builder.Append(ratioOfDefiniteHornClauses).Append(";");
                builder.Append(ratioOfStraitClauses).Append(";");
                builder.Append(ratioOfPositiveLiterals).Append(";");
                builder.Append(ratioOfNegativeLiterals).Append(";");

                if (mayBePigeonHole) builder.Append("1.0").Append(";");
                else builder.Append("0.0").Append(";");

                if (mayBeRandom3SAT) builder.Append("1.0").Append(";");
                else builder.Append("0.0").Append(";");

                if (mayBeRandomAIM) builder.Append("1.0").Append(";");
                else builder.Append("0.0").Append(";");

                if (mayBeNemesisFormula) builder.Append("1.0").Append(";");
                else builder.Append("0.0").Append(";");

                if (mayBeDubois) builder.Append("1.0").Append(":");
                else builder.Append("0.0").Append(":");

                for (int i = 0; i < labels.Count; i++)
                {
                    if (label != labels[i]) builder.Append("0");
                    else builder.Append("1");

                    if (i < labels.Count - 1) builder.Append(";");
                }

                sw.WriteLine(builder.ToString().Replace('.', ','));
                sw.Close();
            }
            catch (Exception)
            {
                MessageBox.Show("Hiba a statisztika készítése során!");
            }
        }
    }
}
