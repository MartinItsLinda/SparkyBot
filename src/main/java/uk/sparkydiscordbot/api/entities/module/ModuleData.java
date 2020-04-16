package uk.sparkydiscordbot.api.entities.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleData {

    /**
     * @return The unique names of the {@link Module}
     */
    String name();

    /**
     * @return The current version string of the {@link Module}
     */
    String version();

}
