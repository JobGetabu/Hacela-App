package com.job.hacelaapp.appExecutor;

import java.util.concurrent.Future;

/**
 * Created by Job on Tuesday : 4/24/2018.
 */
public class CancelTaskSample {

    /*
     * Get the future of the task by submitting it to the pool
     */
    Future future = DefaultExecutorSupplier.getInstance().forBackgroundTasks()
            .submit(new Runnable() {
                @Override
                public void run() {
                    // do some background work here.
                }
            });

    /*
     * cancelling the task
     */

    //use case
    private void useCase(){
        future.cancel(true);
    }
}
