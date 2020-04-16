package examples;

import com.google.common.collect.Maps;
import com.moandjiezana.toml.Toml;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.module.Metrics;
import uk.sparkydiscordbot.api.entities.module.Module;
import uk.sparkydiscordbot.api.entities.module.ModuleData;

import java.util.Map;

@ModuleData(name = "ModuleExample", version = "1.0")
public class ModuleExample extends Module {

    @Override
    public void onEnable() {

        this.getCommandRegistry().register(Command.builder()
                        .names("hello", "hi")
                        .executor(new CommandExample())
                        .build());

        this.getEventRegistry().register(new ListenerExample());

        this.getMetrics().addGraph(Metrics.Graph.of("My-Graph", () -> {

            final Map<String, Integer> map = Maps.newHashMap();

            //Normally you would use more useful things but this is just for the sake of an example

            map.put("hellos", 52);
            map.put("goodbyes", 14);

            return map;

        }));

        //configs are auto created if they're not present in your modules data folder
        //you can get your modules data folder with this.getDataFolder();
        //configs will only be created if there is a config.toml inside the built jar
        final Toml toml = this.getConfig();

        final boolean sayHello = toml.getBoolean("say-hello");
        if (sayHello) {
            //loggers are created on module initialization, we use slf4j
            this.getLogger().info("Hello Admin!");

            this.getScheduler().runTaskRepeating(() -> this.getLogger().info("Another hello to my beloved admins!"), 10L, 10_000L);

        }

    }

    @Override
    public void onDisable() {

    }

}
