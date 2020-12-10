/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cameltooling.lsp.internal.completion.camelapplicationproperties;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.junit.jupiter.api.Test;

import com.github.cameltooling.lsp.internal.AbstractCamelKafkaConnectorTest;
import com.github.cameltooling.lsp.internal.CamelLanguageServer;

class CamelKafkaCamelSourcePropertyCompletionTest extends AbstractCamelKafkaConnectorTest {

	@Test
	void testCompletion() throws Exception {
		String text = "connector.class=org.test.kafkaconnector.TestSourceConnector\n"
					+ "camel.source.";
		CamelLanguageServer languageServer = initializeLanguageServer(text);
		List<CompletionItem> completions = getCompletionFor(languageServer, new Position(1, 13)).get().getLeft();
		Optional<CompletionItem> optionCompletion = completions.stream().filter(completion -> "path.aMediumPathOption".equals(completion.getLabel())).findAny();
		assertThat(optionCompletion).isPresent();
		TextEdit textEdit = optionCompletion.get().getTextEdit();
		assertThat(textEdit.getRange()).isEqualTo(new Range(new Position(1, 13), new Position(1, 13)));
		assertThat(textEdit.getNewText()).isEqualTo("path.aMediumPathOption=a default value for medium path option");
	}
	
	@Test
	void testCompletionWithNoDefaultValue() throws Exception {
		String text = "connector.class=org.test.kafkaconnector.TestSourceConnector\n"
					+ "camel.source.";
		CamelLanguageServer languageServer = initializeLanguageServer(text);
		List<CompletionItem> completions = getCompletionFor(languageServer, new Position(1, 13)).get().getLeft();
		Optional<CompletionItem> optionCompletion = completions.stream().filter(completion -> "endpoint.aMediumEndpointOption".equals(completion.getLabel())).findAny();
		assertThat(optionCompletion).isPresent();
		TextEdit textEdit = optionCompletion.get().getTextEdit();
		assertThat(textEdit.getRange()).isEqualTo(new Range(new Position(1, 13), new Position(1, 13)));
		assertThat(textEdit.getNewText()).isEqualTo("endpoint.aMediumEndpointOption=");
	}
	
	@Test
	void testCompletionWithDashedNotation() throws Exception {
		String text = "connector.class=org.test.kafkaconnector.TestSourceConnector\n"
					+ "camel.source.a-high-path-option=test\n"
					+ "camel.source.";
		CamelLanguageServer languageServer = initializeLanguageServer(text);
		List<CompletionItem> completions = getCompletionFor(languageServer, new Position(2, 13)).get().getLeft();
		Optional<CompletionItem> optionCompletion = completions.stream().filter(completion -> "path.a-medium-path-option".equals(completion.getLabel())).findAny();
		assertThat(optionCompletion).isPresent();
		TextEdit textEdit = optionCompletion.get().getTextEdit();
		assertThat(textEdit.getRange()).isEqualTo(new Range(new Position(2, 13), new Position(2, 13)));
		assertThat(textEdit.getNewText()).isEqualTo("path.a-medium-path-option=a default value for medium path option");
	}
	
	@Test
	void testCompletionWithStartedValue() throws Exception {
		String text = "connector.class=org.test.kafkaconnector.TestSourceConnector\n"
					+ "camel.source.pat";
		CamelLanguageServer languageServer = initializeLanguageServer(text);
		List<CompletionItem> completions = getCompletionFor(languageServer, new Position(1, 15)).get().getLeft();
		assertThat(completions).hasSize(2);
		assertThat(completions.get(0).getTextEdit().getRange()).isEqualTo(new Range(new Position(1, 13), new Position(1, 16)));
	}
	
	@Test
	void testNoCompletionWithNoConnectorClass() throws Exception {
		String text = "camel.source.";
		CamelLanguageServer languageServer = initializeLanguageServer(text);
		List<CompletionItem> completions = getCompletionFor(languageServer, new Position(0, 13)).get().getLeft();
		assertThat(completions).isEmpty();
	}
	
	@Test
	void testNoCompletionWithUnkownConnectorClass() throws Exception {
		String text = "connector.class=unknown\n"
					+ "camel.source.";
		CamelLanguageServer languageServer = initializeLanguageServer(text);
		List<CompletionItem> completions = getCompletionFor(languageServer, new Position(1, 13)).get().getLeft();
		assertThat(completions).isEmpty();
	}
	
}
