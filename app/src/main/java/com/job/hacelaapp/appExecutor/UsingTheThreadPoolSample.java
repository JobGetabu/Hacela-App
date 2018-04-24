package com.job.hacelaapp.appExecutor;

/**
 * Created by Job on Tuesday : 4/24/2018.
 */
public class UsingTheThreadPoolSample {

    /*
     * Using it for Background Tasks
     */
    public void doSomeBackgroundWork(){
        DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        // do some background work here.
                    }
                });
    }

    /*
     * do some task at high priority
     */
    public void doSomeTaskAtHighPriority() {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                .submit(new PriorityRunnable(Priority.HIGH) {
                    @Override
                    public void run() {
                        // do some background work here at high priority.
                    }
                });
    }

    /*
     * Using it for Light-Weight Background Tasks
     */
    public void doSomeLightWeightBackgroundWork(){
        DefaultExecutorSupplier.getInstance().forLightWeightBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        // do some light-weight background work here.
                    }
                });
    }

    /*
     * Using it for MainThread Tasks
     */
    public void doSomeMainThreadWork(){
        DefaultExecutorSupplier.getInstance().forMainThreadTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        // do some Main Thread work here.
                    }
                });
    }

}
