/*
 * Copyright 2008 Philip van Oosten (Mentoring Systems BVBA)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package longbow.tools;


import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * A functor that can be used to generate source code for transformations.
 * 
 * @author Philip van Oosten
 * 
 */
public class TransformationGenerator {

	private final Map<String, String> inputs, outputs;

	private final Map<String, String> inputClasses, outputClasses;

	private String className;

	private String packageName;

	public TransformationGenerator() {
		inputs = new HashMap<String, String>();
		outputs = new HashMap<String, String>();
		inputClasses = new HashMap<String, String>();
		outputClasses = new HashMap<String, String>();
	}

	/**
	 * Add an input to the transformation that is to be generated
	 * 
	 * @param name
	 *            The name of the input
	 * @param medal
	 *            The medal script for the metadata of the output
	 * @param className
	 *            The fully qualified Java class name that will be used to
	 *            declare the output
	 */
	public void addInput(final String name, final String medal, final String className) {
		// no Class, but String for the third argument, 
		// because it must be possible to build up the dynamic model BEFORE
		// implementing any of the static model.
		if (name == null || medal == null || className == null) {
			throw new IllegalArgumentException("Illegal null value when adding an input");
		}
		inputs.put(name, medal);
		inputClasses.put(name, className);
	}

	/**
	 * Add an output to the transformation that is to be generated
	 * 
	 * @param name
	 *            The name of the output
	 * @param medal
	 *            The medal script for the metadata of the output
	 * @param className
	 *            The fully qualified Java class name that will be used to
	 *            declare the output
	 */
	public void addOutput(final String name, final String medal, final String className) {
		// no Class, but String for the third argument, 
		// because it must be possible to build up the dynamic model BEFORE
		// implementing any of the static model.
		if (name == null || medal == null || className == null) {
			throw new IllegalArgumentException("Illegal null value when adding an output");
		}
		outputs.put(name, medal);
		outputClasses.put(name, className);
	}

	/**
	 * Generate boilerplate code for the transformation.
	 * 
	 * @param out
	 *            The stream to write the source code to (will not be closed)
	 */
	public void generateSourceCode(final PrintStream out) {
		// package declaration
		out.print("package ");
		out.print(packageName);
		out.println(";");
		out.println();

		// import statements
		out.println("import longbow.ContextEvent;");
		out.println("import longbow.DataWrapper;");
		out.println("import longbow.Metadata;");
		out.println("import longbow.Transformation;");
		out.println("import longbow.TransformationContext;");
		out.println("import longbow.metadata.MedalMetadata;");
		out.println();

		// Class declaration
		out.print("public class ");
		out.print(className);
		out.println(" implements Transformation {");

		// input and output declarations
		printInputDeclarations(out);
		printOutputDeclarations(out);

		// Transformation methods
		printStartExecution(out);
		printStopExecution(out);
		printAddToContext(out);
		printContextChange(out);
		printContextRemoved(out);
		printImportData(out);
		printProcessData(out);
		printExportData(out);
		printIsExecutable(out);

		// end of class declaration
		out.println("}");
	}

	/**
	 * Generate a handy piece of source code that can serve as a starting point
	 * to create JUnit4 tests for the transformation
	 * 
	 * @param out
	 *            The stream to write source code to. Will not be closed.
	 */
	public void generateTestCode(final PrintStream out) {

	}

	void setClassName(final String className) {
		this.className = className;
	}

	void setPackageName(final String packageName) {
		this.packageName = packageName;
	}

	private String constantName(final String key, final boolean input) {
		final StringBuilder buf = new StringBuilder(input ? "IN_" : "OUT_");
		for (final char ch : key.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				buf.append('_');
			}
			buf.append(Character.toUpperCase(ch));
		}
		return buf.toString();
	}

	private void printAddToContext(final PrintStream out) {
		out.println("\tpublic void addToContext(final ContextEvent event) {");
		if (!inputs.isEmpty() || !outputs.isEmpty()) {
			out.println("\t\tfinal TransformationContext context = event.getSource();");
			for (final String input : inputs.keySet()) {
				out.println("\t\tcontext.addInput(" + constantName(input, true) + ", " + input + "Metadata);");
			}
			for (final String output : outputs.keySet()) {
				out.println("\t\tcontext.addOutput(" + constantName(output, false) + ", " + output + "Metadata);");
			}
		}
		out.println("\t}\n");
	}

	private void printContextChange(final PrintStream out) {
		out.println("\tpublic void contextChange(final ContextEvent event) {\n\t}\n");
	}

	private void printContextRemoved(final PrintStream out) {
		out.println("\tpublic void contextRemoved(final ContextEvent event) {\n\t}\n");
	}

	private void printExportData(final PrintStream out) {
		out.println("\tpublic void exportData() {");
		for (final String output : outputs.keySet()) {
			out.println("\t\tassert " + output + "Metadata.acceptsData(" + output + ") : \"Export data for output " + output + " in class \"+getClass().getCanonicalName()+\" not accepted\" ;");
			out.println("\t\t" + output + "Wrapper.setData(" + output + ");");
		}
		out.println("\t}\n");
	}

	private void printImportData(final PrintStream out) {
		out.println("\tpublic void importData() {");
		for (final Map.Entry<String, String> entry : inputClasses.entrySet()) {
			out.println("\t\t" + entry.getKey() + " = (" + entry.getValue() + ") " + entry.getKey() + "Wrapper.getData();");
			out.println("\t\tassert " + entry.getKey() + "Metadata.acceptsData(" + entry.getKey() + ") : \"Import data for input " + entry.getKey()
			        + " in class \"+getClass().getCanonicalName()+\" not accepted\" ;");
		}
		out.println("\t}");
	}

	private void printInputDeclarations(final PrintStream out) {
		for (final Map.Entry<String, String> entry : inputs.entrySet()) {
			final String medal = entry.getValue();
			final String inputName = entry.getKey();
			final String cls = inputClasses.get(inputName);
			out.println("public static final String " + constantName(entry.getKey(), true) + " = \"" + entry.getKey() + "\";");
			out.println("private static final Metadata " + inputName + "Metadata = MedalMetadata.parse(\"" + medal + "\");");
			out.println("private DataWrapper " + inputName + "Wrapper;");
			out.println("private " + cls + " " + inputName + ";");
		}
	}

	private void printIsExecutable(final PrintStream out) {
		out.println("\tpublic boolean isExecutable() {");
		out.println("\t\treturn true;");
		out.println("\t}\n");
	}

	private void printOutputDeclarations(final PrintStream out) {
		for (final Map.Entry<String, String> entry : outputs.entrySet()) {
			final String medal = entry.getValue();
			final String outputName = entry.getKey();
			final String cls = outputClasses.get(outputName);
			out.println("public static final String " + constantName(entry.getKey(), false) + " = \"" + entry.getKey() + "\";");
			out.println("private static final Metadata " + outputName + "Metadata = MedalMetadata.parse(\"" + medal + "\");");
			out.println("private DataWrapper " + outputName + "Wrapper;");
			out.println("private " + cls + " " + outputName + ";");
		}
	}

	private void printProcessData(final PrintStream out) {
		out.println("\tpublic void processData() {");
		out.println("\t\t// TODO Auto-generated method stub");
		out.println("\t}\n");
	}

	private void printStartExecution(final PrintStream out) {
		out.println("\tpublic void startExecution(final TransformationContext context) {");
		if (!outputs.isEmpty()) {
			out.println("// initialize outputs");
			for (final Map.Entry<String, String> entry : outputClasses.entrySet()) {
				out.println("\t\t" + entry.getKey() + "Wrapper = context.getOutputWrapper(" + constantName(entry.getKey(), false) + ");");
			}
		}
		if (!inputs.isEmpty()) {
			out.println("// initialize inputs");
			for (final Map.Entry<String, String> entry : inputClasses.entrySet()) {
				out.println("\t\t" + entry.getKey() + "Wrapper = context.getInputWrapper(" + constantName(entry.getKey(), true) + ");");
			}

		}
		out.println("\n\t}\n");
	}

	private void printStopExecution(final PrintStream out) {
		out.println("\tpublic void stopExecution() {");
		for (final String input : inputs.keySet()) {
			out.println("\t\t" + input + " = null;");
			out.println("\t\t" + input + "Wrapper = null;");
		}
		for (final String output : outputs.keySet()) {
			out.println("\t\t" + output + " = null;");
			out.println("\t\t" + output + "Wrapper = null;");
		}
		out.println("\t}\n");
	}

	/**
	 * A small console application that can be used to generate source code for
	 * transformations
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final InputStream in = System.in;
		final PrintStream out = System.out;

		final TransformationGenerator gen = new TransformationGenerator();

		out.println("package name:");
		final Scanner scan = new Scanner(in);
		gen.setPackageName(scan.nextLine());
		out.println("class name (not qualified):");
		gen.setClassName(scan.nextLine());
		out.println("Add input? (Y/n)");
		String answer = scan.nextLine();
		while (!answer.equals("n") && !answer.equals("N")) {
			out.println("Name of input:");
			final String name = scan.nextLine().trim();
			out.println("Medal script for metadata of input:");
			final String medal = scan.nextLine();
			out.println("fully qualified class name of input:");
			final String cls = scan.nextLine();
			gen.addInput(name, medal, cls);
			out.println("Add another input? (Y/n)");
			answer = scan.nextLine();
		}

		out.println("Add output? (Y/n)");
		answer = scan.nextLine();
		while (!answer.equals("n") && !answer.equals("N")) {
			out.println("Name of output:");
			final String name = scan.nextLine().trim();
			out.println("Medal script for metadata of output:");
			final String medal = scan.nextLine();
			out.println("fully qualified class name of output:");
			final String cls = scan.nextLine();
			gen.addOutput(name, medal, cls);
			out.println("Add another output? (Y/n)");
			answer = scan.nextLine();
		}
		final String cwd = new File("").getAbsolutePath();
		out.println("Generated file location? (absolute or relative to " + cwd + ")");
		final String dirname = cwd + "/" + scan.nextLine();
		File dir = new File(dirname);
		final String[] pkg = gen.packageName.split("\\.");
		for (final String d : pkg) {
			dir = new File(dir, d);
		}
		dir.mkdirs();
		dir = new File(dir, gen.className + ".java");
		final PrintStream ps = new PrintStream(dir);
		gen.generateSourceCode(ps);
		ps.close();
	}
}
