package uk.sparkydiscordbot.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.entities.utils.Objects;

public class MongoUtils {

    private MongoUtils() {}

    public static MongoCollection<Document> getCollectionByName(@NotNull(value = "collection cannot be null") final String collection) {
        Objects.checkArgument(!collection.trim().isEmpty(), "collection name cannot be effectively null");
        return Bot.getInstance().getMongoDatabase().getCollection(collection);
    }

    public static MongoCollection<Document> getCollectionByName(@NotNull(value = "collection names cannot be null") final String collection,
                                                             @NotNull(value = "database cannot be null") final MongoDatabase database) {
        Objects.checkArgument(!collection.isEmpty(), "collection names cannot be effectively null");
        return database.getCollection(collection);
    }

}
