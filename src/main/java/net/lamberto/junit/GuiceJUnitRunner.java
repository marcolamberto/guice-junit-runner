package net.lamberto.junit;

import static java.util.Arrays.asList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;

import lombok.SneakyThrows;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceJUnitRunner extends BlockJUnit4ClassRunner {
	private Collection<Class<? extends Module>> modules;


	@Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface GuiceModules {
        Class<? extends Module>[] value();
    }


	public GuiceJUnitRunner(final Class<?> testClass) throws InitializationError {
		super(testClass);
    }

    private Injector createInjectorFor(final Collection<Class<? extends Module>> classes) throws InitializationError {
    	return Guice.createInjector(Collections2.transform(classes, new Function<Class<? extends Module>, Module>() {
			@Override
			@SneakyThrows
			public Module apply(final Class<? extends Module> module) {
                return module.newInstance();
			}
		}));
    }

	private Collection<Class<? extends Module>> getModulesFor(final Class<?> module) throws InitializationError {
        final GuiceModules annotation = module.getAnnotation(GuiceModules.class);

        return annotation == null ?
    		null :
			asList(annotation.value());
    }

	private Collection<Class<? extends Module>> getModulesFor(final FrameworkMethod method) throws InitializationError {
        final GuiceModules annotation = method.getAnnotation(GuiceModules.class);

        return annotation == null ?
    		null :
			asList(annotation.value());
    }

	private Collection<Class<? extends Module>> getModulesFor(final FrameworkMethod method, final Class<?> module) throws InitializationError {
		return Optional.fromNullable(getModulesFor(method))
			.or(Optional.fromNullable(getModulesFor(module))
				.or(Collections.<Class<? extends Module>>emptyList()));
	}

	@Override
	protected Object createTest() throws Exception {
        return createInjectorFor(modules).getInstance(getTestClass().getJavaClass());
	}

	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
	    try {
			modules = getModulesFor(method, method.getDeclaringClass());

			super.runChild(method, notifier);
		} catch (final InitializationError e) {
			throw new IllegalArgumentException(e);
		}
	}
}
