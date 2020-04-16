package uk.sparkydiscordbot.api.event.module;

import uk.sparkydiscordbot.api.entities.event.Event;
import uk.sparkydiscordbot.api.entities.module.Module;

public class ModuleEvent extends Event {

    private final Module module;

    /**
     * @param module The {@link Module}
     */
    public ModuleEvent(final Module module) {
        this.module = module;
    }

    /**
     * @return The {@link Module}
     */
    public Module getModule() {
        return this.module;
    }
}
