/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.language.java;

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.SourceCodeWriter;

/**
 * A {@link SourceCodeWriter} that writes {@link SourceCode} in Java.
 *
 * @author Andy Wilkinson
 * @author Matt Berteaux
 * @author Tad Sanden
 */
public class JavaSourceCodeWriter extends JavaCodeWriter {

	public JavaSourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
		super(indentingWriterFactory);
	}

	protected void writeTopTypeDeclaration(IndentingWriter writer, JavaTypeDeclaration type) {
		writer.print("class " + type.getName());
		if (type.getExtends() != null) {
			writer.print(" extends " + getUnqualifiedName(type.getExtends()));
		}
		writer.println(" {");
		writer.println();
	}

	protected void writeMethodDeclaration(IndentingWriter writer, JavaMethodDeclaration methodDeclaration) {
		writeAnnotations(writer, methodDeclaration);
		writeModifiers(writer, METHOD_MODIFIERS, methodDeclaration.getModifiers());
		writer.print(getUnqualifiedName(methodDeclaration.getReturnType()) + " " + methodDeclaration.getName() + "(");
		List<Parameter> parameters = methodDeclaration.getParameters();
		if (!parameters.isEmpty()) {
			writer.print(parameters.stream()
					.map((parameter) -> getUnqualifiedName(parameter.getType()) + " " + parameter.getName())
					.collect(Collectors.joining(", ")));
		}
		writer.println(") {");
		writer.indented(() -> {
			List<JavaStatement> statements = methodDeclaration.getStatements();
			for (JavaStatement statement : statements) {
				if (statement instanceof JavaExpressionStatement) {
					writeExpression(writer, ((JavaExpressionStatement) statement).getExpression());
				}
				else if (statement instanceof JavaReturnStatement) {
					writer.print("return ");
					writeExpression(writer, ((JavaReturnStatement) statement).getExpression());
				}
				writer.println(";");
			}
		});
		writer.println("}");
		writer.println();
	}

	private void writeExpression(IndentingWriter writer, JavaExpression expression) {
		if (expression instanceof JavaMethodInvocation) {
			writeMethodInvocation(writer, (JavaMethodInvocation) expression);
		}
	}

	private void writeMethodInvocation(IndentingWriter writer, JavaMethodInvocation methodInvocation) {
		writer.print(getUnqualifiedName(methodInvocation.getTarget()) + "." + methodInvocation.getName() + "("
				+ String.join(", ", methodInvocation.getArguments()) + ")");
	}

}
