package maincode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

//	  TestGen: A tool for automatically generating intelligent test questions
//	  Author(s): Will Fehrnstrom, Robin Ji, Athan Chan, Tyler Abramson, Aiden O'neil
//	  Copyright (C) 2016 
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
public class Question {
	String rawText;
	public String question;
	String[] answers;
	String codeBody;
	QuestionType type;
	int number;
	//Two possible types that a question can be: mutable, or reorderable.  
	//Mutable types will have their variable names and values changed.  
	//Reorderable types will just have their answers randomized, and 
	//their test number changed.  If it is a I, II, and III type of question, 
	//the method randomize will randomize what I, II, and III are too. 
	static final QuestionType[] reorderTypes = {QuestionType.CONDITIONAL, QuestionType.PURPOSE, QuestionType.MULTIPLEPOSS, QuestionType.CODEANSWER};
	static final QuestionType[] mutableTypes = {QuestionType.TRACE, QuestionType.RECURSION};
	ArrayList<String> varNames = new ArrayList<String>(Arrays.asList("variable", "var", "x", "y", "z", "unknown", "thing", "a", "b", "c"));
	ArrayList<String> stringVals = new ArrayList<String>(Arrays.asList("zebra", "automobile", "plane", "train", "horse", "flames", "trees", "freeze", "vaporize", "earth", "venus", "jupiter", "saturn", "pluto", "neptune", "monkey", "grain", "football", "lacrosse", "thing", "governor", "party", "lemonade", "scramble", "cliffs", "artillery", "puzzle", "machete", "freedom", "floored", "unknown"));
	//Array Structure is the following.  Reorder types, Mutable Types.
	//Within these, structure follows array structure of reorderTypes and mutableTypes.  The keywords are then 
	//in no particular order
	//QUESTION KEYWORDS ARE TO BE USED ON THE QUESTION SEGMENT ONLY
	static final String  questionKeywords[][][] = {{{new String ("What happens"), new String ("which of the following"), new String ("does not compile"), new String ("fixed"), new String ("work as intended"), new String ("can be used"), "test"},
		{new String ("&&"), new String ("||"), new String ("!"), new String ("true"), new String ("false"), new String ("conditional"), new String ("and"), new String ("or")}, 
		{new String ("purpose")}, 
		{new String ("replacement"), new String ("is true")}, 
		{new String ("replacement"), new String ("correctly")}}, 
		//--------------------------------------------------------------
		{{new String ("value"), new String ("output"), new String ("what is"), new String ("printed"), new String ("executes")},
		{new String ("trace"), new String ("tracing")}, 
		{new String ("recursion"), new String ("recursive")}}};
	
	enum QuestionType {
		TRACE, CONDITIONAL, PURPOSE, RECURSION, MULTIPLEPOSS, CODEANSWER
	}
	
	public Question(String rawText){
		this.rawText = rawText;
		System.out.println(rawText);
	}

	public boolean isAMutableType(){
		for(int i = 0; i < mutableTypes.length; i++){
			if(type == mutableTypes[i]){
				return true;
			}
		}
		return false;
	}
	
	public String getAssignment(String body, String varName, int indexOfDec){
		System.out.println("Index of dec:" + indexOfDec);
		System.out.println(body.charAt(indexOfDec));
		int indexOfInit = body.indexOf(varName, indexOfDec + 1);
		System.out.println("Index of init:" + indexOfInit);
		int indexOfEqualsInInit = body.indexOf("=", indexOfInit + 1);
		System.out.println("Index of equals in init: " + indexOfEqualsInInit);
		System.out.println("assignment substring:" + body.substring(indexOfInit, body.indexOf(";", indexOfInit) + 1));
		int indexOfSemicolonInInit = body.indexOf(";", indexOfInit + 1);
		System.out.println("Index of semicolon in init: " + indexOfSemicolonInInit);
		if(indexOfEqualsInInit < indexOfSemicolonInInit && (indexOfEqualsInInit != -1)){
			return body.substring(indexOfInit, body.indexOf(";", indexOfInit) + 1);
		}
		return "-1";
	}
	
	public boolean isLetter(char c){
		if(c < 65 || c > 122 || (c > 90 && c < 97)){
			return false;
		}
		return true;
	}
	
	public String mutableRandomizer(){
		//POSTCONDITION: The variable types of the question should remain constant
		String[] possibleVariableTypes = {"int", "string"};
		String originalText = codeBody;
		for(int i = 0; i < codeBody.length() - 1; i++){
			varChecks:
			for(int j = 0; j < possibleVariableTypes.length; j++){
				System.out.print("Substring at i: " + codeBody.charAt(i) + ", index of i - 1:" + (i - 1) +  "\tindex of ; from i - 2:" + codeBody.indexOf(";", i-2) + "\n");
			System.out.print("Detected variable type at index: ");
				System.out.println(i == codeBody.toLowerCase().indexOf(possibleVariableTypes[j]));
//				System.out.print("Semicolon, newline, or beginning of file immediately before index: ");
				//System.out.println(((i-1 == codeBody.indexOf(";", i-2)) || (i - 1 == codeBody.indexOf("\n", i-2)) || i == 0));
				String decStatement = "";
				if(i == codeBody.toLowerCase().indexOf(possibleVariableTypes[j]) && (i == 0 || (!isLetter(codeBody.charAt(i - 1))))){
					System.out.println(codeBody.indexOf(";", codeBody.indexOf("(", i)));
					System.out.println(codeBody.indexOf(")", codeBody.indexOf("(", i)));
					if(codeBody.indexOf("(", i) != -1 && ((codeBody.indexOf(";", i) > codeBody.indexOf(")", i)))){
						System.out.println(	"It's a method!");
						if(((!codeBody.substring(i, codeBody.indexOf("(", i)).contains("=")) && !codeBody.substring(i, codeBody.indexOf("(", i)).contains(","))){ //It's a method!
							String parameters = "";
							if(codeBody.indexOf(")", i) - codeBody.indexOf("(", i) > 1){
								parameters = codeBody.substring(codeBody.indexOf("(", i) + 1, codeBody.indexOf(")", i));
							}
							else{
								i++;
								break varChecks;
							}
							Random rand = new Random();
							int randIndex = ((int)rand.nextInt(varNames.size()));
							String oldName = codeBody.substring(codeBody.indexOf(" ", i) + 1, codeBody.indexOf("(", i)).trim();
							String newName = varNames.get(randIndex);
							codeBody = replaceOldNamesWithNewNames(codeBody, oldName, newName);
							varNames.remove(randIndex);
							decStatement = parameters + ";";
						}
					}
					else{ //Not a method
						System.out.println("Not a method!");
						decStatement = codeBody.substring(i, codeBody.indexOf(";", i) + 1);
					}
					//System.out.println("declaration statement is:" + decStatement);
					ArrayList<String> assignmentStatements = new ArrayList<String>();
					ArrayList<String> variablesDeclared = new ArrayList<String>();
					ArrayList<Integer> indexOfVarsDeclared = new ArrayList<Integer>();
					boolean initializedWhenDeclared = false;
					if(decStatement.indexOf("=") != -1){ //variable is initialized when declared
						assignmentStatements.add(codeBody.substring(i, codeBody.indexOf(";", i) + 1));
						variablesDeclared.add(codeBody.substring(i, codeBody.indexOf(";", i) + 1));
						System.out.println("Intialized when declared");
						initializedWhenDeclared = true;
					}
					else{  //variable is not initialized when declared
						if(decStatement.indexOf(",") != -1){ // There are multiple variables declared
							System.out.println("multiple variables declared!");
							System.out.println("Dec statement:" + decStatement);
							int l = 0; 
							while(decStatement.indexOf(",", l + 1) != -1 || l != decStatement.length()){ //While there are more commas 
								if(decStatement.indexOf(",", l + 1) != -1){
									//System.out.println("enters with commas first");
									System.out.println(decStatement.indexOf(",", l));
									String varDeclared = decStatement.substring(l + possibleVariableTypes[j].length(), decStatement.indexOf(",", l));
									int numSpaces = amntOfLeadingWhitespace(varDeclared);
									variablesDeclared.add(decStatement.substring(l + possibleVariableTypes[j].length() + numSpaces, decStatement.indexOf(",", l)).trim());
									//indexOfVarsDeclared.add(l + possibleVariableTypes[j].length() + numSpaces);
								}
								else{ //no more commas left
									String varDeclared = decStatement.substring(l + 1, decStatement.length());
									System.out.println("VARIABLE DECLARED:" + varDeclared);
									int numSpaces = amntOfLeadingWhitespace(varDeclared);
									variablesDeclared.add(decStatement.substring(l + 1 + numSpaces, decStatement.length() - 1).trim());
									//System.out.println("l + 1 + number of spaces:" + (l + 1 + numSpaces));
									//indexOfVarsDeclared.add(l + 1 + numSpaces);
								}
								if(decStatement.indexOf(",", l + 1) != -1){
									l = decStatement.indexOf(",", l + 1);
								}
								else{
									l = decStatement.length();
								}
							}			
						}
						else{
							String varName = decStatement.substring(0, decStatement.indexOf(";"));
							variablesDeclared.add(varName);
						}
					}
					for(int k = 0; k < variablesDeclared.size(); k++){
						//System.out.println("Enters k loop");
						String oldName = "";
						String newName = "";
						Random rand = new Random();
						int numSpaces = amntOfLeadingWhitespace(variablesDeclared.get(k));
						indexOfVarsDeclared.add(codeBody.indexOf(variablesDeclared.get(k)) + numSpaces);
						System.out.println("variable declared: " + variablesDeclared.get(k));
						if(!initializedWhenDeclared){
							System.out.println("GETASSIGNMENT:" + getAssignment(codeBody, variablesDeclared.get(k), indexOfVarsDeclared.get(k)));
							if(getAssignment(codeBody, variablesDeclared.get(k), indexOfVarsDeclared.get(k)).equals("-1")){
								oldName = variablesDeclared.get(k);
								int randIndex = ((int)rand.nextInt(varNames.size()));
								newName = varNames.get(randIndex);
								codeBody = replaceOldNamesWithNewNames(codeBody, oldName, newName);
								break varChecks;
							}
							else{
								assignmentStatements.add(getAssignment(codeBody, variablesDeclared.get(k), indexOfVarsDeclared.get(k)));
								oldName = assignmentStatements.get(k).substring(0, assignmentStatements.get(k).indexOf("=")).trim();
							}
						}
						else{
							oldName = assignmentStatements.get(k).substring(assignmentStatements.get(k).indexOf(" "), assignmentStatements.get(k).indexOf("=")).trim(); //initialized when declared
						}
						System.out.println("Old assignment statement:" + assignmentStatements.get(k));
						String assignmentInsertion = changeVariable(possibleVariableTypes[j], assignmentStatements.get(k));
						int indexOfInit = codeBody.indexOf(assignmentStatements.get(k));
						System.out.println("assignment insertion:" + assignmentInsertion + " at index: " + indexOfInit);
						newName = (assignmentInsertion.substring(assignmentInsertion.indexOf(possibleVariableTypes[j]) + possibleVariableTypes[j].length(), assignmentInsertion.indexOf("=")).trim());
						System.out.println("index of initialization:" + indexOfInit);
						codeBody = codeBody.substring(0, indexOfInit) + assignmentInsertion + codeBody.substring(indexOfInit + assignmentStatements.get(k).length());
						codeBody = replaceOldNamesWithNewNames(codeBody, oldName, newName);
						System.out.println("MUTATION:" + codeBody);
					}
				}
			}
		}
		String newText = codeBody;
		codeBody = originalText;
		return newText;
	}
	//Returns assignment statement
	public String changeVariable(String variableType, String assignmentStatement){
		Random rand = new Random();
		String newAssignedVal = "";
		String statement = "";
		String newName = "";
		String modifier = "";
		int randIndex = ((int)rand.nextInt(varNames.size()));
		if(assignmentStatement.toLowerCase().indexOf(variableType) != -1){
			modifier += variableType + " ";
		}
		newName += varNames.get(randIndex);
		varNames.remove(randIndex);
		//Determine whether the value of the assignment statement comes from a function or from a value
		if(assignmentStatement.indexOf("()") != -1){
			//TODO: Go to the function that returns that value
			statement += " " + newName + " = " + assignmentStatement.substring(assignmentStatement.indexOf("=") + 1, assignmentStatement.indexOf(";"));
			return statement;
		}
		else{ //is a value
			String assigned = assignmentStatement.substring(assignmentStatement.indexOf("=") + 1, assignmentStatement.indexOf(";"));
			assigned.trim();
			switch(variableType){
				case "int":
					if(assigned.contains("+")){
						int[] intInfo = parseForInteger(assigned);
						System.out.println("INT-INFO:" + intInfo[0]);
						if(intInfo[0] != -1){ //If there is a valid integer 
							int originalVal = Integer.parseInt(assigned.substring(intInfo[0], intInfo[0] + intInfo[1]).trim());
							int newVal = newValWithinRange(originalVal);
							newAssignedVal += newVal;
						}
					}
					else{
						int originalVal = Integer.parseInt(assignmentStatement.substring(assignmentStatement.indexOf("=") + 1, assignmentStatement.indexOf(";")).trim());
						int newVal = newValWithinRange(originalVal);
						newAssignedVal += newVal;
					}
					break;
				case "string":
					if(assigned.contains("+")){
						if(assigned.contains("\"")){
							String remainder = assigned.substring(assigned.lastIndexOf("\"", assigned.indexOf("+")) + 1);
							System.out.println("ORIGINAL STRING:" + assigned);
							randIndex =  (int)rand.nextInt(stringVals.size());
							String stringVal = "\"" + stringVals.get(randIndex) + "\"";
							newAssignedVal = stringVal + remainder;
						}
					}
					else{
						System.out.println("ORIGINAL STRING:" + assigned);
						randIndex = (int)rand.nextInt(stringVals.size());
						String stringVal = "\"" + stringVals.get(randIndex) + "\"";
						newAssignedVal = stringVal;
					}
			}
		}
		return modifier + newName + " = " + newAssignedVal + ";";
	}
	
	public int amntOfLeadingWhitespace(String str){
		int count = 0;
		for(int i = 0; i < str.length() - 1; i++){
			if(str.charAt(i) == ' '){
				count++;
				str = str.substring(i + 1, str.length());
			}
			else{
				break;
			}
		}
		return count;
	}
	
	//replaceOldNameWithNewNames returns string body that has had all of toBeReplaced replaced by replacement
	public String replaceOldNamesWithNewNames(String body, String toBeReplaced, String replacement){
		System.out.println("REPLACING:" + toBeReplaced + " WITH:" +  replacement);
		int i = body.indexOf(toBeReplaced);
		while((i < body.length()) && i != -1){
			System.out.println("i: " + i);
			int leadCharIndex;
			int endCharIndex;
			if(body.indexOf(toBeReplaced, i) != 0){
				leadCharIndex = body.indexOf(toBeReplaced, i) - 1;
			}
			else{
				//No lead char index;
				leadCharIndex = -1;
			}
			if((body.indexOf(toBeReplaced, i) +  toBeReplaced.length()) != body.length()){
				endCharIndex = body.indexOf(toBeReplaced, i) + toBeReplaced.length();
			}
			else{
				//no end char index
				endCharIndex = -1;
			}
			//If the trailing and leading characters of that specific substringed index are not alphabetic, replace them
			boolean notAtEnds = endCharIndex != -1 && leadCharIndex != -1;
			if(notAtEnds){
				System.out.println(body.toLowerCase().charAt(endCharIndex));
				System.out.println((body.toLowerCase().charAt(endCharIndex) < 97) || (body.toLowerCase().charAt(endCharIndex) > 122));
				System.out.println((body.toLowerCase().charAt(leadCharIndex) < 97) || (body.toLowerCase().charAt(leadCharIndex) > 122));
				System.out.println(((body.toLowerCase().charAt(leadCharIndex) < 97) || body.toLowerCase().charAt(leadCharIndex) > 122) && ((body.toLowerCase().charAt(endCharIndex) < 97) || (body.toLowerCase().charAt(endCharIndex) > 122)));
				if(((body.toLowerCase().charAt(leadCharIndex) < 97) || body.toLowerCase().charAt(leadCharIndex) > 122) && ((body.toLowerCase().charAt(endCharIndex) < 97) || (body.toLowerCase().charAt(endCharIndex) > 122))){
					String substringToBeChanged = body.substring(i,  i + toBeReplaced.length());
					System.out.println("Substring to be changed:" + substringToBeChanged);
					System.out.println("Replacement preview:" + substringToBeChanged.replace(toBeReplaced, replacement) + body.substring(body.indexOf(substringToBeChanged) + substringToBeChanged.length()));
					body = body.substring(0, body.indexOf(substringToBeChanged, i-1)) + substringToBeChanged.replace(toBeReplaced, replacement) +  body.substring(body.indexOf(substringToBeChanged, i) + substringToBeChanged.length());
					System.out.println("Replaced because characters were not alphabetic");
				}
			}
			else if(endCharIndex != -1){ //at beginning
				if(((body.toLowerCase().charAt(endCharIndex) < 97) || (body.toLowerCase().charAt(endCharIndex) > 122))){
					String substringToBeChanged = body.substring(i,  i + toBeReplaced.length());
					body = body.substring(0, body.indexOf(substringToBeChanged)) + substringToBeChanged.replace(toBeReplaced, replacement) +  body.substring(body.indexOf(substringToBeChanged) + substringToBeChanged.length());
					substringToBeChanged.replace(toBeReplaced, replacement);
					System.out.println("Replaced because characters were not alphabetic");
				}
			}
			else if(leadCharIndex != -1){ //at end
				if((body.toLowerCase().charAt(leadCharIndex) < 97) || body.toLowerCase().charAt(leadCharIndex) > 122){
					String substringToBeChanged = body.substring(i,  i + toBeReplaced.length());
					body = body.substring(0, body.indexOf(substringToBeChanged)) + substringToBeChanged.replace(toBeReplaced, replacement) +  body.substring(body.indexOf(substringToBeChanged) + substringToBeChanged.length());
					substringToBeChanged.replace(toBeReplaced, replacement);
					System.out.println("Replaced because characters were not alphabetic");
				}
			}
			else{ //Only name 
				System.out.println(toBeReplaced.length());
				String substringToBeChanged = body.substring(i,  i + toBeReplaced.length());
				body = body.substring(0, body.indexOf(substringToBeChanged, i + 1)) + substringToBeChanged.replace(toBeReplaced, replacement) +  body.substring(body.indexOf(substringToBeChanged, i + 1) + substringToBeChanged.length());
				System.out.println("Replaced because no leading characters or possibly no ending characters present");
			}
			i = body.indexOf(toBeReplaced, i + 1);
		}
		return body;
	}
	
	public int[] parseForInteger(String seq){ //Return [0] index of sequence, [1] length of sequence
		seq = seq.trim();
		int index = -1;
		int length = 0;
		int[] integerIndexAndLength = {index, length};
		boolean continuous = true;
		for(int i = 0; i < seq.length(); i++){ //For all characters in sequence
			if((seq.charAt(i) > 47 && seq.charAt(index) < 58) && continuous){ //Is an integer
				if(index == -1){
					index = i;
				}
				length++;
			}
			else if(seq.charAt(i) == 32){
				if(continuous){
					integerIndexAndLength[0] = index;
					integerIndexAndLength[1] = length;
					return integerIndexAndLength;
				}
				else{
					continuous = true;
					length = 0;
					index = -1;
				}
			}
			else{
				if(parseForLetter(seq.substring(i, i+1)) != -1){
					continuous = false;
					index = -1;
					length = 0;
					integerIndexAndLength[0] = index;
					integerIndexAndLength[1] = length;
					if(seq.indexOf(" ") != -1){
						i = seq.indexOf(" ") - 1;
					}
					else{ //terminate
						return integerIndexAndLength;
					}
				}
			}
		}
		integerIndexAndLength[0] = index;
		integerIndexAndLength[1] = length;
		return integerIndexAndLength;
	}
	
	public int parseForLetter(String str){ //returns first occurrence of a letter character
		int index = -1;
		for(int i = 0; i < str.length(); i++){
			if((str.charAt(i) > 64 && str.charAt(i) < 91) || (str.charAt(i) > 96 && str.charAt(i) < 123)){ //is a letter
				if(index == -1){
					index = i;
				}
			}
		}
		return index;
	}
	
	public String generateAnswer(String questionCode) throws IOException, InterruptedException{
		String questionCodeWrapperStart = "public class temp {\n\tpublic static void main(String[] args){\n";
		String questionCodeWrapperEnd = "\n}\n}";
		questionCode = questionCodeWrapperStart + questionCode + questionCodeWrapperEnd;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		File fpath = new File("/Users/wfehrnstrom/Documents/workspace/TestGen/temp.java");
		FileWriter writer = new FileWriter(fpath);
		BufferedWriter bWrite = new BufferedWriter(writer);
		bWrite.write(questionCode);
		bWrite.close();
		int result = compiler.run(System.in, System.out, System.err, fpath.getAbsolutePath());
		if(result == 0){
			Process process1 = Runtime.getRuntime().exec("java temp");
			BufferedReader in = new BufferedReader(new InputStreamReader(process1.getInputStream(), Charset.forName("UTF-8")));
			BufferedReader err = new BufferedReader(new InputStreamReader(process1.getErrorStream(), Charset.forName("UTF-8")));
			//process1.waitFor();
			System.out.println("Waiting....");
			String line = in.readLine();
			while(in.ready()){
				System.out.println("Data available");
				System.out.println(line);
				line += in.readLine();
			}
			//line += err.readLine();
			while(err.ready()){
				System.out.println(line);
				line += err.readLine();
			}
			return line;
		}
		else{
			System.out.println("Compile operation failed.  Cannot proceed");
			return null;
		}
	}
	
	public String getIncorrectAnswers(){
		return null;
	}
	
	public String getIncorrectAnswer(){
		
		return null;
	}
	
	public String modifyCode(String code){
		String[] conditionals = {"if", "else if", "while", "for"};
		String[] arithmeticOperators = {"==", "!=", "<=", "<", ">=", ">"};
		String[] logicalOperators = {"||", "&&"};
		for(int i = 0; i < code.length(); i++){
			for(int j = 0; j < conditionals.length; j++){
				
			}
		}
		return null;
	}
	
	public String generateAnswers(){
		return null;
	}
	
	public Question generateNewQuestion(){
		QuestionType type = determineType();
		if(type == QuestionType.TRACE){
			//Approach 2
			 String newQuestionCode = mutableRandomizer();
		}
		else{ //Approach 1
			
		}
		return null;
	}
	//Lower index inclusive, upper index non-inclusive
	public int newValWithinRange(int val){
		Random rand = new Random();
		int sign = (int)(rand.nextInt(2));
		System.out.println(sign);
		int newVal = -1;
		if(sign == 0){ //negative
			System.out.println("Sign is negative");
			newVal = (int)(val - (int)rand.nextInt(val + 1));
		}
		else if(sign == 1){
			System.out.println("Sign is positive");
			newVal = (int)(val + (int)rand.nextInt(val + 1));
		}
		return newVal;
	}
	
	public String generateQuestionText(){
		String questionText = "";
		return questionText;
	}
	
	public QuestionType determineType(){
		//Compute largest values in questionKeywords
		int largestAmountOfQuestions = 0;
		for(int i = 0; i < questionKeywords.length; i++){
			int count = questionKeywords[i].length;
			if(count > largestAmountOfQuestions){
				largestAmountOfQuestions = count;
			}
		}
		//Initialize an array with percentage similarities between the question and the questionType 
		double[][] percentageSimilarities = new double[questionKeywords.length][largestAmountOfQuestions - 1];
		//Outer loop controls biasing towards nonMutable methodology or mutable methodology for
		//randomizing the question
		for(int i = 0; i < questionKeywords.length; i++){
			int numOfGeneralTerms = questionKeywords[i][0].length;
			int numberOfGeneralMatches = 0;
			//Loop through the general terms that bias towards a certain
			//algorithm methodology
			for(int q = 0; q < questionKeywords[i][0].length; q++){
				if(question.toLowerCase().indexOf(questionKeywords[i][0][q]) != -1){
					numberOfGeneralMatches++;
				}
			}
			//Iterate through the question types
			for(int j = 1; j < questionKeywords[i].length; j++){
				int totalQuestionTerms = numOfGeneralTerms;
				int questionTypeMatches = numberOfGeneralMatches;
				for(int k = 0; k < questionKeywords[i][j].length; k++){
					if(question.toLowerCase().indexOf(questionKeywords[i][j][k]) != -1){
						questionTypeMatches++;
					}
					totalQuestionTerms++;
				}
				if(questionTypeMatches > totalQuestionTerms){
					System.out.println("Number of matches by approach algorithm exceeds number of cases.");
				}
				percentageSimilarities[i][j - 1] = ((float)questionTypeMatches)/totalQuestionTerms;
			}
		}
		//Finally, determine which questionType matches the question text
		//most closely
		double highestPercentage = 0f;
		//Default to approach 1
		int index = 0;
		int questionIndex = 0;
		for(int i = 0; i < percentageSimilarities.length; i++){
			for(int j = 0 ; j < percentageSimilarities[i].length; j++){
				if(percentageSimilarities[i][j] > highestPercentage){
					highestPercentage = percentageSimilarities[i][j];
					index = i;
					questionIndex = j;
				}
			}
		}
		if(index == 0){
			type = reorderTypes[questionIndex];
			System.out.println("Question match found.  Modifying question based on immutable approach......");
		}
		else if(index == 1){
			type = mutableTypes[questionIndex];
			System.out.println("Question match found.  Modifying question based on mutable approach......");
		}
		else{
			System.out.println("What happened!?!?");
			return null;
		}
		System.out.println("Similarity of question to  question type: " + highestPercentage);
		System.out.println("Type is: " + type);
		return type;
	}
	
	
}
