package coProject;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBLCompiler {
	String gue;
	// Define regular expressions for BBL constructs

	// ra2m z = 42
	String variableDeclarationRegex = "^( {4}|\t)ra2m\\s+(\\w+)\\s*=\\s*(\\d+)\\s*$";

	// iza x = y:
	String ifStatementRegex = "^( {4}|\t)iza\\s+(\\w+)\\s*=\\s*(\\w+)\\s*:$";

	// aw iza i = 487:
	String elseifStatmentRegex = "^( {4}|\t)aw\\s+iza\\s+(\\w+)\\s*=\\s*(\\w+)\\s*:$";

	// aw:
	String elseStatmentRegex = "^( {4}|\t)aw\\s*:$";

	// a = b + c
	String arithmeticExpressionRegex = "^( {4}| {8}|\t|\t\t)(\\w+)\\s*=\\s*(\\w+)\\s*([-+])\\s*(\\w+)\\s*$";

	// anyVar = 42
	String changevalueRegex = "^( {4}| {8}|\t|\t\t)(\\w+)\\s*=\\s*(\\w+)$";
	
	String signequalRegex = "^( {4}| {8}|\t|\t\t)(\\w+)\\s*([-+])\\s*=\\s*(\\w+)\\s*$";
	
	static String allcode = "";
	static String bblCode = "";
	static int counter = 0;
	static int elses = 1;
	static int else1 = 0;
	static int flag = 0;
	static int flag2 = 0;
	static int flag3 = 0;
	static int flag4 = 0;
	static int flag5 = 0;
	static int c = 0;
	static int awc = 0;
	static String armCode = "";
	// Initializing treemaps for the registers,variables and values
	static Map<String, Integer> dc = new TreeMap<>();
	static Map<String, String> dc1 = new TreeMap<>();

	// constructor to initialize the BBL Compiler
	// parameter gue The BBL code to be compiled.
	public BBLCompiler(String g) {
		for (int i = 0; i < 10; i++) {
			String key = "R" + i;
			dc.put(key, null);
			dc1.put(key, null);
		}
		gue = g;
	}

	public String compile() {
		bblCode = gue;
		FunctToArm function = new FunctToArm();

		// Compile the regular expressions
		Pattern variableDeclarationPattern = Pattern.compile(variableDeclarationRegex);
		Pattern ifStatementPattern = Pattern.compile(ifStatementRegex);
		Pattern elseifStatmentPattern = Pattern.compile(elseifStatmentRegex);
		Pattern elseStatmentPattern = Pattern.compile(elseStatmentRegex);
		Pattern changevaluePattern = Pattern.compile(changevalueRegex);
		Pattern arithmeticExpressionPattern = Pattern.compile(arithmeticExpressionRegex);
		Pattern signequalPattern = Pattern.compile(signequalRegex);

		// Parse the BBL code line by line
		String[] lines = bblCode.split("\\r?\\n");

		// Iterates through a list of strings and prints each one to the console. If a
		// string contains the substring "iza",
		// increments the else1 variable.
		for (String line : lines) {
			System.out.println(line);
			if (line.contains("iza") || line.contains("aw")) {
				else1++;
			
			}
			if (line.contains("#tejhiz")) {flag4++;}
			if (line.contains("#barmajeh")) {flag5++;}
			
		}
		if (flag4!= 1 ||flag5!= 1)
		{allcode+="/*sections are not perfectly set,but code will continue functioning\n";}
		
		
		// Match the expressions
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			Matcher variableDeclarationMatcher = variableDeclarationPattern.matcher(line);
			Matcher ifStatementMatcher = ifStatementPattern.matcher(line);
			Matcher elseifStatmenMatcher = elseifStatmentPattern.matcher(line);
			Matcher elseStatmentMatcher = elseStatmentPattern.matcher(line);
			Matcher changevalueMatcher = changevaluePattern.matcher(line);
			Matcher arithmeticExpressionMatcher = arithmeticExpressionPattern.matcher(line);
			Matcher signequalMatcher = signequalPattern.matcher(line);
			
			
			
			// Conversion to ARM

			// Variable declaration
			if (variableDeclarationMatcher.matches()) {
				function.performvariable(variableDeclarationMatcher);
			}

			// iza statement
			else if (ifStatementMatcher.matches()) {
				function.performif(ifStatementMatcher, lines, i);
			}

			// Arithmetic Expression
			else if (arithmeticExpressionMatcher.matches()) {
				function.performarithm(arithmeticExpressionMatcher);

			}
			// +=/-= statment:
			else if (signequalMatcher.matches()) {
				function.performsignequal(signequalMatcher);
			}

			// Aw iza Statemnt:
			else if (elseifStatmenMatcher.matches()) {
				function.performelseif(ifStatementMatcher,elseStatmentMatcher,elseStatmentPattern ,lines, i, ifStatementPattern, elseifStatmenMatcher);
			}

			// N2lob l Variables:
			else if (changevalueMatcher.matches()) {
				function.performchange(changevalueMatcher);
			}
			// aw statment:
			else if (elseStatmentMatcher.matches()) {
				function.performelse(lines, ifStatementPattern, i);
			}

			else if (line.equals("#tejhiz") || line.equals("#barmajeh")) {
			} else {
				System.out.println("/*line is not accepted by the coding language*/");
				allcode += "/*line is not accepted by the coding language*/\n";
			}
		}

		// Appends "END:" to the end of the allcode string if it does not already end
		// with "END".
		if (allcode.endsWith("END")) {
			allcode += "END:";
		} else {
			allcode += "\tB END\n";
			allcode += "END:";
		}

		System.out.println("---- Compiled Code ----");
		// Additionally, print the code to the console
		System.out.println(allcode);

		return allcode;
	}
}
