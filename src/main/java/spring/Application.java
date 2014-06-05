package spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@ComponentScan
@EnableAutoConfiguration
public class Application implements AsyncConfigurer, SchedulingConfigurer {
    private static final Logger log = LogManager.getLogger(Application.class);
    private static final Logger schedulingLogger =
            LogManager.getLogger(log.getName() + ".[scheduling]");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        log.info("Setting up thread pool task scheduler with 20 threads.");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("task-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setErrorHandler(new MyErrorHandler());
        scheduler.setRejectedExecutionHandler(new MyRejectedExecutionHandler());

        return scheduler;
    }

    @Override
    public Executor getAsyncExecutor() {
        return this.taskScheduler();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        TaskScheduler scheduler = this.taskScheduler();
        registrar.setTaskScheduler(scheduler);
    }

    private static class MyErrorHandler implements ErrorHandler {

        @Override
        public void handleError(Throwable throwable) {
            schedulingLogger.error("Unknown error occurred while executing task.", throwable);
        }
    }

    private static class MyRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            schedulingLogger.error("Execution of task {} was rejected for unknown reasons.");
        }
    }
}
