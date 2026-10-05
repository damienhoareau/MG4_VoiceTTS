package android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: classes.dex */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface BroadcastBehavior {
    boolean explicitOnly() default false;

    boolean includeBackground() default false;

    boolean protectedBroadcast() default false;

    boolean registeredOnly() default false;
}
