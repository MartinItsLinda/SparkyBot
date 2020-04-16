package uk.sparkydiscordbot.api.entities.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * @return {@code true} if the {@link Event} is ignoring cancelled, {@code false} otherwise
     */
    boolean ignoreCancelled() default false;

}
