guice-junit-runner
====

**Continuous Integration:** [![Build Status](https://api.travis-ci.org/marcolamberto/guice-junit-runner.png?branch=master)](https://travis-ci.org/marcolamberto/guice-junit-runner) <br/>

A Guice JUnit Runner

## What is guice-junit-runner?

guice-junit-runner is a JUnit Runner allowing Guice-based testing.
Each test method is running with a clean Injector instance.

## Basic usage

```java
@RunWith(GuiceJUnitRunner.class)
public class GuiceJUnitRunnerTest {
	@Inject
	public MyService service;

	@Test
	public void test() {
		// ...
	}
}
```

## Using custom Guice modules

You can easily add one more modules by using the @GuiceModules annotation.

```java
@RunWith(GuiceJUnitRunner.class)
@GuiceModules(TestModule.class)
public class GuiceJUnitRunnerTest {

	public static class TestModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(MyService.class).to(MyServiceImpl.class);
			// ...
		}
	}


	@Inject
	public MyService service;

	@Test
	@GuiceModules(TestModule2.class)
	public void perTestSpecificModule() {
		// ...
	}

	@Test
	public void test() {
		// ...
	}
}
```
