/*
 * Copyright 2012-present the original author or authors.
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

package org.springframework.boot.configurationprocessor;

import java.util.function.BiConsumer;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import org.springframework.boot.configurationprocessor.test.ItemMetadataAssert;
import org.springframework.boot.configurationprocessor.test.RoundEnvironmentTester;
import org.springframework.boot.configurationprocessor.test.TestableAnnotationProcessor;
import org.springframework.core.test.tools.SourceFile;
import org.springframework.core.test.tools.TestCompiler;

/**
 * Base test infrastructure to test {@link PropertyDescriptor} implementations.
 *
 * @author Stephane Nicoll
 * @author Scott Frederick
 */
public abstract class PropertyDescriptorTests {

	protected String createAccessorMethodName(String prefix, String name) {
		char[] chars = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return prefix + new String(chars, 0, chars.length);
	}

	protected ExecutableElement getMethod(TypeElement element, String name) {
		return ElementFilter.methodsIn(element.getEnclosedElements())
			.stream()
			.filter((method) -> ((Element) method).getSimpleName().toString().equals(name))
			.findFirst()
			.orElse(null);
	}

	protected VariableElement getField(TypeElement element, String name) {
		return ElementFilter.fieldsIn(element.getEnclosedElements())
			.stream()
			.filter((method) -> ((Element) method).getSimpleName().toString().equals(name))
			.findFirst()
			.orElse(null);
	}

	protected ItemMetadataAssert assertItemMetadata(MetadataGenerationEnvironment metadataEnv,
			PropertyDescriptor property) {
		return new ItemMetadataAssert(property.resolveItemMetadata("test", metadataEnv));
	}

	protected void process(Class<?> target,
			BiConsumer<RoundEnvironmentTester, MetadataGenerationEnvironment> consumer) {
		TestableAnnotationProcessor<MetadataGenerationEnvironment> processor = new TestableAnnotationProcessor<>(
				consumer, new MetadataGenerationEnvironmentFactory());
		TestCompiler compiler = TestCompiler.forSystem()
			.withProcessors(processor)
			.withSources(SourceFile.forTestClass(target));
		compiler.compile((compiled) -> {
		});
	}

}
