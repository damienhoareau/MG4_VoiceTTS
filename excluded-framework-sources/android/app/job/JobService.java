package android.app.job;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
public abstract class JobService extends Service {
    public static final String PERMISSION_BIND = "android.permission.BIND_JOB_SERVICE";
    private static final String TAG = "JobService";
    private JobServiceEngine mEngine;

    public abstract boolean onStartJob(JobParameters jobParameters);

    public abstract boolean onStopJob(JobParameters jobParameters);

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mEngine == null) {
            this.mEngine = new JobServiceEngine(this) { // from class: android.app.job.JobService.1
                @Override // android.app.job.JobServiceEngine
                public boolean onStartJob(JobParameters jobParameters) {
                    return JobService.this.onStartJob(jobParameters);
                }

                @Override // android.app.job.JobServiceEngine
                public boolean onStopJob(JobParameters jobParameters) {
                    return JobService.this.onStopJob(jobParameters);
                }
            };
        }
        return this.mEngine.getBinder();
    }

    public final void jobFinished(JobParameters jobParameters, boolean z) {
        this.mEngine.jobFinished(jobParameters, z);
    }
}
