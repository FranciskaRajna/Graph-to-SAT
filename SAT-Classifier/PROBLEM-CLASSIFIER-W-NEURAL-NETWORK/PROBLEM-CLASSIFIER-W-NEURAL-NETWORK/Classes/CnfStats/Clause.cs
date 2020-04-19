using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.Classes
{
    class Clause
    {
        public int[] vars;
        int[] literals;
        public bool[] isPos;

        public Clause(int[] literals)
        {
            this.literals = literals;
            vars = new int[literals.Length];
            for (int i = 0; i < literals.Length; i++) { vars[i] = Math.Abs(literals[i]); }
            isPos = new bool[literals.Length];
            for (int i = 0; i < literals.Length; i++) { isPos[i] = (literals[i] > 0); }
        }
    }
}
