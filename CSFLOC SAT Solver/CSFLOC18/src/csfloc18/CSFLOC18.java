import java.util.*;
import java.io.*;


public class CSFLOC18 {
	private static String fileName = "";
	static String variableRenamingStrategy = ""; // may contain the letters: B, C, H, I, R, S, W, or the same lower case letters
		// B: do renameBlackClause
		// C: do clustering
		// H: do renameDefiniteHornClauses
		// I: do renameIslandClauses
		// R: do renameVariables by their frequency
		// S: do renameStraitClauses
		// W: do renameWhiteClauses
		// list of some useful strategies: 
			// in case of random 3-SAT problems: "IWCR" or "HWCR" or "BHWCR"
			// in case of pigeon-holes problems: "B"
	static int learn3ClausesPerLevel = 1;	// 0 = do not learn clauses, 
											// 1 = learn max 3 clauses at each index level
	static int addLongTailClauses = 0;	// 0 = do not add long tail clauses, 
										// 1 = add long tail clauses, 
										// 2 = use learnedClauses to store long tail clauses
	static int clusteringFactor = 5; // it must be at least 2, 
									 // works only if the variable renaming strategy contains "C" for clustering
	static int clauseListWithSorting = 0;	// 0 = do not use sorting while creating a clause list
											// 1 = use sorting while creating a clause list, sort by the first literal
											// it is easy to add a new kind of sorting, just create a new subclass of ClauseList
    static int BCP = 0;				 // does not work, must be 0, 0 = do not do BCP, 1 = do BCP, it works, but only if the input is UNSAT, otherwise do not use this option yet
    public static int learn3ClausesForClauses = 0;	// it works, but it is a beta feature, so do not use yet, so let it be 0
    												// 0 = do not learn clauses for each clause, 
    												// 1 = learn max 3 clauses for each clause
    public static int useExtendedResolution = 0;  // number of new variables added by extended resolution
    
    
	public static void main(String[] args) {
		try {
			if (args.length >= 1) { 
				fileName = args[0];
				if (fileName.equals("-help") || 
					fileName.equals("help") || 
					fileName.equals("-?") || 
					fileName.equals("?")) {
					printHelp();
					return;
				}
			}
			if (args.length >= 2) { 
				variableRenamingStrategy = args[1];
				if (args[1].toUpperCase().equals("NON")) variableRenamingStrategy = "";
			}
			if (args.length >= 3) { learn3ClausesPerLevel = Integer.parseInt(args[2]); }
			if (args.length >= 4) { addLongTailClauses = Integer.parseInt(args[3]); }
			if (args.length >= 5) { clusteringFactor = Integer.parseInt(args[4]); }
			if (args.length >= 6) { clauseListWithSorting = Integer.parseInt(args[5]); }
			if (args.length >= 7) {
				System.out.println("Too many parameters! Please read the help:");
				printHelp();
				return;
			}
		}
		catch(Exception e) {
			System.out.println("Argument error! Please read the help:");
			printHelp();
			return;
		}
		System.out.println("CSFLOC18 is now running! Please wait for the result!");
		long startTime = System.currentTimeMillis();
		DIMACSReader dcs = new DIMACSReader(fileName);
		//System.out.println("DIMACSReader is done");
		if (useExtendedResolution > 0) dcs.addExtendedResolutionClauses(useExtendedResolution);
		HighLevelReader reader = new HighLevelReader(dcs, variableRenamingStrategy);
		//System.out.println("HighLevelReader is done");
		ClauseSet S = reader.getClauseSet();
		//System.out.println("Clause Set is done");
		int[] translate = reader.translate;
		ArrayList<int[]> cs = reader.getClauseSetAsArrayListOfIntArray();
		//System.out.println("CS is created");
		CSFLOCSolver solver = new CSFLOCSolver(S);
		//System.out.println("Solver is created");
		boolean[] solution = solver.CSFLOC_v7();
		//boolean[] solution = solver.CSFLOCBomber_v1();
		long endTime = System.currentTimeMillis();

		if (solution != null) {
			System.out.println("A solution is:");
			for(int i=1; i<S.numberOfVariables+1; i++) {
				int var = i;
				if (translate != null) var = translate[i];
				if (solution[var]) System.out.print(i + " ");
				else System.out.print(-i + " ");
			}
			System.out.println();
			System.out.println("Because the following full length clause is not subsumed:");
			for(int i=1; i<S.numberOfVariables+1; i++) {
				int var = i;
				if (translate != null) var = translate[i];
				if (solution[var]) System.out.print(-i + " ");
				else System.out.print(i + " ");
			}
			System.out.println("0");
			if (!CheckSolution.SimpleCheckSolution(cs, solution)) {
				System.out.println("The solution is checked to be sure, but it is NOT A SOLUTION! There must be a bug:(");
			}
			//else {System.out.println("The solution is checked to be sure, and it is INDEED A SOLUTION!");}
		}
		else {
			System.out.println("There is no solution!");
		}
		System.out.println("CPU time is " + (endTime - startTime) / 1000.0 + " s");
		solver.printStatistics();
		printParameters();
	}
	
	private static void printHelp() {
		System.out.println("Usage: java CSFLOC18 switches cnf_file_name variable_renaming_strategy use_learned_clauses add_long_tail_clauses clustering_factor clause_list_ordering");
		System.out.println("Example: java CSFLOC18 hole7.cnf BC 1 1 5 1");
		System.out.println("The parameter 'swhitches' is optional. In this version we support only these ones:");
		System.out.println("-help, help, -?, ?: All of them prints this help and do nothing else.");
		System.out.println("The parameter 'cnf_file_name' is the name of a file in DIMACS format containing a CNF SAT problem.");
		System.out.println("The parameter 'variable_renaming_strategy' may contain the letters in any order: B, C, H, I, R, S, W, or the same lower case letters;");
		System.out.println("or it is 'NON' which means, that there is no strategy.");
		System.out.println("If it is not set, then no strategy is used.");
		System.out.println("Letter 'B' means, that black clauses (all literals are negative) will contain low variable indices.");
		System.out.println("Letter 'C' means, that it clusters variables, i.e., it moves those variables closer, which occurs frequently together in the clauses.");
		System.out.println("Letter 'H' means, that definite horn clauses (exactly one positive literal) will contain low variable indices. It renames the literals of a definite horn clause only if non of them are renamed yet.");
		System.out.println("Letter 'I' means, that definite horn clauses will contain low variable indices. The positive literal will get the biggest index inside the definite horn clause.");
		System.out.println("Letter 'R' means, that it renames variables by their frequency, the most frequent variable will get the least index.");
		System.out.println("Letter 'S' means, that 'strait' clauses (exactly one negative literal) will contain low variable indices. The negative literal will get the least index inside the strait clause.");
		System.out.println("Letter 'W' means, that white clauses (all literals are positive) will contain low variable indices.");
		System.out.println("List of some useful strategies:"); 
		System.out.println("In case of random 3-SAT problems: 'IWCR', 'HWCR', or 'BHWCR'.");
		System.out.println("In case of pigeon-holes problems: 'B'.");
		System.out.println("In case of Black-and-White SAT problems generated by the Balatonboglár model: 'I 1 2 0 0'.");
		System.out.println("The parameter 'use_learned_clauses' may be 0 or 1.");
		System.out.println("If it is not set, then it is 1.");
		System.out.println("Number '0' means, that it does not use learned clauses to speed up the search.");
		System.out.println("Number '1' means, that it generates for each variable upto 2*3 learned clauses, and it uses them to speed up the search.");
		System.out.println("The parameter 'add_long_tail_clauses' may be 0, 1, or 2.");
		System.out.println("If it is not set, then it is 0.");
		System.out.println("Number '0' means, that it does not generate resolvents in the preprocessing steps.");
		System.out.println("Number '1' means, that it generates resolvents, which contains big index variables, and it adds them to the initial clause set.");
		System.out.println("Number '2' means, that it generates resolvents, which contains big index variables, and it adds them to the learned clauses.");
		System.out.println("The parameter 'clustering_factor' may be 2 or a bigger number.");
		System.out.println("If it is not set, then it is 5.");
		System.out.println("Clustering computes the variable pair frequency.");
		System.out.println("It groups the most frequent pairs in the first cluster until it becomes full, and so on.");
		System.out.println("It works only if the variable_renaming_strategy contains 'C'.");
		System.out.println("Usually 5 is the best option.");
		System.out.println("The parameter 'clause_list_ordering' may be 0 or 1.");
		System.out.println("If it is not set, then it is 0.");
		System.out.println("Number '0' means, that it does not sort clauses inside a clause list.");
		System.out.println("Number '1' means, that it sorts the clauses inside each clause list by the index of their first literal. For example {-1, 5, 10} precedes {2, 4, -8}.");
		System.out.println("Sorting almost always results in less 'numberOfRunsOfTheMainLoop', but sorting needs O(numberOfClauses*numberOfClauses/numberOfVariables) time, so if you have lots of clauses, you might consider to switch it off.");
	}
	private static void printParameters() {
		System.out.println("The parameter file_name was: " + fileName);
		System.out.println("The parameter variable_renaming_strategy was: '" + variableRenamingStrategy + "'");
		System.out.println("The parameter use_learned_clauses was: " + learn3ClausesPerLevel);
		System.out.println("The parameter add_long_tail_clauses was: " + addLongTailClauses);
		System.out.println("The parameter clustering_factor was: " + clusteringFactor);
		System.out.println("The parameter clause_list_ordering was: " + clauseListWithSorting);
	}
}
class CSFLOCSolver {
	static long numberOfRunsOfTheMainLoop = 1;
	static long numberOfUsedLearnedClauses = 0;
	static long islandsLengthSum = 0;
	static long islandsCount = 0;
	static long islandsMaxLength = 0;
	static long lastSeaLengthSum = 0;
	static long lastSeaCount = 0;
	static long lastSeaMaxLength = 0;
	static long jumpLengthSum = 0;
	static long jumpCount = 0;
	static long jumpMaxLength = 0;
	static long numberOfCasesWithoutLearnedClauses = 0;
	
	ClauseSet S;
	int numberOfVariables;
	Clause[][] effectedClauses, oldEffectedClauses;
	Clause[][] learnedClausesNeg;
	Clause[][] learnedClausesPos;
	
	public CSFLOCSolver(ClauseSet S) {
		this.S = S;
		numberOfVariables = S.numberOfVariables;
		setupEffectedClauses();
		setupLearnedClauses();
		if (CSFLOC18.addLongTailClauses == 2) { populateLearnedClauses(); }
	}
	private void setupEffectedClauses() {
		effectedClauses = new Clause[numberOfVariables+1][2];
		oldEffectedClauses = new Clause[numberOfVariables+1][2];
	}
	private void setupLearnedClauses() {
		learnedClausesNeg = new Clause[numberOfVariables+1][3];
		learnedClausesPos = new Clause[numberOfVariables+1][3];
	}
	public void addEffectedClause(int index, Clause c) {
		//System.out.println("addEffectedClause, index: " + index);
		//System.out.println("addEffectedClause, c: " + c);
		c.isEffected = true;
		if (effectedClauses[index][0] == null)
			effectedClauses[index][0] = c;
		else 
			effectedClauses[index][1] = c;
	}
	public void clearEffectedClauses(int index) {
		oldEffectedClauses[index][0] = effectedClauses[index][0];
		oldEffectedClauses[index][1] = effectedClauses[index][1];
		effectedClauses[index][0] = null;
		effectedClauses[index][1] = null;
	}
	public void addLearnedClause_v4(Clause c) {
		if (CSFLOC18.learn3ClausesPerLevel != 1) return;
		int index = c.lastVarIndex;
		//System.out.println("index: " + index + ", c: " + c);
		if (c.literals.length == index) { return; } // ez lehet, hogy lassit
		if (c.lastLiteral > 0) {
			learnedClausesPos[index][2] = learnedClausesPos[index][1];
			learnedClausesPos[index][1] = learnedClausesPos[index][0];
			learnedClausesPos[index][0] = c;
		}
		else {
			learnedClausesNeg[index][2] = learnedClausesNeg[index][1];
			learnedClausesNeg[index][1] = learnedClausesNeg[index][0];
			learnedClausesNeg[index][0] = c;
		}
	}
	public void populateLearnedClauses() {
		for(int i=numberOfVariables; i>0; i--) {
			if (S.clauseListOrderdByLastVarIndexNeg[i].length == 0) continue;
			Clause b = S.clauseListOrderdByLastVarIndexNeg[i][0];
			/* experimental code, do not use yet:
			int lastButOneB = b.vars.length > 1 ? b.vars[b.vars.length-2] : 0;
			boolean lastIsPosB = b.isPos.length > 1 ? b.isPos[b.isPos.length-2] : true;
			*/
			for(int j=0; j<S.clauseListOrderdByLastVarIndexPos[i].length; j++) {
				Clause a = S.clauseListOrderdByLastVarIndexPos[i][j];
				/* experimental code, do not use yet:
				int lastButOneA = a.vars.length > 1 ? a.vars[a.vars.length-2] : 0;
				boolean lastIsPosA = a.isPos.length > 1 ? a.isPos[a.isPos.length-2] : true;
				int lastButOne = lastButOneA > lastButOneB ? lastButOneA : lastButOneB;
				boolean lastIsPos = lastButOneA > lastButOneB ? lastIsPosA : lastIsPosB;
				if (lastIsPos && learnedClausesPos[lastButOne][2] != null) continue;  
				else if (learnedClausesNeg[lastButOne][2] != null) continue;
				*/
				Clause c = Clause.resolution_v3(a,b,a.lastVarIndex);
				if (c!=null) {
					//System.out.println("c: " + c);
					if (c.lastLiteral > 0 && learnedClausesPos[c.lastLiteral][2] == null) addLearnedClause_v4(c);  
					else if (learnedClausesNeg[c.lastVarIndex][2] == null) addLearnedClause_v4(c);
				}
			}
		}
		for(int i=numberOfVariables; i>0; i--) {
			if (S.clauseListOrderdByLastVarIndexPos[i].length == 0) continue;
			Clause a = S.clauseListOrderdByLastVarIndexPos[i][0];
			for(int k=1; k<S.clauseListOrderdByLastVarIndexNeg[i].length; k++) {
				Clause b = S.clauseListOrderdByLastVarIndexNeg[i][k];
				Clause c = Clause.resolution_v3(a,b,a.lastVarIndex);
				if (c!=null) {
					//System.out.println("c: " + c);
					if (c.lastLiteral > 0 && learnedClausesPos[c.lastLiteral][2] == null) addLearnedClause_v4(c);  
					else if (learnedClausesNeg[c.lastVarIndex][2] == null) addLearnedClause_v4(c);
				}
			}
		}
	}
	public boolean[] CSFLOC_v7() {
		//System.out.println("Solver is started!");
		boolean[] counter;
		counter = new boolean[numberOfVariables+1];
		int index = S.indexOfLastVariableOfBestBlackClause;
		//System.out.println("S.numberOfVariables: " + S.numberOfVariables);
		//System.out.println("index: " + index);
		if (index == S.numberOfVariables+1) {
			for(int i=0; i<counter.length; i++) { counter[i] = !counter[i]; }
			return counter;
		}
		addEffectedClause(index, S.bestBlackClause);
		counter[index] = true;
		//System.out.println("S.bestBlackClause: " + S.bestBlackClause);
		//System.out.println("index: " + index);
		//System.out.print("counter: "); printCounter(counter);
		int oldIndex = index;
		int lengthOfTheLastIsland = 1;
		int lengthOfTheLastSea = index;
		int lengthOfTheLastJump = 0;
		while(index > 0) {
			//System.out.print("counter1: "); printCounter(counter);
			index = usingBestClause_v4(index, counter);
			//System.out.println("index: " + index);
			//System.out.print("counter2: "); printCounter(counter);
			if (index == 0) {
				for(int i=0; i<counter.length; i++) { counter[i] = !counter[i]; }
				return counter;
			}
			//System.out.print("counter in main loop: "); printCounter(counter);
			//System.out.println("index: " + index);
			//System.out.println("oldIndex: " + oldIndex + ",  index: " + index);
			index = increaseCounter_v5(index, true, counter);
			//System.out.print("counter in main loop: "); printCounter(counter);
			//System.out.println("oldIndex: " + oldIndex + ",  index: " + index);
			//try { Thread.sleep(1000); } catch(Exception e) {}
			if (index - oldIndex == 1) {
				lengthOfTheLastIsland++;
				lengthOfTheLastSea = 0;
			}
			else {
				islandsLengthSum += lengthOfTheLastIsland;
				islandsCount++;
				lengthOfTheLastIsland = 1;
				lengthOfTheLastSea = index - oldIndex + 1;
			}
			if (lengthOfTheLastSea < 0) {
				lengthOfTheLastJump = oldIndex - index;
				jumpLengthSum += lengthOfTheLastJump;
				jumpCount++;
				lengthOfTheLastSea = 0; // undef
			}
			else {
				lastSeaLengthSum += lengthOfTheLastSea;
				lastSeaCount++;
			}
			if (lengthOfTheLastIsland > islandsMaxLength) {
				islandsMaxLength = lengthOfTheLastIsland;
				//System.out.println("islandsMaxLength: " + islandsMaxLength);
			}
			if (lengthOfTheLastSea > lastSeaMaxLength) {
				lastSeaMaxLength = lengthOfTheLastSea;
				//System.out.println("seaMaxLength: " + seaMaxLength);
			}
			if (lengthOfTheLastJump > jumpMaxLength) {
				jumpMaxLength = lengthOfTheLastJump;
				//System.out.println("jumpMaxLength: " + jumpMaxLength);
			}
			oldIndex = index;
			numberOfRunsOfTheMainLoop++;
		}
		return null;
	}
	public boolean[] CSFLOC_v8() {
		boolean[] counter;
		boolean[] counterX;
		counter = new boolean[numberOfVariables+1];
		counterX = new boolean[numberOfVariables+1];
		//System.out.println("S.bestBlackClause: " + S.bestBlackClause);
		int index = S.indexOfLastVariableOfBestBlackClause;
		S.bestBlackClause.setCounterX(counterX);
		//System.out.println("S.numberOfVariables: " + S.numberOfVariables);
		System.out.println("index: " + index);
		System.out.print("counterX: "); printCounterX(counter, counterX);
		if (index == S.numberOfVariables+1) {
			for(int i=0; i<counter.length; i++) { counter[i] = !counter[i]; }
			return counter;
		}
		addEffectedClause(index, S.bestBlackClause);
		counter[index] = true;
		System.out.println("index: " + index);
		System.out.print("counterX: "); printCounterX(counter, counterX);
		int oldIndex = index;
		int lengthOfTheLastIsland = 1;
		int lengthOfTheLastSea = index;
		int lengthOfTheLastJump = 0;
		while(index > 0) {
			index = usingBestClause_v5(index, counter, counterX);
			System.out.println("index: " + index);
			System.out.print("counterX: "); printCounterX(counter, counterX);
			if (index == 0) {
				for(int i=0; i<counter.length; i++) { counter[i] = !counter[i]; }
				return counter;
			}
			index = increaseCounter_v6(index, true, counter, counterX);
			System.out.println("index: " + index);
			System.out.print("counterX: "); printCounterX(counter, counterX);
			//try { Thread.sleep(1000); } catch(Exception e) {}
			if (index - oldIndex == 1) {
				lengthOfTheLastIsland++;
				lengthOfTheLastSea = 0;
			}
			else {
				islandsLengthSum += lengthOfTheLastIsland;
				islandsCount++;
				lengthOfTheLastIsland = 1;
				lengthOfTheLastSea = index - oldIndex + 1;
			}
			if (lengthOfTheLastSea < 0) {
				lengthOfTheLastJump = oldIndex - index;
				jumpLengthSum += lengthOfTheLastJump;
				jumpCount++;
				lengthOfTheLastSea = 0; // undef
			}
			else {
				lastSeaLengthSum += lengthOfTheLastSea;
				lastSeaCount++;
			}
			if (lengthOfTheLastIsland > islandsMaxLength) {
				islandsMaxLength = lengthOfTheLastIsland;
				//System.out.println("islandsMaxLength: " + islandsMaxLength);
			}
			if (lengthOfTheLastSea > lastSeaMaxLength) {
				lastSeaMaxLength = lengthOfTheLastSea;
				//System.out.println("seaMaxLength: " + seaMaxLength);
			}
			if (lengthOfTheLastJump > jumpMaxLength) {
				jumpMaxLength = lengthOfTheLastJump;
				//System.out.println("jumpMaxLength: " + jumpMaxLength);
			}
			oldIndex = index;
			numberOfRunsOfTheMainLoop++;
		}
		return null;
	}
	private int usingBestClause_v4(int index, boolean[] counter) {
		// we try to cancel the last island, i.e.,
		// we try to find a clause which subsumes the counter and
		// its last literal is "+index"
		//System.out.println("index: " + index);
		Clause a = effectedClauses[index][1];
		if (a != null && a.subsumedBy(counter)) {
			//System.out.println("a: " + a);
			return index;
		}
		a = oldEffectedClauses[index][1];
		if (a != null && a.subsumedBy(counter)) { 
			addEffectedClause(index, a);
			//System.out.println("a: " + a);
			return index; 
		}
		Clause[] clauseList =  S.clauseListOrderdByLastVarIndexPos[index];
		for(Clause c : clauseList) { 
			if (c.subsumedBy(counter)) { 
				addEffectedClause(index, c);
				//System.out.println("c: " + c);
				return index; 
			}
		}
		for(int i=0; i<learnedClausesPos[index].length; i++) {
			Clause c = learnedClausesPos[index][i];
			if (c == null) {
				if (i == 0) numberOfCasesWithoutLearnedClauses++;
				break;
			}
			if (c.subsumedBy(counter)) { 
				numberOfUsedLearnedClauses++; 
				addEffectedClause(index, c);
				//System.out.println("c: " + c);
				return index;
			}
		}
		// we could not cancel the last island, so we have to build a new last one, i.e.,
		// we try to find a clause which subsumes the counter and
		// its last literal is "-index"
		for(index++ ; index<S.numberOfVariables+1; index++) {
			Clause b = effectedClauses[index][0];
			if (b != null && b.subsumedBy(counter)) {
				//System.out.println("b: " + b);
				return index; 
			}
			b = oldEffectedClauses[index][0];
			if (b != null && b.subsumedBy(counter)) { 
				addEffectedClause(index, b); 
				//System.out.println("b: " + b);
				return index; 
			}
			clauseList =  S.clauseListOrderdByLastVarIndexNeg[index];
			for(Clause c : clauseList) { 
				if (c.subsumedBy(counter)) { 
					addEffectedClause(index, c);
					//System.out.println("c: " + c);
					return index; } 
				}
			for(int i=0; i<learnedClausesNeg[index].length; i++) {
				Clause c = learnedClausesNeg[index][i];
				if (c == null) {
					if (i == 0) numberOfCasesWithoutLearnedClauses++;
					break;
				}
				if (c.subsumedBy(counter)) { 
					numberOfUsedLearnedClauses++;
					//System.out.println("c: " + c);
					return useSubsumedClause(index, c, counter); 
				}
			}
			/* experimenting code, please, delete it
			Clause toBeCeckedLater = new Clause(counter,index);
			System.out.println("toBeCeckedLater: " + toBeCeckedLater);
			addEffectedClause(index, toBeCeckedLater); 
			return index;
			*/
		}
		return 0;
	}
	private int useSubsumedClause(int index, Clause c, boolean[] counter) {
		if (CSFLOC18.learn3ClausesForClauses == 0) {
			addEffectedClause(index, c); 
			return index;
		}
		Clause[] learnedClauses = c.learnedClauses;
		for(int i=0; i<learnedClauses.length; i++)
		{
			Clause learnedClause = learnedClauses[i];
			if (learnedClause != null && learnedClause.subsumedByNonLazy(counter)  ) {
				//System.out.println("counter: " + counter);
				//System.out.println("c: " + c);
				//System.out.println("learnedClause: " + learnedClause);
				//System.out.println("S.effectedClauses[learnedClause.lastVarIndex][0]: " + S.effectedClauses[learnedClause.lastVarIndex][0]); 				
				//System.out.println("S.effectedClauses[learnedClause.lastVarIndex][1]: " + S.effectedClauses[learnedClause.lastVarIndex][1]);
				//System.out.println("index régi: " + index);
				while(index>learnedClause.lastVarIndex) 
				{
					counter[index] = false;
					clearEffectedClauses(index);
					index--;
				}
				numberOfUsedLearnedClauses++;
				addEffectedClause(index, learnedClause);
				//System.out.println("index új  : " + index);
				return index;
			}
		}
		addEffectedClause(index, c); 
		return index;
	}
	
	
	private int usingBestClause_v5(int index, boolean[] counter, boolean[] counterX) {
		// we try to cancel the last island, i.e.,
		// we try to find a clause which subsumes the counter and
		// its last literal is "+index"
		//System.out.println("index: " + index);
		ArrayList<Clause> bestOnes = new ArrayList<>();
		Clause a = effectedClauses[index][1];
		if (a != null && a.subsumedBy(counter)) { bestOnes.add(a); }
		a = oldEffectedClauses[index][1];
		if (a != null && a.subsumedBy(counter)) { bestOnes.add(a); }
		Clause[] clauseList =  S.clauseListOrderdByLastVarIndexPos[index];
		for(Clause c : clauseList) { if (c.subsumedBy(counter)) { bestOnes.add(c); }}
		for(int i=0; i<learnedClausesPos[index].length; i++) {
			Clause c = learnedClausesPos[index][i];
			if (c == null) {
				if (i == 0) numberOfCasesWithoutLearnedClauses++;
				break;
			}
			if (c.subsumedBy(counter)) { numberOfUsedLearnedClauses++; bestOnes.add(c); }
		}
		if (bestOnes.size() > 0) {
			Clause best = selectTheBestSubsumer(bestOnes, index, counter, counterX);
			addEffectedClause(index, best); 
			return index;
		}
		// we could not cancel the last island, so we have to build a new last one, i.e.,
		// we try to find a clause which subsumes the counter and
		// its last literal is "-index"
		for(index++ ; index<S.numberOfVariables+1; index++) {
			Clause b = effectedClauses[index][0];
			if (b != null && b.subsumedBy(counter)) { bestOnes.add(b); }
			b = oldEffectedClauses[index][0];
			if (b != null && b.subsumedBy(counter)) { bestOnes.add(b); }
			clauseList =  S.clauseListOrderdByLastVarIndexNeg[index];
			for(Clause c : clauseList) { if (c.subsumedBy(counter)) { bestOnes.add(c); } }
			for(int i=0; i<learnedClausesNeg[index].length; i++) {
				Clause c = learnedClausesNeg[index][i];
				if (c == null) {
					if (i == 0) numberOfCasesWithoutLearnedClauses++;
					break;
				}
				if (c.subsumedBy(counter)) { 
					numberOfUsedLearnedClauses++; 
					bestOnes.add(c);
				}
			}
			if (bestOnes.size() > 0) {
				Clause best = selectTheBestSubsumer(bestOnes, index, counter, counterX);
				addEffectedClause(index, best); 
				return index;
			}
		}
		return 0;
	}
	
	private Clause selectTheBestSubsumer(ArrayList<Clause> bestOnes, int index, boolean[] counter, boolean[] counterX) {
		//System.out.println("index: " + index);
		//System.out.print("counter:  "); printCounter(counter);
		//System.out.print("counterX: "); printCounter(counterX);
		
		if (bestOnes.size() == 1) {
			Clause selectedOne = bestOnes.get(0);
			System.out.println("selectedOne: " + selectedOne);
			selectedOne.setCounterX(counterX);
			return selectedOne;
		}
		int bestLastVar = scoreThisClause(bestOnes.get(0), counterX);
		int bestClauseIndex = 0;
		for(int i=1; i<bestOnes.size(); i++) {
			int actLastVar = scoreThisClause(bestOnes.get(i), counterX);
			if (actLastVar < bestLastVar) {
				bestLastVar = actLastVar;
				bestClauseIndex = i;
			}
		}
		Clause selectedOne = bestOnes.get(bestClauseIndex);
		if (bestClauseIndex != 0) {
			if (bestOnes.size() > 1) {
				//System.out.print("counter1: "); printCounter(counter);
				//System.out.print("counter2: "); printCounter(counterX);
				//System.out.print("counterX: "); printCounterX(counter, counterX);
				//for(int i=0; i<bestOnes.size(); i++) System.out.println(bestOnes.get(i));
			}
			//System.out.println("selectedOne: " + selectedOne);
		}
		selectedOne.setCounterX(counterX);
		System.out.println("selectedOne: " + selectedOne);
		return selectedOne;
	}
	private void printCounter(boolean[] counter) {
		for(int i=1; i<counter.length; i++) {
			System.out.print(counter[i] ? i : -i);
			System.out.print(" ");
		}
		System.out.println();
	}
	private void printCounterX(boolean[] counter, boolean[] counterX) {
		for(int i=1; i<counter.length; i++) {
			System.out.print(counterX[i] ? (counter[i] ? i : -i) : "X");
			System.out.print(" ");
		}
		System.out.println();
	}
	private int scoreThisClause(Clause c, boolean[] counterX) {
		//System.out.println("c: " + c);
		for(int i=c.literals.length-2; i>=0; i--) {
			//System.out.println("i: " + i);
			//System.out.println("c.vars[i]: " + c.vars[i]);
			if (!counterX[c.vars[i]]) return c.vars[i];
		}
		//System.out.println("return: " + 0);
		return 0;
	}
	
	
	private int increaseCounter_v5(int index, boolean first, boolean[] counter) {
		//System.out.println("increaseCounter");
		//System.out.println("counter: " + counter);
		//System.out.println("index: " + index);
		Clause union = null;
		if (counter[index]) {
			//System.out.println("---------START-----------------------");
			//System.out.println("index: " + index);
			Clause a = effectedClauses[index][0];
			Clause b = effectedClauses[index][1];
			//System.out.println("a: " + a);
			//System.out.println("b: " + b);
			clearEffectedClauses(index);
			counter[index] = false; 
			index--;
			union = Clause.union_v2(a, b, index);
			if (first) a.addLearnedClause(union);
			if (first) b.addLearnedClause(union);
			if (first) addLearnedClause_v4(union); // this adds the most speed
			//System.out.println("union: " + union);
		}
		while(counter[index] && union.lastVarIndex > 0) {
			//System.out.println("index: " + index);
			Clause a = effectedClauses[index][0];
			clearEffectedClauses(index);
			counter[index] = false; 
			if (union.lastLiteral == index) {
				Clause b = union;
				//System.out.println("a: " + a);
				//System.out.println("b: " + b);
				union = Clause.union_v2(a, b, index);
				//a.addLearnedClause(union);
				//b.addLearnedClause(union);
				//S.addLearnedClause_v4(union);
				//System.out.println("union: " + union);
			}
			index--;
			//System.out.println("index: " + index);
		}
		if (union != null) {
			//System.out.println("last union: " + union);
			//System.out.println("counter: " + counter);
			while(index>union.lastVarIndex) 
			{
				//System.out.println("index: " + index);
				//Clause aaa = S.effectedClauses[index][0];
				//Clause bbb = S.effectedClauses[index][1];
				//System.out.println("aaa: " + aaa);
				//System.out.println("bbb: " + bbb);
				
				counter[index] = false;
				clearEffectedClauses(index);
				index--;
			}
			//System.out.println("index: " + index);
			//Clause aaa = S.effectedClauses[index][0];
			//Clause bbb = S.effectedClauses[index][1];
			//System.out.println("aaa: " + aaa);
			//System.out.println("bbb: " + bbb);
			
			//System.out.println("counter: " + counter);
			addEffectedClause(index, union);
			return increaseCounter_v5(index, false, counter);
		}
		counter[index] = true;
		//System.out.println("counter: " + counter);
		//System.out.println("index: " + index);
		return index;
	}
	private int increaseCounter_v6(int index, boolean first, boolean[] counter, boolean[] counterX) {
		//System.out.println("increaseCounter");
		//System.out.println("counter: " + counter);
		//System.out.println("index: " + index);
		Clause union = null;
		if (counter[index]) {
			//System.out.println("---------START-----------------------");
			//System.out.println("index: " + index);
			Clause a = effectedClauses[index][0];
			Clause b = effectedClauses[index][1];
			//System.out.println("a: " + a);
			//System.out.println("b: " + b);
			clearEffectedClauses(index);
			counter[index] = false; 
			counterX[index] = false;
			index--;
			union = Clause.union_v2(a, b, index);
			if (first) a.addLearnedClause(union);
			if (first) b.addLearnedClause(union);
			if (first) addLearnedClause_v4(union); // this adds the most speed
			//System.out.println("union: " + union);
		}
		while(counter[index] && union.lastVarIndex > 0) {
			//System.out.print("index: " + index);
			Clause a = effectedClauses[index][0];
			clearEffectedClauses(index);
			counter[index] = false;
			counterX[index] = false;
			if (union.lastLiteral == index) {
				Clause b = union;
				//System.out.println("a: " + a);
				union = Clause.union_v2(a, b, index);
				//a.addLearnedClause(union);
				//b.addLearnedClause(union);
				//S.addLearnedClause_v4(union);
				//System.out.println("union: " + union);
				//System.out.println("counter: " + counter);
			}
			index--;
			//System.out.println("index: " + index);
		}
		if (union != null) {
			//System.out.println("last union: " + union);
			//System.out.println("counter: " + counter);
			while(index>union.lastVarIndex) 
			{
				//System.out.println("index: " + index);
				//Clause aaa = S.effectedClauses[index][0];
				//Clause bbb = S.effectedClauses[index][1];
				//System.out.println("aaa: " + aaa);
				//System.out.println("bbb: " + bbb);
				
				counter[index] = false;
				counterX[index] = false;
				clearEffectedClauses(index);
				index--;
			}
			//System.out.println("index: " + index);
			//Clause aaa = S.effectedClauses[index][0];
			//Clause bbb = S.effectedClauses[index][1];
			//System.out.println("aaa: " + aaa);
			//System.out.println("bbb: " + bbb);
			
			//System.out.println("counter: " + counter);
			addEffectedClause(index, union);
			return increaseCounter_v6(index, false, counter, counterX);
		}
		counter[index] = true;
		counter[index] = true;
		//System.out.println("counter: " + counter);
		//System.out.println("index: " + index);
		return index;
	}
	public void printStatistics() {
		System.out.println("numberOfRunsOfTheMainLoop: " + numberOfRunsOfTheMainLoop);
		System.out.println("numberOfUsedLearnedClauses: " + numberOfUsedLearnedClauses);
		System.out.println("islandsLengthAvg: " + (double)islandsLengthSum / islandsCount);
		System.out.println("islandsMaxLength: " + islandsMaxLength);
		System.out.println("seaLengthAvg: " + (double)lastSeaLengthSum / lastSeaCount);
		System.out.println("seaMaxLength: " + lastSeaMaxLength);
		System.out.println("jumpLengthAvg: " + (double)jumpLengthSum / jumpCount);
		System.out.println("jumpMaxLength: " + jumpMaxLength);
		System.out.println("numberOfClauses: " + S.numberOfClauses);
		System.out.println("numberOfCasesWithoutLearnedClauses: " + numberOfCasesWithoutLearnedClauses);
		System.out.println("numberOfUnAffectedClauses: " + numberOfUnAffectedClauses());
	}
	public int numberOfUnAffectedClauses() {
		int numberOfUnAffected = 0;
		for(int i=0; i<S.clauseListOrderdByLastVarIndexNeg.length; i++) {
			for(int j=0; j<S.clauseListOrderdByLastVarIndexNeg[i].length; j++) {
				if (!S.clauseListOrderdByLastVarIndexNeg[i][j].isEffected) {
					numberOfUnAffected++;
					//System.out.println("unaffected clause["+i+"]["+j+"]: " + S.clauseListOrderdByLastVarIndexNeg[i][j]);
				}
			}
		}
		for(int i=0; i<S.clauseListOrderdByLastVarIndexPos.length; i++) {
			for(int j=0; j<S.clauseListOrderdByLastVarIndexPos[i].length; j++) {
				if (!S.clauseListOrderdByLastVarIndexPos[i][j].isEffected) {
					numberOfUnAffected++;
					//System.out.println("unaffected clause["+i+"]["+j+"]: " + S.clauseListOrderdByLastVarIndexPos[i][j]);
				}
			}
		}
		return numberOfUnAffected;
	}
}

class ClauseSet {
	int numberOfVariables;
	int numberOfClauses;
	int indexOfLastVariableOfBestBlackClause;
	Clause bestBlackClause;
	Clause[][] clauseListOrderdByLastVarIndexNeg;
	Clause[][] clauseListOrderdByLastVarIndexPos;
	
	public ClauseSet(int numberOfVariables,
			 int numberOfClauses,
			 int indexOfLastVariableOfBestBlackClause,
			 Clause bestBlackClause,
			 ClauseList[] clauseListOrderdByLastVarIndexNeg,
			 ClauseList[] clauseListOrderdByLastVarIndexPos){
		this.numberOfVariables = numberOfVariables;
		this.numberOfClauses = numberOfClauses;
		this.indexOfLastVariableOfBestBlackClause = indexOfLastVariableOfBestBlackClause;
		this.bestBlackClause = bestBlackClause;
		if (CSFLOC18.addLongTailClauses == 1) {
			//System.out.println("addlongtail");
			addLongTailClauses(clauseListOrderdByLastVarIndexNeg, clauseListOrderdByLastVarIndexPos);
		}
		this.clauseListOrderdByLastVarIndexNeg = new Clause[numberOfVariables+1][];
		for(int i=0; i<=numberOfVariables; i++) {
			int length = clauseListOrderdByLastVarIndexNeg[i].clauseList.size();
			this.clauseListOrderdByLastVarIndexNeg[i] = new Clause[length];
			for(int j=0; j<length; j++) {
				this.clauseListOrderdByLastVarIndexNeg[i][j] =
						clauseListOrderdByLastVarIndexNeg[i].clauseList.get(j);
			}
		}
		this.clauseListOrderdByLastVarIndexPos = new Clause[numberOfVariables+1][];
		for(int i=0; i<=numberOfVariables; i++) {
			int length = clauseListOrderdByLastVarIndexPos[i].clauseList.size();
			this.clauseListOrderdByLastVarIndexPos[i] = new Clause[length];
			for(int j=0; j<length; j++) {
				this.clauseListOrderdByLastVarIndexPos[i][j] = 
						clauseListOrderdByLastVarIndexPos[i].clauseList.get(j);
			}  
		}
	}
	public void addLongTailClauses(ClauseList[] clauseListOrderdByLastVarIndexNeg,
			                       ClauseList[] clauseListOrderdByLastVarIndexPos) {
		for(int i=numberOfVariables; i>0; i--) {
			if (clauseListOrderdByLastVarIndexNeg[i].clauseList.size() == 0) continue;
			Clause b = clauseListOrderdByLastVarIndexNeg[i].clauseList.get(0);
			for(int j=0; j<clauseListOrderdByLastVarIndexPos[i].clauseList.size(); j++) {
				Clause a = clauseListOrderdByLastVarIndexPos[i].clauseList.get(j);
				Clause c = Clause.resolution_v3(a,b,a.lastVarIndex);
				//System.out.println("a: " + a);
				//System.out.println("b: " + b);
				if (c!=null) {
					//System.out.println("c: " + c);
					if (c.lastLiteral > 0) { clauseListOrderdByLastVarIndexPos[c.lastVarIndex].insert(c); } 
					else { clauseListOrderdByLastVarIndexNeg[c.lastVarIndex].insert(c); }
				}
			}
		}
		for(int i=numberOfVariables; i>0; i--) {
			if (clauseListOrderdByLastVarIndexPos[i].clauseList.size() == 0) continue;
			Clause a = clauseListOrderdByLastVarIndexPos[i].clauseList.get(0);
			for(int k=1; k<clauseListOrderdByLastVarIndexNeg[i].clauseList.size(); k++) {
				Clause b = clauseListOrderdByLastVarIndexNeg[i].clauseList.get(k);
				Clause c = Clause.resolution_v3(a,b,a.lastVarIndex);
				//System.out.println("a: " + a);
				//System.out.println("b: " + b);
				if (c!=null) {
					//System.out.println("c: " + c);
					if (c.lastLiteral > 0) { clauseListOrderdByLastVarIndexPos[c.lastVarIndex].insert(c); } 
					else { clauseListOrderdByLastVarIndexNeg[c.lastVarIndex].insert(c); }
				}
			}
		}
	}
	
}
abstract class ClauseList {
	ArrayList<Clause> clauseList = new ArrayList<Clause>();
	public abstract void insert(Clause c);
}
class ClauseListWithSorting extends ClauseList {
	@Override
	public void insert(Clause c) {
		int i=0;
		while(i<clauseList.size() && (clauseList.get(i).vars[0] < c.vars[0])) i++;
		clauseList.add(i, c);
	}
}
class ClauseListWithoutSorting extends ClauseList {
	@Override
	public void insert(Clause c) { clauseList.add(c); }
}
class Clause {
	boolean isEffected = false;
	int[] vars; // számitott
	int[] literals;
	boolean[] isPos; // számitott
	int lastLiteral;
	int lastVarIndex;
	Clause[] learnedClauses;
	// ArrayList<Clause> clauses;
	// ArrayList<Integer> notToChecks;
	protected Clause() {}
	/**
	 * The literals must be ordered by the absolute value of its elements.
	 */
	public Clause(int[] literals) {
		this.literals = literals;
		vars = new int[literals.length];
		for(int i=0; i<literals.length; i++) { vars[i] = Math.abs(literals[i]); }
		isPos = new boolean[literals.length];
		for(int i=0; i<literals.length; i++) { isPos[i] = (literals[i] > 0); }
		if (literals.length > 0) {
			lastLiteral = literals[literals.length-1];
			lastVarIndex = Math.abs(lastLiteral);
		}
		if (CSFLOC18.learn3ClausesForClauses == 1) learnedClauses = new Clause[3];
		//clauses = new ArrayList<Clause>();
		//clauses.add(this);
		//notToChecks = new ArrayList<Integer>();
		//notToChecks.add(1);
	}
	public Clause(boolean[] counter, int index) {
		vars = new int[index];
		literals = new int[index];
		isPos = new boolean[index];
		for(int i=1; i<=index; i++) {
			isPos[i-1] = counter[i];
			vars[i-1] = i;
			literals[i-1] = counter[i] ? i : -i;
		}
		lastLiteral = literals[literals.length-1];
		lastVarIndex = Math.abs(lastLiteral);
		if (CSFLOC18.learn3ClausesForClauses == 1) learnedClauses = new Clause[3];
		//clauses = new ArrayList<Clause>();
		//clauses.add(this);
		//notToChecks = new ArrayList<Integer>();
		//notToChecks.add(1);
	}
	//public ArrayList<Clause> getClauses() { return clauses; }
	//public ArrayList<Integer> getNotToChecks() { return notToChecks; }
	public void addLearnedClause(Clause c) {
		if (CSFLOC18.learn3ClausesForClauses == 0) return;
		learnedClauses[2] = learnedClauses[1];
		learnedClauses[1] = learnedClauses[0];
		learnedClauses[0] = c;
	}
	public boolean isBlack() {
		for(int i=0; i<literals.length; i++) { if (literals[i] > 0) return false; }
		return true;
	}
	public boolean subsumedBy(boolean[] counter) {
		for(int i=0; i<literals.length-1; i++) {
			if (counter[vars[i]] != isPos[i]) { return false; }
		}
		return true;
	}
	public boolean subsumedBy(boolean[] counter, int notToCheck) {
		for(int i=0; i<literals.length-notToCheck; i++) {
			if (counter[vars[i]] != isPos[i]) { return false; }
		}
		return true;
	}
	public boolean subsumedByNonLazy(boolean[] counter) {
		for(int i=0; i<literals.length; i++) {
			if (counter[vars[i]] != isPos[i]) { return false; }
		}
		return true;
	}
	public void setCounterX(boolean[] counterX) {
		for(int i=0; i<literals.length; i++) {
			counterX[vars[i]] = true;
		}
	}
	public String toString() {
		String out = "";
		for(int i=0; i<literals.length; i++) {
			out += literals[i] + " ";
		}
		return out;
	}
	public static Clause union_v2(Clause a, Clause b, int uptoIndex) {
		//System.out.println("a: " + a);
		//System.out.println("b: " + b);
		//System.out.println("uptoIndex: " + uptoIndex);
		int aIndex = 0;
		int bIndex = 0;
		int[] aLiterals = a.literals;
		int[] bLiterals = b.literals;
		int[] aVarIndices = a.vars;
		int[] bVarIndices = b.vars;
		int[] copyBuffer = new int[aLiterals.length + bLiterals.length-2];
		int index = 0;
		int aLit = aLiterals[aIndex];
		//System.out.println("aLit: " + aLit);
		int bLit = bLiterals[bIndex];
		//System.out.println("bLit: " + bLit);
		int aVar = aVarIndices[aIndex];
		int bVar = bVarIndices[bIndex];
		while(aLit != -bLit) {
			if (aLit == bLit) {
				copyBuffer[index] = aLit;
				//System.out.println("index: + index " + ", copyBuffer: " + copyBuffer[index]);
				aIndex++;
				bIndex++;
				if (aIndex == aLiterals.length || bIndex == bLiterals.length) break;
				aLit = aLiterals[aIndex];
				bLit = bLiterals[bIndex];
				aVar = aVarIndices[aIndex];
				bVar = bVarIndices[bIndex];
			}
			else if (aVar < bVar) {
				copyBuffer[index] = aLit;
				//System.out.println("index: + index " + ", copyBuffer: " + copyBuffer[index]);
				aIndex++;
				if (aIndex == aLiterals.length) break;
				aLit = aLiterals[aIndex];
				aVar = aVarIndices[aIndex];
			}
			else {
				copyBuffer[index] = bLit;
				//System.out.println("index: + index " + ", copyBuffer: " + copyBuffer[index]);
				bIndex++;
				if (bIndex == bLiterals.length) break;
				bLit = bLiterals[bIndex];
				bVar = bVarIndices[bIndex];
			}
			index++;
		}
		if (aIndex >= aLiterals.length) {
			//System.out.println("bIndex while: ");
			while(bIndex < bLiterals.length-1) {
				index++;
				copyBuffer[index] = bLiterals[bIndex];
				bIndex++;
			}
		}
		else if (bIndex >= bLiterals.length) {
			//System.out.println("aIndex while: ");
			while(aIndex < aLiterals.length-1) {
				index++;
				copyBuffer[index] = aLiterals[aIndex];
				aIndex++;
			}
		}
		int[] literals = new int[index];
		//System.out.print("union: ");
		for(int i=0; i<index; i++) { 
			literals[i] = copyBuffer[i];
			//System.out.print(" " + literals[i] );
		}
		//System.out.println();
		//try { Thread.sleep(1000); } catch(Exception e) {}
		return new Clause(literals);
	}
	public static Clause union_v3(Clause a, Clause b, int uptoIndex) {
		//System.out.println("a: " + a);
		//System.out.println("b: " + b);
		//System.out.println("uptoIndex: " + uptoIndex);
		int aIndex = 0;
		int bIndex = 0;
		int[] aLiterals = a.literals;
		int[] bLiterals = b.literals;
		int[] aVarIndices = a.vars;
		int[] bVarIndices = b.vars;
		int[] copyBuffer = new int[aLiterals.length + bLiterals.length-2];
		int index = 0;
		int aLit = aLiterals[aIndex];
		int bLit = bLiterals[bIndex];
		int aVar = aVarIndices[aIndex];
		int bVar = bVarIndices[bIndex];
		while(aLit != -bLit) {
			if (aLit == bLit) {
				copyBuffer[index] = aLit;
				aIndex++;
				bIndex++;
				if (aIndex == aLiterals.length || bIndex == bLiterals.length) break;
				aLit = aLiterals[aIndex];
				bLit = bLiterals[bIndex];
				aVar = aVarIndices[aIndex];
				bVar = bVarIndices[bIndex];
			}
			else if (aVar < bVar) {
				copyBuffer[index] = aLit;
				aIndex++;
				if (aIndex == aLiterals.length) break;
				aLit = aLiterals[aIndex];
				aVar = aVarIndices[aIndex];
			}
			else {
				copyBuffer[index] = bLit;
				bIndex++;
				if (bIndex == bLiterals.length) break;
				bLit = bLiterals[bIndex];
				bVar = bVarIndices[bIndex];
			}
			index++;
		}
		if (aIndex >= aLiterals.length) {
			//System.out.println("elso while: ");
			while(bIndex < bLiterals.length-1) {
				index++;
				copyBuffer[index] = bLiterals[bIndex];
				bIndex++;
			}
		}
		else if (bIndex >= bLiterals.length) {
			//System.out.println("másiodik while: ");
			while(aIndex < aLiterals.length-1) {
				index++;
				copyBuffer[index] = aLiterals[aIndex];
				aIndex++;
			}
		}
		int[] literals = new int[index];
		//System.out.print("union: ");
		for(int i=0; i<index; i++) { 
			literals[i] = copyBuffer[i];
			//System.out.print(" " + literals[i] );
		}
		//System.out.println();
		//try { Thread.sleep(1000); } catch(Exception e) {}
		return new Clause(literals);
	}
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		Clause other = (Clause)o;
		if (literals.length != other.literals.length) return false;
		for(int i=0; i<literals.length; i++) { if (literals[i] != other.literals[i]) return false; }
		return true;
	}
	public static Clause resolution_v3(Clause a, Clause b, int uptoIndex) {
		//System.out.println("a: " + a);
		//System.out.println("b: " + b);
		//System.out.println("uptoIndex: " + uptoIndex);
		int aIndex = 0;
		int bIndex = 0;
		int[] aLiterals = a.literals;
		int[] bLiterals = b.literals;
		int[] copyBuffer = new int[aLiterals.length + bLiterals.length];
		int index = 0;
		int aLit = aLiterals[aIndex];
		int bLit = bLiterals[bIndex];
		while(aLit != -bLit) {
			if (aLit == bLit) {
				copyBuffer[index] = aLit;
				aIndex++;
				bIndex++;
				if (aIndex == aLiterals.length || bIndex == bLiterals.length) break;
				aLit = aLiterals[aIndex];
				bLit = bLiterals[bIndex];
			}
			else if (Math.abs(aLit) < Math.abs(bLit)) {
				copyBuffer[index] = aLit;
				aIndex++;
				if (aIndex == aLiterals.length) break;
				aLit = aLiterals[aIndex];
			}
			else {
				copyBuffer[index] = bLit;
				bIndex++;
				if (bIndex == bLiterals.length) break;
				bLit = bLiterals[bIndex];
			}
			if (Math.abs(copyBuffer[index]) > uptoIndex) { System.out.println("Kell ez a sor!!!"); break; } // kell ez a sor?
			index++;
		}
		if (aIndex >= aLiterals.length) {
			//System.out.println("elso while: ");
			while(Math.abs(copyBuffer[index]) <= uptoIndex) {
				index++;
				copyBuffer[index] = bLiterals[bIndex];
				bIndex++;
			}
		}
		else if (bIndex >= bLiterals.length) {
			//System.out.println("másiodik while: ");
			while(Math.abs(copyBuffer[index]) <= uptoIndex) {
				index++;
				copyBuffer[index] = aLiterals[aIndex];
				aIndex++;
			}
		}
		if (!(aIndex == aLiterals.length-1 && bIndex == bLiterals.length-1)) {
			//System.out.println("a: " + a);
			//System.out.println("b: " + b);
			//System.out.println("uptoIndex: " + uptoIndex);
			//System.out.println("aIndex: " + aIndex);
			//System.out.println("bIndex: " + bIndex);
			return null;
		}
		
		int[] literals = new int[index];
		//System.out.print("union: ");
		for(int i=0; i<index; i++) { 
			literals[i] = copyBuffer[i];
			//System.out.print(" " + literals[i] );
		}
		//System.out.println();
		//try { Thread.sleep(1000); } catch(Exception e) {}
		//System.out.println("lastLiteral: " + lastLiteral);
		//System.out.println("lastVarIndex: " + lastVarIndex);
		return new Clause(literals);
	}
}
/*
class DerivedClause extends Clause {
	ArrayList<Clause> clauses;
	ArrayList<Integer> notToChecks;
	public DerivedClause(Clause a, Clause b, int uptoIndex) {
		clauses = new ArrayList<>();
		clauses.addAll(a.getClauses());
		clauses.addAll(b.getClauses());
		notToChecks = new ArrayList<>();
		notToChecks.addAll(a.getNotToChecks());
		notToChecks.addAll(b.getNotToChecks());
		for(int i=clauses.size()-1; i>=0; i--) {
			int[] vars = clauses.get(i).vars;
			int length = vars.length;
			int notToCheck = notToChecks.get(i);
			int index = length - notToCheck - 1;
			if (vars[index] >= uptoIndex) {
				notToCheck--;
				notToChecks.set(i, notToCheck);
				if (length == notToCheck) { clauses.remove(i); notToChecks.remove(i); }
			}
		}
	}
	@Override
	public ArrayList<Clause> getClauses() { return clauses; }
	@Override
	public ArrayList<Integer> getNotToChecks() { return notToChecks; }
	@Override
	public boolean subsumedBy(boolean[] counter) {
		for(int i=0; i<clauses.size(); i++) {
			if (!clauses.get(i).subsumedBy(counter, notToChecks.get(i))) return false;
		}
		return true;
	}
}
*/
class HighLevelReader {
	int numberOfVariables;
	int numberOfClauses;
	ClauseList[] clauseListOrderdByLastVarIndexNeg;
	ClauseList[] clauseListOrderdByLastVarIndexPos;
	int indexOfLastVariableOfBestBlackClause;
	Clause bestBlackClause;
	
	ArrayList<int[]> cs;
	int[] translate;
	
	public HighLevelReader(DIMACSReader dr, String variableRenamingStrategy) {
		numberOfVariables = dr.numberOfVariables;
		cs = dr.cs;
		if (CSFLOC18.BCP == 1) if (dr.units.size() > 0) { BCP(cs, dr.units); }
		
		int[] oldTranslate = null;
		int[] variablePairs = null;
		
		for(int i = 0; i<variableRenamingStrategy.length(); i++) {
			char c = variableRenamingStrategy.charAt(i);
			//System.out.println("c: " + c);
			switch(c) {		
			case 'b':
			case 'B':
				translate = renameBlackClauses(cs);
				translateVariables(cs, translate);			
				translate = productOfTranslates(translate, oldTranslate);	
				oldTranslate = translate;
				break;
			case 'h':
			case 'H':
				translate = renameDefiniteHornClauses(cs);			
				translateVariables(cs, translate);			
				translate = productOfTranslates(translate, oldTranslate);	
				oldTranslate = translate;
				break;
			case 'c':
			case 'C':
				variablePairs = createVariablePairs(cs);
				int clim = 2; //clustering factor must be at least 2
				if (CSFLOC18.clusteringFactor >= 2) clim = CSFLOC18.clusteringFactor; 
				translate = clusterVariables(variablePairs, clim);
				translateVariables(cs, translate);
				translate = productOfTranslates(translate, oldTranslate);
				oldTranslate = translate;
				break;
			case 'w':
			case 'W':					
				translate = renameWhiteClauses(cs);				
				translateVariables(cs, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			case 'r':
			case 'R':
				variablePairs = createVariablePairs(cs);
				translate = simplerVariableRenaming(variablePairs);
				translateVariables(cs, translate);
				translate = productOfTranslates(translate, oldTranslate);
				oldTranslate = translate;
				break;
			case 'i':
			case 'I':
				translate = renameIslandClauses(cs);				
				translateVariables(cs, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			case 's':
			case 'S':
				translate = renameStraitClauses(cs);				
				translateVariables(cs, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			default:
				break;
			}
		}
		//System.out.println("before ordering");
		orderLiterals(cs);
		//System.out.println("ordering is done");
		if (CSFLOC18.clauseListWithSorting == 1) {
			initDataStructureWithSorting();
		}
		else {
			initDataStructureWithoutSorting();
		}
		//System.out.println("init data structure is done");
		fillDataStructure();
		//System.out.println("fill data structure is done");
		//System.out.println("high level ready");
	}
	
	
	/*
	private int[] getBlackFrequ(ArrayList<int[]> cs)
	{
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		int[] frequ = new int[numberOfVariables+1];
		int bestCount = 1;
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			boolean allNegative = true;
			for(int j=0; j<source.length; j++) { if (source[j] == Math.abs(source[j])) { allNegative = false; break; } }
			if (!allNegative) continue;
			for(int j=0; j<source.length; j++) { 
				int index = Math.abs(source[j]);
				frequ[index]++;
				if (frequ[index] > bestCount) bestCount = frequ[index];
			}
		}
		
		int[] counts = new int[bestCount];
		for(Int2 key : keys) {
			int count = count2.get(key);
			counts[bestCount-count]++;
		}
		int[] variablePairs = new int[keys.size()*2];
		//System.out.println("bestCount: " + bestCount);
		int[] indeces = new int[bestCount];
		indeces[0] = 0;
		for(int i=1; i<bestCount; i++) { indeces[i] = indeces[i-1] + counts[i-1]*2; }
		for(Int2 key : keys) {
			int count = count2.get(key);
			int i = indeces[bestCount-count];
			variablePairs[i] = key.getA();
			variablePairs[i+1] = key.getB();
			indeces[bestCount-count] += 2;
		}
		return translate;
	}
	*/
	
	
	private void BCP(ArrayList<int[]> cs, ArrayList<Integer> units) {
		while (units.size() > 0) {
			int unit = units.get(0);
			units.remove(0);
			//System.out.println("unit: " + unit);
			for(int i=cs.size()-1; i>=0; i--) {
				int[] clause = cs.get(i);
				boolean delete = false;
				int delIndex = -1;
				for(int j=0; j<clause.length; j++) {
					if (clause[j] == unit) { delete = true; break; }
					if (clause[j] == -unit) { delIndex = j; break; }
				}
				if (delete) { cs.remove(i); }
				if (delIndex > -1) {
					int[] literals = new int[clause.length-1];
					int litIndex = 0;
					for(int j=0; j<clause.length; j++) {
						if (j == delIndex) { continue; }
						literals[litIndex] = clause[j];
						litIndex++;
					}
					cs.set(i, literals);
					if (literals.length == 0) { 
						System.out.println("BINGO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						System.exit(-1);
					}
					if (literals.length == 1 && !units.contains(literals[0])) { 
						units.add(literals[0]);
						//System.out.println("unit has been found: " + literals[0]);
						//System.out.println("number of units: " + units.size());
					}
				}
			}
		}
	}
	private int[] clusterVariables(int[] variablePairs, int clim) {
		int[] var_alloc = new int[numberOfVariables + 1];
		int[] clr_size = new int[numberOfVariables];
		int clr_count = 0;
		ArrayList<int[]> clusterList = new ArrayList<int[]>();
		int[] newCluster = new int[clim];
		clusterList.add(newCluster);
		for(int i=0; i<variablePairs.length; i+=2) {
			int lit_a = variablePairs[i];
			int lit_b = variablePairs[i+1];
			if (var_alloc[lit_a] == 0) { /* lit_a is not clustered */
				if (var_alloc[lit_b] == 0) { /* lit_b is not clustered */
					/* create a new cluster with lit_a and lit_b */
					clr_count++;
					var_alloc[lit_a] = clr_count;
					var_alloc[lit_b] = clr_count;
					newCluster = new int[clim];
					newCluster[0] = lit_a;
					newCluster[1] = lit_b;
					clusterList.add(newCluster);
					clr_size[clr_count] = 2;
				} else { /* lit_b is clustered */
					/* try to put lit_a in same cluster with lit_b */
					int j = var_alloc[lit_b];
					if (clr_size[j] < clim) {
						var_alloc[lit_a] = j;
						clusterList.get(j)[clr_size[j]] = lit_a;
						clr_size[j]++;
					} else { /* not possible - create another cluster with lit_a only */
						clr_count++;
						var_alloc[lit_a] = clr_count;
						newCluster = new int[clim];
						newCluster[0] = lit_a;
						clusterList.add(newCluster);
						clr_size[clr_count] = 1;
					}
				}
			} else { /* lit_a is clustered */
				if (var_alloc[lit_b] == 0) { /* lit_b is not clustered */
					/* try to put lit_b in same cluster with lit_a */
					int j = var_alloc[lit_a];
					if (clr_size[j] < clim) {
						var_alloc[lit_b] = j;
						clusterList.get(j)[clr_size[j]] = lit_b;
						clr_size[j]++;
					} else { /* not possible - create another cluster with lit_b only */
						clr_count++;
						var_alloc[lit_b] = clr_count;
						newCluster = new int[clim];
						newCluster[0] = lit_b;
						clusterList.add(newCluster);
						clr_size[clr_count] = 1;
					}
				} else { /* lit_b is clustered */
					/* try to merge the two clusters */
					int ja = var_alloc[lit_a];
					int jb = var_alloc[lit_b];
					if ((ja != jb) && /* they are in different clusters */
							((clr_size[ja] + clr_size[jb]) <= clim)) { /* possible */
						//if(jb<ja) { int tmp = ja; ja = jb; jb = tmp; System.out.println("new line is hit!!!!!!!!!!!!!!!!!");} // new line
						/* put all vars from cluster "jb" into cluster "ja" */
						for (int j = 0; j <= numberOfVariables; j++) {
							if (var_alloc[j] == jb) {
								var_alloc[j] = ja;
							}
						}
						for(int j = 0; j < clr_size[jb]; j++) {
							clusterList.get(ja)[clr_size[ja]+j] = clusterList.get(jb)[j];
						}
						clr_size[ja] += clr_size[jb]; /* update size */
						/* remove cluster "jb" by setting the size to 0 */
						clr_size[jb] = 0;
					} else { /* move to lover index cluster */
						if (ja < jb && clr_size[ja] < clim) {
							var_alloc[lit_b] = ja;
							clusterList.get(ja)[clr_size[ja]] = lit_b;
							clr_size[ja]++;
							int j;
							for(j=0; j<clr_size[jb]; j++) if (clusterList.get(jb)[j] == lit_b) break;
							clusterList.get(jb)[j] = clusterList.get(jb)[clr_size[jb]-1];
							clr_size[jb]--;
						} else if (jb < ja && clr_size[jb] < clim) {
							var_alloc[lit_a] = jb;
							clusterList.get(jb)[clr_size[jb]] = lit_a;
							clr_size[jb]++;
							int j;
							for(j=0; j<clr_size[ja]; j++) if (clusterList.get(ja)[j] == lit_a) break;
							clusterList.get(ja)[j] = clusterList.get(ja)[clr_size[ja]-1];
							clr_size[ja]--;
						}
					}
				}
			}
		}
		int[] translate = new int[numberOfVariables+1];
		int varIndex = 1;
		for (int ic = 1; ic <= clr_count; ic++) {
			//System.out.println("ic: " + ic);
			int[] cluster = clusterList.get(ic);
			for(int j=0; j < clr_size[ic]; j++) {
				//System.out.println("j: " + j);
				translate[cluster[j]] = varIndex;
				//System.out.println(cluster[j] + " -> " + varIndex);
				varIndex++;
			}
		}
		if (varIndex < numberOfVariables+1) {
			for(int i=1; i<translate.length; i++) {
				if (translate[i] == 0) { 
					translate[i] = varIndex;
					//System.out.println("!!!!!!!!!!!!!   " +i + " -> " + varIndex);
					varIndex++;
				}
			}
		}
		return translate;
	}
	private int[] simplerVariableRenaming(int[] variablePairs) {
		int[] translate = new int[numberOfVariables+1];
		int nextValue = 1;
		for(int i=0; i<variablePairs.length; i++) {
			int a = variablePairs[i];
			//System.out.println("a: " + a + ", count1[a]: " + count1[a]);
			if (translate[a] == 0) { 
				translate[a] = nextValue;
				//System.out.println(a + " -> " + nextValue);
				nextValue++;
			}
			if (nextValue == numberOfVariables+1) break;
		}
		if (nextValue < numberOfVariables+1) {
			for(int i=1; i<translate.length; i++) {
				if (translate[i] == 0) { 
					translate[i] = nextValue;
					//System.out.println("!!!!!!!!!!!!!   " +i + " -> " + nextValue);
					nextValue++;
				}
			}
		}
		return translate;
	}
	private int[] renameWhiteClauses(ArrayList<int[]> cs) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			boolean allPositive = true;
			for(int j=0; j<source.length; j++) { if (source[j] != Math.abs(source[j])) { allPositive = false; break; } }
			if (!allPositive) continue;
			//System.out.print("A white one:");
			for(int j=0; j<source.length; j++) {
				//System.out.print(" " + source[j]);
				if (translate[source[j]]==0) {
					translate[source[j]] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] renameBlackClauses(ArrayList<int[]> cs) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			boolean allNegative = true;
			for(int j=0; j<source.length; j++) { if (source[j] == Math.abs(source[j])) { allNegative = false; break; } }
			if (!allNegative) continue;
			for(int j=0; j<source.length; j++) { 
				int index = Math.abs(source[j]);
				if (translate[index]==0) {
					translate[index] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	
	private int[] renameDefiniteHornClauses(ArrayList<int[]> cs) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			int numberOfPoz = 0;
			
			for(int j = 0; j < source.length; j++) {
				if(source[j] == Math.abs(source[j])) { numberOfPoz++; }
			}
			
			if(numberOfPoz!=1) continue;
			
			int numberOfNotRenamedLiterals = 0;
			for(int j = 0; j < source.length; j++) {
				if(translate[Math.abs(source[j])] == 0) { numberOfNotRenamedLiterals++; }
			}
			if(numberOfNotRenamedLiterals != source.length) continue;
			
			for(int j = 0; j < source.length; j++) {
				if(translate[Math.abs(source[j])] == 0) {
					translate[Math.abs(source[j])] = varIndex;
					varIndex++;
					if(varIndex == numberOfVariables + 1) break;
				}
			}
			if(varIndex == numberOfVariables + 1) break;
		}
		
		if(varIndex != numberOfVariables + 1) {
			for (int i = varIndex; i<=numberOfVariables; i++) {
				int j = 1; 
				while(translate[j] != 0) j++;
				translate[j] = i;
			}
		}
		
		return translate;
	}
	private int[] renameIslandClauses(ArrayList<int[]> cs) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			int numberOfPoz = 0;
			int posIndex = 0;
			for(int j=0; j<source.length; j++) { if (source[j] == Math.abs(source[j])) { numberOfPoz++; posIndex = j; } }
			if (numberOfPoz!=1) continue;
			//System.out.println(source.length);
			for(int j=0; j<source.length; j++) { 
				if (j == posIndex) continue;
				int index = Math.abs(source[j]);
				if (translate[index]==0) {
					translate[index] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			int index = Math.abs(source[posIndex]);
			if (translate[index]==0) {
				translate[index] = varIndex;
				//System.out.println(source[posIndex] + " -> " + varIndex);
				varIndex++;
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] renameStraitClauses(ArrayList<int[]> cs) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			int numberOfNeg = 0;
			int negIndex = 0;
			for(int j=0; j<source.length; j++) { if (source[j] != Math.abs(source[j])) { numberOfNeg++; negIndex = j; } }
			if (numberOfNeg!=1) continue;
			//System.out.println(source.length);
			int index = Math.abs(source[negIndex]);
			if (translate[index]==0) {
				translate[index] = varIndex;
				//System.out.println(source[posIndex] + " -> " + varIndex);
				varIndex++;
			}
			for(int j=0; j<source.length; j++) { 
				if (j == negIndex) continue;
				int index2 = Math.abs(source[j]);
				if (translate[index2]==0) {
					translate[index2] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] createVariablePairsNew(ArrayList<int[]> cs) {
		int[][] count2 = new int[numberOfVariables+1][numberOfVariables+1];
		int bestCount = 1;
		
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			int[] clause = new int[source.length];
			for(int j=0; j<source.length; j++) {
				int variable = Math.abs(source[j]);
				clause[j] = variable;
			}
			//System.out.println("clause.length-1: " + (clause.length-1));
			if (clause.length == 1) {
				/*
				int var1 = clause[0];
				for(int var2 = 1; var2 <= numberOfVariables; var2++) {
					if (var2 == var1) continue;
				    Int2 key2 = new Int2(var1, var2);
				    bestCount = increaseKey2(count2, key2, bestCount);
				}
				*/
				continue;
			}
			for(int j=0; j<clause.length-1; j++) {
				for(int k=j+1; k<clause.length; k++) {
					int cur = ++count2[clause[j]][clause[k]];
					if (bestCount > cur) bestCount = cur; 
				}
			}
		}
		System.out.println("bestCount: " + bestCount);
		System.exit(-1);
		return null;
	}
	private int[] createVariablePairs(ArrayList<int[]> cs) {
		HashMap<Int2, Integer> count2 = new HashMap<>();
		int bestCount = 1;
		//int sqrt = (int)Math.sqrt(numberOfVariables+1);
		//sqrt = sqrt < 3 ? 3 : sqrt;
		for(int i=0; i<cs.size(); i++) {
			int[] source = cs.get(i);
			//if (source.length > sqrt) continue;
			if (source.length > 12) continue;
			int[] clause = new int[source.length];
			for(int j=0; j<source.length; j++) {
				int variable = Math.abs(source[j]);
				clause[j] = variable;
			}
			//System.out.println("clause.length-1: " + (clause.length-1));
			if (clause.length == 1) {
				/*
				int var1 = clause[0];
				for(int var2 = 1; var2 <= numberOfVariables; var2++) {
					if (var2 == var1) continue;
				    Int2 key2 = new Int2(var1, var2);
				    bestCount = increaseKey2(count2, key2, bestCount);
				}
				*/
				continue;
			}
			for(int j=0; j<clause.length-1; j++) {
				for(int k=j+1; k<clause.length; k++) {
					Int2 key2 = new Int2(clause[j], clause[k]);
					bestCount = increaseKey2(count2, key2, bestCount);
				}
			}
		}
		//System.out.println("bestCount: " + bestCount);
		Set<Int2> keys = count2.keySet();
		int[] counts = new int[bestCount];
		for(Int2 key : keys) {
			int count = count2.get(key);
			counts[bestCount-count]++;
		}
		int[] variablePairs = new int[keys.size()*2];
		//System.out.println("bestCount: " + bestCount);
		int[] indeces = new int[bestCount];
		indeces[0] = 0;
		for(int i=1; i<bestCount; i++) { indeces[i] = indeces[i-1] + counts[i-1]*2; }
		for(Int2 key : keys) {
			int count = count2.get(key);
			int i = indeces[bestCount-count];
			variablePairs[i] = key.getA();
			variablePairs[i+1] = key.getB();
			indeces[bestCount-count] += 2;
		}
		return variablePairs;
	}
	private int increaseKey2(HashMap<Int2, Integer> count2, Int2 key2, int bestCount) {
		if (count2.containsKey(key2)) {
			Integer count = count2.get(key2); 
			count++;
			count2.put(key2, count); 
			if (count > bestCount) bestCount = count;
		} 
		else { count2.put(key2, 1); }
		return bestCount;
	}
	private void translateVariables(ArrayList<int[]> cs, int[] translate) {
		for(int i=0; i<cs.size(); i++) {
			int[] clause = cs.get(i);
			for(int j=0; j<clause.length; j++) {
				int lit = clause[j];
				//System.out.println("lit: " + lit);
				int var = translate[Math.abs(lit)];
				//System.out.println(Math.abs(lit) + " -> " + var);
				if (var <= 0) {System.err.println("Unexpected value in translate table. There must be a bug!"); System.exit(-1); }
				if (lit > 0) clause[j] = var; else clause[j] =-var;
			}
		}
	}
	private int[] productOfTranslates(int[] translate, int[] oldTranslate) {
		if (oldTranslate == null) return translate;
		int[] product = new int[translate.length];
		for(int i=0; i<product.length; i++) {
			product[i] = translate[oldTranslate[i]];
		}
		return product;
	}
	private void orderLiterals(ArrayList<int[]> cs) {
		for(int i=0; i<cs.size(); i++) {
			int[] clause = cs.get(i);
			/* this part works, but a bit more testing is necessary 
			if (clause.length == 3) {
				int a, b, c, A, B, C;
				a = clause[0]; A = a > 0 ? a : -a;
				b = clause[1]; B = b > 0 ? b : -b;
				c = clause[2]; C = c > 0 ? c : -c;
				if (A > B) 
					if (B > C)         { clause[0] = c; clause[1] = b; clause[2] = a; } // A > B, B > C
					else if (A > C)    { clause[0] = b; clause[1] = c; clause[2] = a; } // A > B, C > B, A > C
					     else          { clause[0] = b; clause[1] = a; clause[2] = c; } // A > B, C > B, C > A
				else if (A > C)        { clause[0] = c; clause[1] = a; clause[2] = b; } // B > A, A > C
				       else if (B > C) { clause[0] = a; clause[1] = c; clause[2] = b; } // B > A, C > A, B > C
			                else       { clause[0] = a; clause[1] = b; clause[2] = c; } // B > A, C > A, C > B
				continue;
			}
			*/
			int j = 1;
			int flag = 1;
			while (flag != 0) {
				flag = 0;
				for(int k = 0; k<clause.length-j; k++) {
					if (Math.abs(clause[k]) > Math.abs(clause[k+1])) {
						int temp = clause[k];
						clause[k] = clause[k+1];
						clause[k+1] = temp;
						flag = 1;
					}
				}
				j++;
			}
		}
	}
	private void initDataStructureWithSorting() {
		clauseListOrderdByLastVarIndexNeg = new ClauseList[numberOfVariables+1];
		clauseListOrderdByLastVarIndexPos = new ClauseList[numberOfVariables+1];
		for(int i=0; i<clauseListOrderdByLastVarIndexPos.length; i++) {
			clauseListOrderdByLastVarIndexPos[i] = new ClauseListWithSorting();
		}
		for(int i=0; i<clauseListOrderdByLastVarIndexNeg.length; i++) {
			clauseListOrderdByLastVarIndexNeg[i] = new ClauseListWithSorting();
		}
		indexOfLastVariableOfBestBlackClause = numberOfVariables + 1;
	}
	private void initDataStructureWithoutSorting() {
		clauseListOrderdByLastVarIndexNeg = new ClauseList[numberOfVariables+1];
		clauseListOrderdByLastVarIndexPos = new ClauseList[numberOfVariables+1];
		for(int i=0; i<clauseListOrderdByLastVarIndexPos.length; i++) {
			clauseListOrderdByLastVarIndexPos[i] = new ClauseListWithoutSorting();
		}
		for(int i=0; i<clauseListOrderdByLastVarIndexNeg.length; i++) {
			clauseListOrderdByLastVarIndexNeg[i] = new ClauseListWithoutSorting();
		}
		indexOfLastVariableOfBestBlackClause = numberOfVariables + 1;
	}
	private void fillDataStructure() {
		for(int i=0; i<cs.size(); i++) {
			Clause newClause = new Clause(cs.get(i));
			if (newClause.lastLiteral > 0) { clauseListOrderdByLastVarIndexPos[newClause.lastVarIndex].insert(newClause); }
			else { clauseListOrderdByLastVarIndexNeg[newClause.lastVarIndex].insert(newClause); }
			if (newClause.isBlack() && newClause.lastVarIndex < indexOfLastVariableOfBestBlackClause) { 
				indexOfLastVariableOfBestBlackClause = newClause.lastVarIndex;
				bestBlackClause = newClause;
			}
		}
		numberOfClauses = cs.size();
	}
	/* API */
	public ClauseSet getClauseSet() { 
		return new ClauseSet(numberOfVariables,
							 numberOfClauses,
							 indexOfLastVariableOfBestBlackClause,
							 bestBlackClause,
							 clauseListOrderdByLastVarIndexNeg,
							 clauseListOrderdByLastVarIndexPos); 
	}
	public ArrayList<int[]> getClauseSetAsArrayListOfIntArray() { return cs; }
	
}
class DIMACSReader {
	int numberOfVariables;
	private int numberOfClauses;			// not used
	ArrayList<int[]> cs = new ArrayList<int[]>();
	ArrayList<Integer> units = new ArrayList<Integer>();
	int[] copyBuffer;
	
	public DIMACSReader(String fileName) {
		try {
			BufferedReader file = new BufferedReader(new FileReader(fileName));
			String clause = file.readLine();
			//System.out.println(clause);
			while (clause.charAt(0) != 'p') {
				clause = file.readLine();
				//System.out.println(clause);
			}
			readPLine(clause);
			initDataStructure();
			clause = file.readLine();
			//System.out.println(clause);
			while (clause != null) {
				addCNFClause(clause);
				clause = file.readLine();
				//System.out.println(clause);
			}
			file.close();
		} catch (IOException e) { System.out.println(e); System.exit(-1); }
	}
	private void readPLine(String cnfClause) {
		//System.out.println("cnfClause: " + cnfClause);
		int i1 = 6;
		int i2 = cnfClause.indexOf(" ", i1);
		//System.out.println("i1: " + i1);
		//System.out.println("i2: " + i2);
		//System.out.println(cnfClause.substring(i1, i2));
		//System.out.println(cnfClause.substring(i2+1, cnfClause.length()));
		numberOfVariables = Integer.parseInt(cnfClause.substring(i1, i2));
		if (CSFLOC18.useExtendedResolution > 0) numberOfVariables += CSFLOC18.useExtendedResolution; 
		// use these 4 lines if you need to know the numberOfClauses
		//while(cnfClause.charAt(i2) == ' ') i2++;
		//int i3 = cnfClause.indexOf(" ", i2);
		//if (i3==-1) i3 = cnfClause.length();
		//numberOfClauses = Integer.parseInt(cnfClause.substring(i2, i3));
		
		//System.out.println("numberOfVariables: " + numberOfVariables);
		//System.out.println("numberOfClauses: " + numberOfClauses);
	}
	private void initDataStructure() {
		copyBuffer = new int[numberOfVariables];
	}
	private void addCNFClause(String cnfClause) {
		if (cnfClause.length() == 0 ||
			cnfClause.charAt(0) == '0' || 
			cnfClause.charAt(0) == 'c' ||
			cnfClause.charAt(0) == '%') { return; }
		//System.out.println("cnfClause: " + cnfClause);
		int i = 0;
		int i1 = 0;
		cnfClause = cnfClause.replace("\t", " ");
		while(i1 < cnfClause.length())  {
			int i2 = cnfClause.indexOf(" ", i1);
			if (i1 == i2) { i1++; continue; }
			if (i2 == -1) break;
			String lit = cnfClause.substring(i1, i2);
			i1 = i2;
//System.out.println(lit);
			int literal = Integer.parseInt(lit);
			if (literal == 0) break;
			if (CSFLOC18.useExtendedResolution > 0) {
				copyBuffer[i] = literal > 0 ? literal + CSFLOC18.useExtendedResolution :
					                          literal - CSFLOC18.useExtendedResolution;
			}
			else copyBuffer[i] = literal;
			i++;
		}
		int[] literals = new int[i];
		for(int index=0; index<i; index++) { 
			literals[index] = copyBuffer[index];
		}
		cs.add(literals);
		if (literals.length == 1 && !units.contains(literals[0])) { 
			units.add(literals[0]);
			//System.out.println("unit has been found: " + literals[0]);
			//System.out.println("number of units: " + units.size());
		}
	}
	public void addExtendedResolutionClauses(int numberOfNewVariables) { 
		//if (1==1) return;
		int bb = 2;
		// x <=> a v b = (!x v a v b) es (x v !a) es (x v !b)
		//for(int i=0; i<1; i++) {
			int[] clauseA = {-1, bb};
			int[] clauseB = {1, -bb};
			//int[] clauseC = {1, -bb-1};
			cs.add(clauseA);
			cs.add(clauseB);
			//cs.add(clauseC);
					
		//}
	}
}
class Int2 {
	private int a, b; 
	public int getA() { return a; }
	public int getB() { return b; }
	public Int2(int a, int b) {
		int temp;
		if (a>b) { temp = a; a = b; b = temp; }
		this.a = a; this.b = b;
	}
	@Override
	public boolean equals(Object o) {
		Int2 other = (Int2)o;
		return this.a == other.a && this.b == other.b;
	}
	@Override
	public int hashCode() {
		return a * 11 + b * 3; 
	}
	@Override
	public String toString() {
		String s = "(";
		s += a;
		s += ", ";
		s += b;
		s += ")";
		return s;
	}
}
class CheckSolution {
	public static boolean SimpleCheckSolution(ArrayList<int[]> cs, boolean[] solution) {
		for(int i=0; i<cs.size(); i++) {
			int[] clause = cs.get(i);
			boolean satisfied = false;
			for(int j=0; j<clause.length; j++) {
				int lit = clause[j];
				int var = Math.abs(lit);
				if(solution[var] == (lit>0)) { satisfied = true; break; }
			}
			if (!satisfied) return false;
		}
		return true;
	}
}


/*
class HighLevelReader_v2 {
	int numberOfVariables;
	ClauseList[] clauseListOrderdByLastVarIndexNeg;
	ClauseList[] clauseListOrderdByLastVarIndexPos;
	int indexOfLastVariableOfBestBlackClause;
	Clause bestBlackClause;
	
	ClauseList cl;
	int[] translate;
	
	public HighLevelReader_v2(DIMACSReader dr, String variableRenamingStrategy) {
		//System.out.println("HIGH level reader v2 constructor is started");
		numberOfVariables = dr.numberOfVariables;
		cl = createClauseListFromLostOfIntArrays(dr.cs);
		//System.out.println("CL is created");
		int[] oldTranslate = null;
		int[] variablePairs = null;
		//System.out.println("start renameing");
		for(int i = 0; i<variableRenamingStrategy.length(); i++) {
			char c = variableRenamingStrategy.charAt(i);
			switch(c) {
			case 'b':
			case 'B':
				translate = renameBlackClauses(cl);				
				translateVariables(cl, translate);			
				translate = productOfTranslates(translate, oldTranslate);	
				oldTranslate = translate;
				break;
			case 'h':
			case 'H':
				translate = renameDefiniteHornClauses(cl);			
				translateVariables(cl, translate);			
				translate = productOfTranslates(translate, oldTranslate);	
				oldTranslate = translate;
				break;
			case 'c':
			case 'C':
				variablePairs = createVariablePairs(cl);
				int clim = 2; //clustering factor must be at least 2
				if (CSFLOC18.clusteringFactor >= 2) clim = CSFLOC18.clusteringFactor; 
				translate = clusterVariables(variablePairs, clim);
				translateVariables(cl, translate);
				translate = productOfTranslates(translate, oldTranslate);
				oldTranslate = translate;
				break;
			case 'w':
			case 'W':
				translate = renameWhiteClauses(cl);				
				translateVariables(cl, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			case 'r':
			case 'R':
				variablePairs = createVariablePairs(cl);
				translate = simplerVariableRenaming(variablePairs);
				translateVariables(cl, translate);
				translate = productOfTranslates(translate, oldTranslate);
				oldTranslate = translate;
				break;
			case 'i':
			case 'I':
				translate = renameIslandClauses(cl);				
				translateVariables(cl, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			case 's':
			case 'S':
				translate = renameStraitClauses(cl);				
				translateVariables(cl, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			case 'v':
			case 'V':
				//System.out.println("A vampire!");
				translate = renameVampire(translate);				
				translateVariables(cl, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			case 'u':
			case 'U':
				translate = renameUnits(cl);				
				translateVariables(cl, translate);				
				translate = productOfTranslates(translate, oldTranslate);			
				oldTranslate = translate;
				break;
			default:
				break;
			}
		}
		//System.out.println("end renaming");
		orderLiterals(cl);
		//System.out.println("CL ordering is ready");
		initDataStructure();
		//System.out.println("init data structure is ready");
		fillDataStructure();
		//System.out.println("fill data structure is ready");
		//System.out.println("high level ready");
	}
	private int[] renameUnits(ClauseList cl) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] vars = cl.clauseList.get(i).vars;
			boolean isUnit = vars.length == 1;
			if (!isUnit) continue;
			//System.out.print("A white one:");
			for(int j=0; j<vars.length; j++) {
				//System.out.print(" " + source[j]);
				if (translate[vars[j]]==0) {
					translate[vars[j]] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			int lastJ = 1;
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = lastJ; while(translate[j]!=0) j++;
				translate[j] = i;
				lastJ = j;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] renameVampire(int[] oldTranslate) {
		int[] translate = new int[numberOfVariables+1];
		System.out.println("465 -> " + oldTranslate[465]);
		
		//int newVar = translate[var];
		//if (isPos[j]) literals[j] = newVar; else literals[j] = -newVar;
		// oldTranslate[465] = 766
		// translate[766] = 1
		// product[465] = translate[oldTranslate[465]]
		//product[i] = translate[oldTranslate[i]];
		// product[465] = translate[766] = 1
		
		//if (translate != null) var = translate[i];
		//if (solution[var]) System.out.print(-i + " ");
		
		
		for(int i=1; i<=numberOfVariables; i++) {
			int var = i < oldTranslate[465] ? i : i-1;
			translate[i] = var;
			if (i<10) System.out.println(i + " -> " + translate[i]);
		}
		translate[oldTranslate[465]] = numberOfVariables;
			
		return translate;
	}

	private ClauseList createClauseListFromLostOfIntArrays(ArrayList<int[]> cs) {
		ClauseList cl = new ClauseList();
		for(int i=0; i<cs.size(); i++) {
			//System.out.println("i: " + i);
			int[] literals = cs.get(i);
			int j = 1;
			int flag = 1;
			while (flag != 0) {
				flag = 0;
				for(int k = 0; k<literals.length-j; k++) {
					if (Math.abs(literals[k]) > Math.abs(literals[k+1])) {
						int tempL = literals[k];
						literals[k] = literals[k+1];
						literals[k+1] = tempL;
						flag = 1;
					}
				}
				j++;
			}
			Clause c = new Clause(literals);
			cl.insert(c);
		}
		return cl;
	}
	private int[] clusterVariables(int[] variablePairs, int clim) {
		int[] var_alloc = new int[numberOfVariables + 1];
		int[] clr_size = new int[numberOfVariables];
		int clr_count = 0;
		ArrayList<int[]> clusterList = new ArrayList<int[]>();
		int[] newCluster = new int[clim];
		clusterList.add(newCluster);
		for(int i=0; i<variablePairs.length; i+=2) {
			int lit_a = variablePairs[i];
			int lit_b = variablePairs[i+1];
			if (var_alloc[lit_a] == 0) { // lit_a is not clustered
				if (var_alloc[lit_b] == 0) { // lit_b is not clustered
					// create a new cluster with lit_a and lit_b
					clr_count++;
					var_alloc[lit_a] = clr_count;
					var_alloc[lit_b] = clr_count;
					newCluster = new int[clim];
					newCluster[0] = lit_a;
					newCluster[1] = lit_b;
					clusterList.add(newCluster);
					clr_size[clr_count] = 2;
				} else { // lit_b is clustered
					// try to put lit_a in same cluster with lit_b
					int j = var_alloc[lit_b];
					if (clr_size[j] < clim) {
						var_alloc[lit_a] = j;
						clusterList.get(j)[clr_size[j]] = lit_a;
						clr_size[j]++;
					} else { // not possible - create another cluster with lit_a only
						clr_count++;
						var_alloc[lit_a] = clr_count;
						newCluster = new int[clim];
						newCluster[0] = lit_a;
						clusterList.add(newCluster);
						clr_size[clr_count] = 1;
					}
				}
			} else { // lit_a is clustered
				if (var_alloc[lit_b] == 0) { // lit_b is not clustered
					// try to put lit_b in same cluster with lit_a
					int j = var_alloc[lit_a];
					if (clr_size[j] < clim) {
						var_alloc[lit_b] = j;
						clusterList.get(j)[clr_size[j]] = lit_b;
						clr_size[j]++;
					} else { // not possible - create another cluster with lit_b only
						clr_count++;
						var_alloc[lit_b] = clr_count;
						newCluster = new int[clim];
						newCluster[0] = lit_b;
						clusterList.add(newCluster);
						clr_size[clr_count] = 1;
					}
				} else { // lit_b is clustered
					// try to merge the two clusters
					int ja = var_alloc[lit_a];
					int jb = var_alloc[lit_b];
					if ((ja != jb) && // they are in different clusters
							((clr_size[ja] + clr_size[jb]) <= clim)) { // possible
						//if(jb<ja) { int tmp = ja; ja = jb; jb = tmp; System.out.println("new line is hit!!!!!!!!!!!!!!!!!");} // new line
						// put all vars from cluster "jb" into cluster "ja"
						for (int j = 0; j <= numberOfVariables; j++) {
							if (var_alloc[j] == jb) {
								var_alloc[j] = ja;
							}
						}
						for(int j = 0; j < clr_size[jb]; j++) {
							clusterList.get(ja)[clr_size[ja]+j] = clusterList.get(jb)[j];
						}
						clr_size[ja] += clr_size[jb]; // update size
						// remove cluster "jb" by setting the size to 0
						clr_size[jb] = 0;
					} else { // move to lover index cluster
						if (ja < jb && clr_size[ja] < clim) {
							var_alloc[lit_b] = ja;
							clusterList.get(ja)[clr_size[ja]] = lit_b;
							clr_size[ja]++;
							int j;
							for(j=0; j<clr_size[jb]; j++) if (clusterList.get(jb)[j] == lit_b) break;
							clusterList.get(jb)[j] = clusterList.get(jb)[clr_size[jb]-1];
							clr_size[jb]--;
						} else if (jb < ja && clr_size[jb] < clim) {
							var_alloc[lit_a] = jb;
							clusterList.get(jb)[clr_size[jb]] = lit_a;
							clr_size[jb]++;
							int j;
							for(j=0; j<clr_size[ja]; j++) if (clusterList.get(ja)[j] == lit_a) break;
							clusterList.get(ja)[j] = clusterList.get(ja)[clr_size[ja]-1];
							clr_size[ja]--;
						}
					}
				}
			}
		}
		int[] translate = new int[numberOfVariables+1];
		int varIndex = 1;
		for (int ic = 1; ic <= clr_count; ic++) {
			//System.out.println("ic: " + ic);
			int[] cluster = clusterList.get(ic);
			for(int j=0; j < clr_size[ic]; j++) {
				//System.out.println("j: " + j);
				translate[cluster[j]] = varIndex;
				//System.out.println(cluster[j] + " -> " + varIndex);
				varIndex++;
			}
		}
		if (varIndex < numberOfVariables+1) {
			for(int i=1; i<translate.length; i++) {
				if (translate[i] == 0) { 
					translate[i] = varIndex;
					//System.out.println("!!!!!!!!!!!!!   " +i + " -> " + varIndex);
					varIndex++;
				}
			}
		}
		return translate;
	}
	private int[] simplerVariableRenaming(int[] variablePairs) {
		int[] translate = new int[numberOfVariables+1];
		int nextValue = 1;
		for(int i=0; i<variablePairs.length; i++) {
			int a = variablePairs[i];
			//System.out.println("a: " + a + ", count1[a]: " + count1[a]);
			if (translate[a] == 0) { 
				translate[a] = nextValue;
				//System.out.println(a + " -> " + nextValue);
				nextValue++;
			}
			if (nextValue == numberOfVariables+1) break;
		}
		if (nextValue < numberOfVariables+1) {
			for(int i=1; i<translate.length; i++) {
				if (translate[i] == 0) { 
					translate[i] = nextValue;
					//System.out.println("!!!!!!!!!!!!!   " +i + " -> " + nextValue);
					nextValue++;
				}
			}
		}
		return translate;
	}
	private int[] renameWhiteClauses(ClauseList cl) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] source = cl.clauseList.get(i).literals;
			boolean[] isPos = cl.clauseList.get(i).isPos;
			boolean allPositive = true;
			for(int j=0; j<source.length; j++) { if (!isPos[j]) { allPositive = false; break; } }
			if (!allPositive) continue;
			//System.out.print("A white one:");
			for(int j=0; j<source.length; j++) {
				//System.out.print(" " + source[j]);
				if (translate[source[j]]==0) {
					translate[source[j]] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] renameBlackClauses(ClauseList cl) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] vars = cl.clauseList.get(i).vars;
			boolean[] isPos = cl.clauseList.get(i).isPos;
			boolean allNegative = true;
			for(int j=0; j<vars.length; j++) { 
				if (isPos[j]) { allNegative = false; break; } 
			}
			if (!allNegative) continue;
			for(int j=0; j<vars.length; j++) { 
				int index = vars[j];
				if (translate[index]==0) {
					translate[index] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	
	private int[] renameDefiniteHornClauses(ClauseList cl) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] vars = cl.clauseList.get(i).vars;
			boolean[] isPos = cl.clauseList.get(i).isPos;
			
			int numberOfPoz = 0;
			
			for(int j = 0; j < vars.length; j++) {
				if(isPos[j]) { numberOfPoz++; }
			}
			
			if(numberOfPoz!=1) continue;
			
			int numberOfNotRenamedLiterals = 0;
			for(int j = 0; j < vars.length; j++) {
				if(translate[vars[j]] == 0) { numberOfNotRenamedLiterals++; }
			}
			if(numberOfNotRenamedLiterals != vars.length) continue;
			
			for(int j = 0; j < vars.length; j++) {
				if(translate[vars[j]] == 0) {
					translate[vars[j]] = varIndex;
					varIndex++;
					if(varIndex == numberOfVariables + 1) break;
				}
			}
			if(varIndex == numberOfVariables + 1) break;
		}
		
		if(varIndex != numberOfVariables + 1) {
			for (int i = varIndex; i<=numberOfVariables; i++) {
				int j = 1; 
				while(translate[j] != 0) j++;
				translate[j] = i;
			}
		}
		
		return translate;
	}
	private int[] renameIslandClauses(ClauseList cl) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] vars = cl.clauseList.get(i).vars;
			boolean[] isPos = cl.clauseList.get(i).isPos;
			int numberOfPoz = 0;
			int posIndex = 0;
			for(int j=0; j<vars.length; j++) { if (isPos[j]) { numberOfPoz++; posIndex = j; } }
			if (numberOfPoz!=1) continue;
			//System.out.println(source.length);
			for(int j=0; j<vars.length; j++) { 
				if (j == posIndex) continue;
				int index = vars[j];
				if (translate[index]==0) {
					translate[index] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			int index = vars[posIndex];
			if (translate[index]==0) {
				translate[index] = varIndex;
				//System.out.println(source[posIndex] + " -> " + varIndex);
				varIndex++;
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] renameStraitClauses(ClauseList cl) {
		int varIndex = 1;
		int[] translate = new int[numberOfVariables+1];
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] vars = cl.clauseList.get(i).vars;
			boolean[] isPos = cl.clauseList.get(i).isPos;
			int numberOfNeg = 0;
			int negIndex = 0;
			for(int j=0; j<vars.length; j++) { if (!isPos[j]) { numberOfNeg++; negIndex = j; } }
			if (numberOfNeg!=1) continue;
			//System.out.println(source.length);
			int index = vars[negIndex];
			if (translate[index]==0) {
				translate[index] = varIndex;
				//System.out.println(source[posIndex] + " -> " + varIndex);
				varIndex++;
			}
			for(int j=0; j<vars.length; j++) { 
				if (j == negIndex) continue;
				int index2 = vars[j];
				if (translate[index2]==0) {
					translate[index2] = varIndex;
					//System.out.println(source[j] + " -> " + varIndex);
					varIndex++;
					if (varIndex == numberOfVariables+1) break;
				}
			}
			//System.out.println();
			if (varIndex == numberOfVariables+1) break;
		}
		if (varIndex != numberOfVariables+1) {
			for(int i=varIndex; i<=numberOfVariables; i++) {
				int j = 1; while(translate[j]!=0) j++;
				translate[j] = i;
				//System.out.println(j + " -> " + i);
			}
		}
		return translate;
	}
	private int[] createVariablePairs(ClauseList cl) {
		HashMap<Int2, Integer> count2 = new HashMap<>();
		int bestCount = 1;
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] vars = cl.clauseList.get(i).vars;
			if (vars.length > 12) { continue; }
			if (vars.length == 1) { continue; }
			for(int j=0; j<vars.length-1; j++) {
				for(int k=j+1; k<vars.length; k++) {
					Int2 key2 = new Int2(vars[j], vars[k]);
					bestCount = increaseKey2(count2, key2, bestCount);
				}
			}
		}
		//System.out.println("bestCount: " + bestCount);
		Set<Int2> keys = count2.keySet();
		int[] counts = new int[bestCount];
		for(Int2 key : keys) {
			int count = count2.get(key);
			counts[bestCount-count]++;
		}
		int[] variablePairs = new int[keys.size()*2];
		//System.out.println("bestCount: " + bestCount);
		int[] indeces = new int[bestCount];
		indeces[0] = 0;
		for(int i=1; i<bestCount; i++) { indeces[i] = indeces[i-1] + counts[i-1]*2; }
		for(Int2 key : keys) {
			int count = count2.get(key);
			int i = indeces[bestCount-count];
			variablePairs[i] = key.getA();
			variablePairs[i+1] = key.getB();
			indeces[bestCount-count] += 2;
		}
		return variablePairs;
	}
	private int increaseKey2(HashMap<Int2, Integer> count2, Int2 key2, int bestCount) {
		if (count2.containsKey(key2)) {
			Integer count = count2.get(key2); 
			count++;
			count2.put(key2, count); 
			if (count > bestCount) bestCount = count;
		} 
		else { count2.put(key2, 1); }
		return bestCount;
	}
	private void translateVariables(ClauseList cl, int[] translate) {
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] literals = cl.clauseList.get(i).literals;
			int[] vars = cl.clauseList.get(i).vars;
			boolean[] isPos = cl.clauseList.get(i).isPos;
			for(int j=0; j<literals.length; j++) {
				int var = vars[j];
				int newVar = translate[var];
				if (isPos[j]) literals[j] = newVar; else literals[j] = -newVar;
				vars[j] = newVar;
			}
		}
	}
	private int[] productOfTranslates(int[] translate, int[] oldTranslate) {
		if (oldTranslate == null) return translate;
		int[] product = new int[translate.length];
		for(int i=0; i<product.length; i++) {
			product[i] = translate[oldTranslate[i]];
		}
		return product;
	}
	private void orderLiterals(ClauseList cl) {
		//ClauseList newCL = new ClauseList();
		for(int i=0; i<cl.clauseList.size(); i++) {
			Clause c = cl.clauseList.get(i);
			int[] literals = c.literals;
			int[] vars = c.vars;
			boolean[] isPos = c.isPos;
			int j = 1;
			int flag = 1;
			while (flag != 0) {
				flag = 0;
				for(int k = 0; k<vars.length-j; k++) {
					if (vars[k] > vars[k+1]) {
						int tempL = literals[k];
						literals[k] = literals[k+1];
						literals[k+1] = tempL;
						int tempV = vars[k];
						vars[k]   = vars[k+1];
						vars[k+1] = tempV;
						boolean tempP = isPos[k];
						isPos[k] = isPos[k+1];
						isPos[k+1] = tempP;
						flag = 1;
					}
				}
				j++;
			}
			if (literals.length > 0) c.lastLiteral = literals[literals.length-1];
			if (vars.length > 0) c.lastVarIndex = vars[vars.length-1];;
		}
	}
	private void initDataStructure() {
		clauseListOrderdByLastVarIndexNeg = new ClauseList[numberOfVariables+1];
		clauseListOrderdByLastVarIndexPos = new ClauseList[numberOfVariables+1];
		for(int i=0; i<clauseListOrderdByLastVarIndexPos.length; i++) {
			clauseListOrderdByLastVarIndexPos[i] = new ClauseList();
		}
		for(int i=0; i<clauseListOrderdByLastVarIndexNeg.length; i++) {
			clauseListOrderdByLastVarIndexNeg[i] = new ClauseList();
		}
		indexOfLastVariableOfBestBlackClause = numberOfVariables + 1;
	}
	private void fillDataStructure() {
		for(int i=0; i<cl.clauseList.size(); i++) {
			Clause newClause = cl.clauseList.get(i);
			//System.out.println("i: " + i);
			//System.out.println("newClause: " + newClause);
			//System.out.println("newClause.lastLiteral: " + newClause.lastLiteral);
			//System.out.println("newClause.lastVarIndex: " + newClause.lastVarIndex);
			
			if (newClause.lastLiteral > 0) { clauseListOrderdByLastVarIndexPos[newClause.lastVarIndex].insert(newClause); }
			else { clauseListOrderdByLastVarIndexNeg[newClause.lastVarIndex].insert(newClause); }
			if (newClause.isBlack() && newClause.lastVarIndex < indexOfLastVariableOfBestBlackClause) { 
				indexOfLastVariableOfBestBlackClause = newClause.lastVarIndex;
				bestBlackClause = newClause;
			}
		}
	}
	//          API
	public ClauseSet getClauseSet() { 
		return new ClauseSet(numberOfVariables,
							 indexOfLastVariableOfBestBlackClause,
							 bestBlackClause,
							 clauseListOrderdByLastVarIndexNeg,
							 clauseListOrderdByLastVarIndexPos); 
	}
	public ArrayList<int[]> getClauseSetAsArrayListOfIntArray() {
		ArrayList<int[]> cs = new ArrayList<>();
		for(int i=0; i<cl.clauseList.size(); i++) {
			int[] literals = cl.clauseList.get(i).literals;
			cs.add(literals);
		}
		return cs;
	}
}
*/