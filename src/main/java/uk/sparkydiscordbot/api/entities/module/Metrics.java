package uk.sparkydiscordbot.api.entities.module;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.scheduler.ScheduledTask;
import uk.sparkydiscordbot.api.entities.utils.Objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A Metrics implementation for recording statistics of {@link Module}'s
 */
public final class Metrics {

    private final Module module;
    private final List<Graph> graphs;

    private final Object lock;

    private ScheduledTask task;

    Metrics(@NotNull(value = "module cannot be null") final Module module) {
        this.module = module;
        this.graphs = Lists.newArrayList();
        this.lock = new Object();
    }

    /**
     * @return The {@link Module} that's recording the metrics
     */
    public Module getModule() {
        return this.module;
    }

    /**
     * @return A {@link Collection} of  {@link Graph}'s recorded by the owning {@link Module}
     */
    public Collection<Graph> getGraphs() {
        return Sets.newHashSet(this.graphs);
    }

    /**
     * @param graph The {@link Graph}
     *
     * @throws IllegalArgumentException If the provided {@link Graph} is {@code null}
     * @throws IllegalArgumentException If there is another {@link Graph} with the same names
     */
    public void addGraph(@NotNull(value = "graph cannot be null") final Metrics.Graph graph) throws IllegalArgumentException {
        if (this.graphs.stream().anyMatch(f -> f.getName().equalsIgnoreCase(graph.getName()))) {
            throw new IllegalArgumentException(String.format("Duplicate field for names %s", graph.getName()));
        }
        this.graphs.add(graph);
    }

    @Override
    public String toString() {

        final StringBuilder metrics = new StringBuilder();

        metrics.append("Metrics{module=").append(this.module).append(", graphs=[");

        for (int i = 0; i < this.graphs.size(); i++) {
            if (i != 0) {
                metrics.append(", ");
            }
            metrics.append(this.graphs.get(i));
        }

        metrics.append("]}");

        return metrics.toString();
    }

    /**
     * of and add a new {@link Graph}
     *
     * @param name     The names of the {@link Graph}
     * @param callable The {@link Callable} to execute
     *
     * @return The {@link Graph}
     *
     * @throws IllegalArgumentException If the provided {@code names} is empty
     * @throws IllegalArgumentException If the provided {@code names} is not unique
     */
    public Graph createGraph(@NotNull(value = "names cannot be null") final String name,
                             @NotNull(value = "callable cannot be null") final Callable<Map<String, Integer>> callable) throws IllegalArgumentException {
        return this.createGraph(name, null, callable);
    }

    /**
     * of and add a new {@link Graph}
     *
     * @param name        The names of the {@link Graph}
     * @param description A brief description of this {@link Graph}s purpose
     * @param callable    The {@link Callable} to execute
     *
     * @return The {@link Graph}
     *
     * @throws IllegalArgumentException If the provided {@code names} is empty
     * @throws IllegalArgumentException If the provided {@code names} is not unique
     */
    public Graph createGraph(@NotNull(value = "names cannot be null") final String name,
                             final String description,
                             @NotNull(value = "callable cannot be null") final Callable<Map<String, Integer>> callable) throws IllegalArgumentException {
        Objects.checkArgument(!name.isEmpty(), "names cannot be effectively null");

        final Graph field = new Graph(name, description, callable);

        this.addGraph(field);

        return field;
    }

    /**
     * Graphs are used to record information for statistics
     */
    public static final class Graph {

        private final String name;
        private final String description;
        private final Callable<Map<String, Integer>> callable;

        /**
         * @param name The names of this {@link Graph}
         */
        Graph(@NotNull(value = "names cannot be null") final String name,
              @NotNull(value = "callable cannot be null") final Callable<Map<String, Integer>> callable) {
            this(name, null, callable);
        }

        /**
         * @param name        The names of this {@link Graph}
         * @param description A brief description of this {@link Graph}s purpose
         * @param callable    The {@link Callable} to execute when it comes time to submit this {@link Graph}s data
         */
        Graph(@NotNull(value = "names cannot be null") final String name,
              final String description,
              @NotNull(value = "callable cannot be null") final Callable<Map<String, Integer>> callable) {
            Objects.checkArgument(!name.isEmpty(), "names cannot be effectively null");
            this.name = name;
            this.description = description;
            this.callable = callable;
        }

        /**
         * @return The names of this {@link Graph}
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return A brief description of this {@link Graph} and its purpose, or {@code null} if no description was set
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * @return The {@link Callable} executed when it comes time to submit the data
         */
        public Callable<Map<String, Integer>> getCallable() {
            return this.callable;
        }

        /**
         * of a new {@link Graph} with the chosen {@code names}
         *
         * @param name     The names of this {@link Graph}
         * @param callable The {@link Callable} to execute when it comes time to submit this {@link Graph}s data
         *
         * @return The {@link Graph}
         */
        public static Graph of(@NotNull(value = "names cannot be null") final String name,
                                   @NotNull(value = "callable cannot be null") final Callable<Map<String, Integer>> callable) {
            return new Graph(name, callable);
        }

        /**
         * of a new {@link Graph} with the chosen {@code names} and {@code description}
         *
         * @param name        The names of this {@link Graph}
         * @param description A brief description of this {@link Graph}s purpose
         * @param callable    The {@link Callable} to execute when it comes time to submit this {@link Graph}s data
         *
         * @return The {@link Graph}
         */
        public static Graph of(@NotNull(value = "names cannot be null") final String name,
                                   final String description,
                                   @NotNull(value = "callable cannot be null") final Callable<Map<String, Integer>> callable) {
            return new Graph(name, description, callable);
        }

        @Override
        public String toString() {

            final StringBuilder graph = new StringBuilder();

            graph.append("Graph{names=").append(this.name)
                    .append(", description=").append(this.description)
                    .append(", results = {");
            try {

                int i = 0;
                for (final Map.Entry<String, Integer> entry : this.callable.call().entrySet()) {
                    if (i != 0) {
                        graph.append(",");
                    }
                    graph.append("key=").append(entry.getKey()).append(", value=").append(entry.getValue());
                    i++;
                }

            } catch (final Exception ignored) {
            }

            graph.append("}");
            graph.append("}");

            return graph.toString();
        }
    }

}
