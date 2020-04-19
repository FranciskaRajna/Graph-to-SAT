/**
Weakly Nondecisive SAT problem generator v.2.0. 
@author Gabor Kusper, gkusper@aries.ektf.hu
@author Csaba Biro, birocs@aries.ektf.hu
@author Tibor Tajti, tajti@aries.ektf.hu
@version v2.0. /31.08.2013. - 07.01.2014./
@see http://fmv.ektf.hu/
*/

import java.util.Random;
import java.io.*;

public class WnDGen2 {
	
	public static void printUsage()
	{
		System.out.println("This program generates Weakly Nondecisive (WnD) clause sets.");
		System.out.println("The definiton of WnD clause set is:");
		System.out.println("  A clause set is WnD iff all its clauses are WnD.");
		System.out.println("  A clause is WnD iff it has a WnD literal.");
		System.out.println("  A literal is WnD iff it is blocked or resolvents on it are subsumed.");
		System.out.println("  Or by formula: WnD(S) :<=> " +
				"for_all[C in S] exists[c in C] for_all[B in S][not(c) in B] " +
				"( exists[b in B][b != not(c)] not(b) in C or " +
				"exist[D in S] D is_a_subset_of reselvent_of(C,B) ).");
		System.out.println("One can read more on WnD clause sets here:");
		System.out.println("  http://aries.ektf.hu/~gkusper/Thesis.pdf");
		System.out.println("  http://fmv.ektf.hu/");
		System.out.println("Usage:");
		System.out.println("java WnDGen2 switches N  K");
		System.out.println("  Generates a WnD clause set with N variables.");
		System.out.println("  Each clause has K literals.");
		System.out.println("  If K is not given, then each clause has 3 literals.");
		System.out.println("  K must be greater or equal than 2 or can be empty.");
		System.out.println("  N must be greater or equal than K.");
		System.out.println("  The generator does not neccessary generate the same output " +
				"for the same parameters, because of the random switch.");
		System.out.println("Switches:");
		System.out.println("  -sat: Generates a satisfiable clause set. It is the default switch.");
		System.out.println("  -unsat: Tries to generate an unsatisfiable clause set, " +
				"which is wnd upto the two last clauses. " +
				"The two extra clauses are the generator clause and its negation. " +
				"It can generate only unsatisfiable clause sets if N>=2K-3. " +
				"Do not use it together with -wndgen1.");
		System.out.println("  -difficult: The same as -unsat, but it adds only one extra clause, " +
				"the generator clause. " +
				"It always generates satisfiable clause sets.");
		System.out.println();
		System.out.println("  -randomgc: The generator clause is a random clause with N literals. " +
				"It is the default switch.");
		System.out.println("  -positivegc: The generator clause contains only positive literals. " +
				"The generator clause has N literals.");
		System.out.println("  -negativegc: The generator clause contains only negative literals. " +
				"The generator clause has N literals.");
		System.out.println();
		System.out.println("  -wndgen: It uses the WnDGen algorithm as described in: " +
				"How to Generate Weakly Nondecisive SAT Instances. " +
				"It is the default switch.");
		System.out.println("  -wndgen1: It uses the WnDGen1 algorithm as described in: " +
				"How to Generate Weakly Nondecisive SAT Instances. ");
		System.out.println();
		System.out.println("  -help: Prints this page.");
	}

	public static void Help()
	{
		System.out.println("-------------------------------------------");
		System.out.println("Switch: -help: Prints how to use WnDGen.");	
		System.out.println("Java WnDGen -help");
		System.out.println("------------------------------------------");
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length == 0) { printUsage(); Help(); return; }
		int numberOfVariables = 5;
		int lengthOfClauseses = 3;

		boolean generateSatisfiableClauseSet = true;
		boolean generateDifficultClauseSet = false;
		boolean randomGeneratorClause = true;
		boolean positiveGeneratorClause = false;
		boolean useWnDGen = true;
		try
		{
			int i = 0;
			while(i<args.length && args[i].startsWith("-"))
			{
System.out.println("while starts");
				if (args[i].equals("-help")) { printUsage(); return; }
				else if (args[i].equals("-unsat")) generateSatisfiableClauseSet = false;
				else if (args[i].equals("-difficult")) generateDifficultClauseSet = true;
				else if (args[i].equals("-positivegc") || args[0].equals("-negativegc"))
				{
					randomGeneratorClause = false;
					if (args[i].equals("-positivegc")) positiveGeneratorClause = true;
					else positiveGeneratorClause = false;
				}
				else if (args[i].equals("-wndgen1")) useWnDGen = false;
				else if ( 	!(args[i].equals("-sat")) ||
							!(args[i].equals("-randomgc")) ||
							!(args[i].equals("-wndgen"))	) throw new Exception();
System.out.println();
System.out.println("end of while.");
				i++;
System.out.println("end of while..");
			}
System.out.println();
System.out.println("end of while...");
			if (args.length > i) numberOfVariables = Integer.parseInt(args[i]);
			i++;
			if (args.length > i) lengthOfClauseses = Integer.parseInt(args[i]);
			if (lengthOfClauseses <= 1) throw new Exception();
			if (numberOfVariables < lengthOfClauseses) throw new Exception();
		}
		catch(Exception e) 
		{
			System.err.println("Wrong arguments!");
			Help(); 
			return;
		}
		int[] clause = generateClauseWithLength(
							numberOfVariables,
							randomGeneratorClause,
							positiveGeneratorClause);
		WnDGen2 gen = new WnDGen2(
							numberOfVariables, 
							lengthOfClauseses, 
							generateSatisfiableClauseSet, 
							generateDifficultClauseSet,
							useWnDGen);
		gen.generateAndPrintBy(clause);
		
	}

	public static int[] generateClauseWithLength(
							int numberOfVariables,
							boolean randomGeneratorClause,
							boolean positiveGeneratorClause)
	{
		int[] clause = new int[numberOfVariables];
		if (randomGeneratorClause)
		{
			Random rnd = new Random();
			for(int i=0; i<numberOfVariables; i++)
			{
				int lit = i + 1;
				clause[i] = rnd.nextBoolean() ? lit : -lit; 
			}
			return clause;
		}
		else
		{
			for(int i=0; i<numberOfVariables; i++)
			{
				int lit = i + 1;
				clause[i] = positiveGeneratorClause ? lit : -lit; 
			}
			return clause;
		}	
	}
	
	int numberOfVariables;
	int lengthOfClauseses;
	boolean generateSatisfiableClauseSet;
	boolean generateDifficultClauseSet;
	boolean useWnDGen;
 
	public WnDGen2(int numberOfVariables, 
			int lengthOfClauseses, 
			boolean generateSatisfiableClauseSet,
			boolean generateDifficultClauseSet,
			boolean useWnDGen)
	{
		this.numberOfVariables = numberOfVariables;
		this.lengthOfClauseses = lengthOfClauseses;
		this.generateSatisfiableClauseSet = generateSatisfiableClauseSet;
		this.generateDifficultClauseSet = generateDifficultClauseSet;
		this.useWnDGen = useWnDGen;
	}

	public void generateAndPrintBy(int[] clause) throws IOException

	{
			generateAndPrintBy_K(clause);
	}

	public void generateAndPrintBy_K(int[] clause) throws IOException

	{
		String delimiter = "  ";
		String zero = "0\n";
		StringBuilder gen = new StringBuilder();
		int N = clause.length;
		int K = lengthOfClauseses;	
		BufferedWriter out = null;
		out = new BufferedWriter( new FileWriter(generateFileName(N)));			
		generateDimacsHeader(gen, N);
		int[] indexVektor = new int[K];
		for(int i=0; i<K; i++) { indexVektor[i] = i; }
		while(true)
		{
			for(; indexVektor[K-1]<N; indexVektor[K-1]++)
			{
		    	int[][] ujClausesPoz = new int[K][K];
				for(int i=0; i<K; i++)
				{
					for(int j=0; j<K; j++)
					{
						ujClausesPoz[i][j] = clause[indexVektor[j]];
						if (i==j) ujClausesPoz[i][j] = -clause[indexVektor[j]];
						gen.append(ujClausesPoz[i][j]);
						gen.append(delimiter);
					}
					gen.append(zero);
				}
				if (useWnDGen)
				{
					int[][] ujClausesNeg = new int[K][K];
					for(int i=0; i<K; i++)
					{
						for(int j=0; j<K; j++)
						{
							ujClausesNeg[i][j] = -clause[indexVektor[j]];
							if (i==j) ujClausesNeg[i][j] = clause[indexVektor[j]];
							gen.append(ujClausesNeg[i][j]);
							gen.append(delimiter);
						}
						gen.append(zero);
					}
				}
			} 
			if (gen.length() >= 1000000)
			{
				try
				{
					String outText = gen.toString();
					out.append(outText);
				}
				catch (IOException e)
				{		
				    e.printStackTrace();
				}
				gen = new StringBuilder();	    
			}	
			int vissza = 1;
			while(K-vissza >= 0 && indexVektor[K-vissza] == N-vissza+1) vissza++;
			if (K-vissza == -1) break;
			int újIndex = indexVektor[K-vissza]+1;
			for(int i=K-vissza; i<K; i++) { indexVektor[i] = újIndex; újIndex++; }
		}
		if(!generateSatisfiableClauseSet)
		{
			for(int i=0; i<clause.length; i++)
			{
				gen.append(clause[i]);
				gen.append(delimiter);
			}
			gen.append(zero);
			for(int i=0; i<clause.length; i++)
			{
				gen.append(-clause[i]);
				gen.append(delimiter);
			}
			gen.append(zero);
		}
		else if (generateDifficultClauseSet)
		{
			for(int i=0; i<clause.length; i++)
			{
				gen.append(clause[i]);
				gen.append(delimiter);
			}
			gen.append(zero);
		}
		try {
			 String outText = gen.toString();
		     out.append(outText);
			 out.flush();
		     out.close();
		    }
		catch (IOException e)
		{		
		    e.printStackTrace();
		}	
	}
		
  public String generateFileName(int n)
  {
	  String sat;
	  if (generateSatisfiableClauseSet) 
		  sat="sat"; 
	  else if (generateDifficultClauseSet) 
		  sat="difficult";
	  else sat="unsat";
	  String filename="WndGen_"+sat+"_"+n+"_"+lengthOfClauseses+".cnf"; 
	  return filename;
  }

	private void generateDimacsHeader(StringBuilder gen, int n) {
		gen.append("c This formular is generated by the call:\n");
		gen.append("c java WnDGen ");
		if(generateSatisfiableClauseSet) gen.append("-sat ");
		else if (generateDifficultClauseSet) gen.append("-difficult ");
		else gen.append("-unsat ");
		gen.append(n);
		gen.append(" ");
		gen.append(lengthOfClauseses);
		gen.append("\n");
		gen.append("c \n");
		gen.append("c See: http://fmv.ektf.hu/\n");
		gen.append("c \n");
		gen.append("p cnf ");
		gen.append(n);
		gen.append(" ");
		double n_over_m = 1.0;
		for(int i=0; i<lengthOfClauseses; i++)
		{
			n_over_m *= n-i;
			n_over_m /= lengthOfClauseses-i;
		}
		n_over_m *= 2 * lengthOfClauseses;
		if (!useWnDGen) n_over_m /= 2; 
		if (!generateSatisfiableClauseSet) n_over_m += 2;
		else if (generateDifficultClauseSet) n_over_m += 1;
		gen.append((int)n_over_m);
		gen.append("\n");
		}
  }