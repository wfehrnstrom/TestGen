package maincode;

import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws IOException, InterruptedException {
		Question q = new Question("");
//		q.question = "Which of the following test data sets would test each possible output for the method?";
//		q.determineType();
		
//		System.out.println(q.codeBody + "\n");
//		q.codeBody = q.replaceOldNamesWithNewNames(q.codeBody, "word", "thing");
		q.codeBody = "int num = 2574;\nint result = 0;\n\nwhile(num > 0)\n{\nresult = result * 10 + num % 10;\nnum /= 10;\n}\nSystem.out.println(result);";
		q.codeBody = q.mutableRandomizer();
		System.out.println("ARRAY:");
		q.printArr(q.generateAnswers());
		//System.out.println(q.replaceOldNamesWithNewNames(q.codeBody, "j", "q"));
	}

}
