package coProject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctToArm extends BBLCompiler {
	static String a = "";

	public FunctToArm() {
		super(a);
	}

	public void performif(Matcher ifStatementMatcher, String[] lines, int i) {

		// If this is not the first "iza" statement encountered, add a branch to this if
		if (flag != 0) {
			allcode += "\tB Else" + elses + "\n";
			// Add a label for the else block
			allcode += "Else" + elses + ":\n";
			// Increment the elses variable
			elses++;
		}
		// Get the two variables being compared in the "iza" statement
		String variable1 = ifStatementMatcher.group(2);
		String variable2 = ifStatementMatcher.group(3);
		// Search for the key corresponding to the first variable in the dc1 map
		String key = "";
		try {

			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable1)) {
					key = entry.getKey();
					break;
				}
			}
 			// Check if the key exists in the dc1 map and throw an exception if it doesn't
			valueTest(key);
			// Generate the ARM assembly code for the comparison between the two variables
			armCode = "\tCMP " + key + ", #0x" +Integer.toHexString(Integer.parseInt(variable2)) + "\n";
			allcode += armCode;
			flag = 0;}
		catch (NoValue e) {
			allcode += e.toString() + "\n";
		}
		catch (NumberFormatException e) {//if the comparison is not between variable and constant then search for register
			String keyg = "";
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable2)) {
					keyg = entry.getKey();
					break;
				}
		}
			armCode = "\tCMP " + key + ", " + keyg + "\n";// compare 2 registers
			allcode+=armCode;
			flag = 0;}
		
		
		
		
			// Reset the flag variable to 0
			while (flag == 0) {
				// Loop through the remaining lines to check if there are more "iza" statements

				for (int j = i + 1; j < lines.length; j++) {
					if (lines[j].contains("iza") || lines[j].contains("aw")) {
						// If another "iza" statement is found, set the flag to 1 and break out of the
						// loop
						flag = 1;
						break;
					}
				}
				break;
			}
			// If another "iza" statement was found, add a branch instruction to the next
			// else block in case this if fails
			if (flag == 1) {
				armCode = "\tBNE Else" + elses + "\n";
				allcode += armCode;
			}

			// If no more "iza" statements were found, add a branch instruction to the end
			// of the code
			else if (flag == 0) {
				armCode = "\tBNE END\n";
				allcode += armCode;
			}
			// Set the flag variable to 1
			flag = 1;
		}
		// If the key does not exist in the dc1 map, catch the exception and add an
		// error message to the generated code

	

	public void performvariable(Matcher variableDeclarationMatcher) {
		try {
			// Check if there's enough memory for the variable
			memoryLimit(counter);
			// Extract the variable name and value from the input
			String variableName = variableDeclarationMatcher.group(2);
			int variableValue = Integer.parseInt(variableDeclarationMatcher.group(3));
			// Check if the variable has been instantiated before
			if (dc1.containsValue(variableName)) {
				// If so, find the register that holds the variable's value
				String keyr = "";
				for (Map.Entry<String, String> entry : dc1.entrySet()) {
					if (entry.getValue().equals(variableName)) {
						keyr = entry.getKey();
						break;
					}
				}
				// Generate ARM assembly code to update the variable's value
				armCode = "/* Variable have been instantiated before,value will be changed*/\n";
				armCode += "\tMOV " + keyr + ", #0x" + Integer.toHexString(variableValue) + "\n";
				allcode += armCode;
				// Update the variable value maps
				dc.replace("R" + counter, variableValue);

			} else {
				// Generate ARM assembly code for new variable declaration
				armCode = "\tMOV R" + counter + ", #0x" + Integer.toHexString(variableValue) + "\n";
				allcode += armCode;
				// Update the register and variable maps
				dc.replace("R" + counter, variableValue);
				dc1.replace("R" + counter, variableName);
				counter++;
			}
		}
		// Handle the exception if there's not enough memory for the variable
		catch (NoMoreMemoryException e) {
			allcode += e + "\n";
		}
	}

	public void performarithm(Matcher arithmeticExpressionMatcher) {
		// get the values of the variables and the operator from the matched expression
		String variable1 = arithmeticExpressionMatcher.group(2);
		String variable2 = arithmeticExpressionMatcher.group(3);
		String operator = arithmeticExpressionMatcher.group(4);
		String variable3 = arithmeticExpressionMatcher.group(5);
		String key1 = "";
		String key2 = "";
		String key3 = "";

		try { // get the key for variable1 from the data structure dc1
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable1)) {
					key1 = entry.getKey();
					break;
				}
			}
			// get the key for variable2 from dc1
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable2)) {
					key2 = entry.getKey();
					break;
				}
			}
			// test if the values of key1 and key2 are valid
			valueTest(key1);
			valueTest(key2);
			// create and append the appropriate ARM code based on the operator and the
			// value of variable3
			if (operator.equals("+")) {
				armCode = "\tADD " + key1 + ", " + key2 + ",#0x" + Integer.toHexString(Integer.parseInt(variable3))
						+ "\n";
				//update registers value
				dc.replace(key1,(dc.get(key2)+Integer.parseInt(variable3)));
				
			}
			if (operator.equals("-")) {
				armCode = "\tSUB " + key1 + ", " + key2 + ",#0x" + Integer.toHexString(Integer.parseInt(variable3))
						+ "\n";
				//update registers value
				dc.replace(key1,(dc.get(key2)-Integer.parseInt(variable3)));
			}
			allcode += armCode;
		}

		// catch an exception if either key1 or key2 is not a valid value in dc1
		catch (NoValue e) {
			allcode += e.tosString() + "\n";
		} catch (NumberFormatException b) {
			// catch if integer of variable 3 cant be formed,then its variable not a value

			// get the key for variable3 from dc1
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable3)) {
					key3 = entry.getKey();
					break;
				}
			}

			// create and append the appropriate ARM code based on the operator
			if (operator.equals("+")) {
				armCode = "\tADD " + key1 + ", " + key2 + ", " + key3 + "\n";
				//update registers value
				dc.replace(key1,(dc.get(key2)+dc.get(key3)));
			}
			if (operator.equals("-")) {
				armCode = "\tSUB " + key1 + ", " + key2 + ", " + key3 + "\n";
				//update registers value
				dc.replace(key1,(dc.get(key2)-dc.get(key3)));
			}
			allcode += armCode;
		}

	}

	public void performelseif(Matcher ifStatmentMatcher,Matcher elseStatmentMatcher,Pattern elseStatmentPattern,String[] lines, int i, Pattern ifStatementPattern,
			Matcher elseifStatmenMatcher) {
		c = 0;
		int q = 0;
		// loop through the remaining lines to find the corresponding "else" block or
		// end of the program
		for (int j = i + 1; j < lines.length; j++) {
			ifStatmentMatcher = ifStatementPattern.matcher(lines[j]);
			elseStatmentMatcher = elseStatmentPattern.matcher(lines[j]);
			if (ifStatmentMatcher.matches()) {
				System.out.println(lines[j]);
				flag2 = 1; // set flag2 to 1 to indicate "else" block is found
				c = j; // save the line number of the "else" block
				break;
			}
		}
		// count the number of "iza" statements before the "else" block
		for (int k = 0; k < c; k++) {
			if (lines[k].contains("iza") || lines[k].contains("aw")) {
				q++;
			}
		}
		// if "else" block is found, add a branch instruction to the end of the current
		// block
		if (flag2 == 1) {
			allcode += "\tB Else" + q + "\n";
			allcode+= "Else" + elses + ":\n";
		}
		// otherwise, branch to the end of the program
		else {
			allcode += "\tB END\n";
		}
		
		// reset variables c and flag2 to 0
		c = 0;
		flag2=0;

		// match variables and values using the regular expression pattern for "else if"
		// statements
		String variable1 = elseifStatmenMatcher.group(2);
		String variable2 = elseifStatmenMatcher.group(3);
		String key = "";
		try {


			// loop through the dictionary and find the key that corresponds to variable1
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable1)) {
					key = entry.getKey();
					break;
				}
			}

			valueTest(key);// call the valuetest function to validate the value of variable1
			// create a label for the "else if" block

				// Generate the ARM assembly code for the comparison between the two variables
				allcode += "\tCMP " + key + ", #0x" +Integer.toHexString(Integer.parseInt(variable2)) + "\n";
			}
			catch (NoValue e) {
				allcode += e.toString() + "\n";
			}
			catch (NumberFormatException e) {//if comparison is between two variable get the other variable register
				String keyg = "";
				for (Map.Entry<String, String> entry : dc1.entrySet()) {
					if (entry.getValue().equals(variable2)) {
						keyg = entry.getKey();
						break;
					}
			}
				allcode += "\tCMP " + key + ", " + keyg + "\n";//e.g: cmp r0,r1
}
			
			
			
			

			if (elses != else1) {
				elses++;
				// if not equal to all if statments in program, branch to the next "if-else"
				// block
				armCode += "\tBNE Else" + elses + "\n";
			}

			// otherwise, branch to the end of the program
			else {
				armCode += "\tBNE END\n";
			}
			allcode += armCode;
		}
		// if the value of variable1 is not valid, catch the exception and append the
		// error message to allcode

	

	public void performchange(Matcher changevalueMatcher) {
		// Extract the variable names from the input and initialize an empty key string
		String variable1 = changevalueMatcher.group(2);
		String variable2 = changevalueMatcher.group(3);
		String key = "";
		// Search for the key in the dc1 map using the variable name as a value
		try {
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable1)) {
					key = entry.getKey();
					break;
				}
			}

			// Check if the key exists in the dc map and is valid
			valueTest(key);
			// Update the value of the key in both dc and dc1 maps
			dc.replace(key, Integer.valueOf(variable2));
			dc1.replace(key, variable1);

			// Concatenate the ARM code string to update the variable value
			allcode += "\tMOV " + key + ", #0x" + Integer.toHexString(Integer.valueOf(variable2)) + "\n";
		}
		// Concatenate the error message to the ARM code string if the key is invalid
		catch (NoValue e) {
			allcode += e.tosString() + "\n";
		} catch (NumberFormatException e) {
			// if integer of variable2 cant be formed it means variable 2 represents
			// variable not integer
			String keyz = null;
			for (Map.Entry<String, String> entry : dc1.entrySet()) {// get the register of variable2
				if (entry.getValue().equals(variable2)) {
					keyz = entry.getKey();
					break;
				}
			}
			dc.replace(key, dc.get(keyz));// replace register of variable 1 with value of register representing variable
											// 2
			allcode += "\tMOV " + key + ", " + keyz + "\n";
		}
	}

	public void performelse(String[] lines, Pattern ifStatementPattern, int i) {

		// initialize variables
		int q = 0;
		// loop through the remaining lines
		for (int j = i + 1; j < lines.length; j++) {
			// check if the line is an if statement
			Matcher ifStatementMatcher1 = ifStatementPattern.matcher(lines[j]);
			if (ifStatementMatcher1.matches()) {
				flag3 = 1;
				c = j;
				break;
			}
		}
		// count the number of iza statements before c
		for (int k = 1; k < c + 1; k++) {
			if (lines[k].contains("iza")) {
				q++;
			}
		}

		// if there is an if statement after the else statement
		if (flag3 == 1) {
			allcode += "\tB Else" + (q + awc) + "\n";
			// add an Else label and increment elses
			allcode += "Else" + elses + ":\n";
			elses++;
		}
		// if there is no if statement after the else statement
		else {
			allcode += "\tB END\n";
			// add an Else label and increment elses
			allcode += "Else" + elses + ":\n";
			elses++;
		}
		// reset variables
		c = 0;
		awc++;
		else1++;
		flag3 = 0;
	}
	
	public void performsignequal(Matcher signequalMatcher) {
		
		String variable1 = signequalMatcher.group(2);
		String operator = signequalMatcher.group(3);
		String variable2 = signequalMatcher.group(4);
		String keyd = "";
		try { // get the key for variable1 from the data structure dc1
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable1)) {
					keyd = entry.getKey();
					break;
				}
			}
			valueTest(keyd);
			if (operator.equals("+")) {//add variable 2 to variable 1 if it is integer
				armCode = "\tADD " + keyd + ", " + keyd + ", #0x" + Integer.toHexString(Integer.parseInt(variable2))
						+ "\n";
				//update registers value
				dc.replace(keyd,(dc.get(keyd)+Integer.parseInt(variable2)));
				
			}
			if (operator.equals("-")) {//subtract variable 2 from variable 1 if it is integer
				armCode = "\tSUB " + keyd + ", " + keyd + ", #0x" + Integer.toHexString(Integer.parseInt(variable2))
						+ "\n";
				//update registers value
				dc.replace(keyd,(dc.get(keyd)-Integer.parseInt(variable2)));
			}
			allcode += armCode;
		}
		catch (NoValue e) {
			allcode += e.tosString() + "\n";
		} catch (NumberFormatException b) {// catch if integer of variable 2 cant be formed,then its variable not a value
			String keyt= "";
			

			// get the key for variable2 from dc1
			for (Map.Entry<String, String> entry : dc1.entrySet()) {
				if (entry.getValue().equals(variable2)) {
					keyt = entry.getKey();
					break;
				}
			}

	
			if (operator.equals("+")) {//add variable 2 to variable 1
				armCode = "\tADD " + keyd + ", " + keyd + ", " + keyt + "\n";
				//update registers value
				dc.replace(keyd,(dc.get(keyd)+dc.get(keyt)));
			}
			if (operator.equals("-")) {//subtract variable 2 from variable 1
				armCode = "\tSUB " + keyd + ", " + keyd + ", " + keyt + "\n";
				//update registers value
				dc.replace(keyd,(dc.get(keyd)-dc.get(keyt)));
			}
			allcode += armCode;
		}

	

	// Checks if the memory limit has been exceeded.
	}public static void memoryLimit(int c) throws NoMoreMemoryException {
		if (c >= 10) {
			throw new NoMoreMemoryException();
		}
	}

	// Throws a novalue exception if the string is empty.
	public static void valueTest(String c) throws NoValue {
		if (c.equals("")) {
			throw new NoValue();
		}
	}


}
