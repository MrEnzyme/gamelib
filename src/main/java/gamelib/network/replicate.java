package gamelib.network;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public @Retention(RUNTIME) @interface replicate
{
    public abstract String value();
}
