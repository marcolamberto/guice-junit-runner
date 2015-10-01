package net.lm.junit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import lombok.Getter;
import net.lm.junit.GuiceJUnitRunner.GuiceModules;
import net.lm.junit.GuiceJUnitRunnerTest.TestModule;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules(TestModule.class)
public class GuiceJUnitRunnerTest {
	private static final String SOME_VALUE = "Some value!";

	@Inject
	private SampleBean sample;

	@Inject
	private Injector injector;

	private static final Collection<Integer> injectors = Sets.newHashSet();

	@Before
	public void setUp() {
		injectors.add(injector.hashCode());
	}

	@AfterClass
	public static void afterClass() {
		assertThat(injectors, hasSize(2));
	}

	@Test
	public void basicUsage() {
		assertThat(sample, is(notNullValue()));
		assertThat(sample.getValue(), is(SOME_VALUE));
	}

	@Test
	public void itShouldBuildANewInjectorForEveryTest() {
		basicUsage();
	}

	public static class TestModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(String.class).annotatedWith(Names.named("some-value")).toInstance(SOME_VALUE);
		}
	}

	@Getter
	public static class SampleBean {
		@Inject @Named("some-value")
		private String value;
	}
}