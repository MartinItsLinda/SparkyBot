package uk.sparkydiscordbot.internal.caching.caches;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.configuration.GuildSettings;
import uk.sparkydiscordbot.internal.caching.EntityLoadingCache;
import uk.sparkydiscordbot.util.MongoUtils;

import java.util.Map;

public class GuildConfigCache extends EntityLoadingCache<String, GuildSettings> {

    public GuildConfigCache(){
        super(new GuildConfigCacheLoader());
    }

    private static final class GuildConfigCacheLoader extends CacheLoader<String, GuildSettings> {

        @Override
        public GuildSettings load(@NotNull(value = "key cannot be null") final String key) {
            final Map<String, Object> map = Maps.newHashMap();
            map.put("server_id", key);

            for (final Document d : MongoUtils.getCollectionByName("server_config").find(Filters.eq("server_id", key))) {
                map.put(d.getString("server_key"), d.get("server_value"));
            }

            return GuildSettings.fromMap(map);
        }

    }

}
