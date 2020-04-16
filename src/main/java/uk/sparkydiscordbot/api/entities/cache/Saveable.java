package uk.sparkydiscordbot.api.entities.cache;

public interface Saveable {

    /**
     * Checks whether the entity is modified so we know whether it should be committed to the database
     *
     * @return {@code true} if the entity has been modified, {@code false}
     */
    boolean isModified();

    /**
     * Commits the information of this entity to the database
     */
    void save();

}
