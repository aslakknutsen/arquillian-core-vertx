package org.arquillian.vertx.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.arquillian.vertx.test.core.ArquillianAdapter;
import org.arquillian.vertx.test.event.TestEvents;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

public class Arquillian extends BlockJUnit4ClassRunner {

    private ArquillianAdapter adaptor;

    public Arquillian(Class<?> klass) throws InitializationError
    {
       super(klass);
    }
    
    @Override
    public void run(final RunNotifier notifier)
    {
       State.runnerStarted();

       // first time we're being initialized
       if(!State.hasAdaptor())   
       {
          // no, initialization has been attempted before and failed, refuse to do anything else
          if(State.hasInitializationException())  
          {
             // failed on suite level, ignore children
             //notifier.fireTestIgnored(getDescription());
             notifier.fireTestFailure(
                   new Failure(getDescription(), 
                         new RuntimeException(
                               "Arquillian has previously been attempted initialized, but failed. See cause for previous exception", 
                               State.getInitializationException())));
          }
          else
          {
             try 
             {
                // ARQ-1742 If exceptions happen during boot
                adaptor = ArquillianAdapter.build();
                // don't set it if beforeSuite fails
                adaptor.send(TestEvents.beforeSuite());
                State.setAdaptor(adaptor);
             } 
             catch (Exception e)  
             {
                // caught exception during BeforeSuite, mark this as failed
                State.caughtInitializationException(e);
                notifier.fireTestFailure(new Failure(getDescription(), e));
             }
          }
       }
       notifier.addListener(new RunListener()
       {
          @Override
          public void testRunFinished(Result result) throws Exception
          {
             State.runnerFinished();
             shutdown();
          }

          private void shutdown()
          {
             try
             {
                if(State.isLastRunner())
                {
                   try
                   {
                      if(adaptor != null)
                      {
                         adaptor.send(TestEvents.afterSuite());
                         adaptor.shutdown();
                      }
                   }
                   finally
                   {
                      State.clean();
                   }
                }
                adaptor = null;
             }
             catch (Exception e)
             {
                throw new RuntimeException("Could not run @AfterSuite", e);
             }
          }
       });
       // initialization ok, run children
       if(State.hasAdaptor())
       {
          adaptor = State.getAdaptor();
          super.run(notifier);
       }
    }

    /**
     * Override to allow test methods with arguments
     */
    @Override
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors)
    {
       List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
       for (FrameworkMethod eachTestMethod : methods)
       {
          eachTestMethod.validatePublicVoid(isStatic, errors);
       }
    }
       
    /*
     * Override BeforeClass/AfterClass and Before/After handling.
     * 
     * Let super create the Before/After chain against a EmptyStatement so our newly created Statement
     * only contains the method that are of interest to us(@Before..etc). 
     * They can then optionally be executed if we get expected callback.
     * 
     */
       
    @Override
    protected Statement withBeforeClasses(final Statement originalStatement)
    {
       final Statement onlyBefores = super.withBeforeClasses(new EmptyStatement());
       return new Statement() 
       {
          @Override
          public void evaluate() throws Throwable
          {
             Boolean execute = adaptor.send(TestEvents.beforeClass(Arquillian.this.getTestClass().getJavaClass()));
             if(execute) {
                 onlyBefores.evaluate();
             }
             originalStatement.evaluate();
          }
       };
    }
    
    @Override
    protected Statement withAfterClasses(final Statement originalStatement)
    {
       final Statement onlyAfters = super.withAfterClasses(new EmptyStatement());
       return new Statement() 
       {
          @Override
          public void evaluate() throws Throwable
          {
             multiExecute
             (
                originalStatement,
                new Statement() { 
                    @Override 
                    public void evaluate() throws Throwable 
                    {
                        Boolean execute = adaptor.send(TestEvents.afterClass(Arquillian.this.getTestClass().getJavaClass()));
                        if(execute) {
                            onlyAfters.evaluate();
                        }
                    }
                }
             );
          }
       };
    }   

    @Override
    @SuppressWarnings("deprecation")
    protected Statement methodBlock(final FrameworkMethod method) {
        Object test;
        try {
            test= new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest();
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }
        try
        {
            Method withRules = BlockJUnit4ClassRunner.class.getDeclaredMethod("withRules",
                    new Class[] {FrameworkMethod.class, Object.class, Statement.class});
            withRules.setAccessible(true);

            Statement statement = methodInvoker(method, test);
            statement = possiblyExpectingExceptions(method, test, statement);
            statement = withPotentialTimeout(method, test, statement);

            final Object testObj = test;
            final Statement testStatement = statement;

            Statement arounds = withBefores(method, test, testStatement);
            arounds = withAfters(method, test, arounds);
            arounds = (Statement)withRules.invoke(this, new Object[] {method, test, arounds});
            final Statement withArounds = arounds;
            return new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    List<Throwable> exceptions = new ArrayList<Throwable>();
                    try {
                        Boolean executeBefore = adaptor.send(TestEvents.beforeTest(testObj.getClass(), method.getMethod()));
                        
                        try {
                            State.caughtExceptionAfterJunit(null);
                            if(executeBefore) {
                                withArounds.evaluate();
                            } else {
                                testStatement.evaluate();
                            }
                        }
                        catch (Throwable e) {
                            State.caughtExceptionAfterJunit(e);
                            exceptions.add(e);
                        }
                    } finally {
                        try {
                            Boolean executeAfter = adaptor.send(TestEvents.afterTest(testObj.getClass(), method.getMethod()));
                        }
                        catch(Throwable e) {
                            exceptions.add(e);
                        }
                    }
                    if(exceptions.isEmpty())
                    {
                       return;
                    }
                    if(exceptions.size() == 1)
                    {
                       throw exceptions.get(0);
                    }
                    throw new MultipleFailureException(exceptions);
                }
            };
        } catch(Exception e) {
            throw new RuntimeException("Could not create statement", e);
        }
     }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test)
    {
       return new Statement()
       {
          @Override
          public void evaluate() throws Throwable
          {
//             Object resultObj = adaptor.test(new TestMethodExecutor()
//             {
//                @Override
//                public void invoke(Object... parameters) throws Throwable
//                {
//                   try
//                   {
//                      method.invokeExplosively(test, parameters); 
//                   } 
//                   catch (Throwable e) 
//                   {
//                      // Force a way to return the thrown Exception from the Container the client. 
//                      State.caughtTestException(e);
//                      throw e;
//                   }
//                }
//                
//                public Method getMethod()
//                {
//                   return method.getMethod();
//                }
//                
//                public Object getInstance()
//                {
//                   return test;
//                }
//             });
//             TestResultWrapper result = new TestResultWrapper(resultObj);
//             if(result.getThrowable() != null)
//             {
//                Throwable t = result.getThrowable(); 
//                if(t instanceof InvocationTargetException) {
//                   throw t.getCause();
//                }
//                throw t;
//             }
          }
       };
    }

    /**
     * A helper to safely execute multiple statements in one.<br/>
     * 
     * Will execute all statements even if they fail, all exceptions will be kept. If multiple {@link Statement}s
     * fail, a {@link MultipleFailureException} will be thrown.
     *
     * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
     * @version $Revision: $
     */
    private void multiExecute(Statement... statements) throws Throwable 
    {
       List<Throwable> exceptions = new ArrayList<Throwable>();
       for(Statement command : statements) 
       {
          try
          {
             command.evaluate();
          } 
          catch (Throwable e) 
          {
             exceptions.add(e);
          }
       }
       if(exceptions.isEmpty())
       {
          return;
       }
       if(exceptions.size() == 1)
       {
          throw exceptions.get(0);
       }
       throw new MultipleFailureException(exceptions);
    }
    
    private static class EmptyStatement extends Statement
    {
       @Override
       public void evaluate() throws Throwable
       {
       }
    }
}
