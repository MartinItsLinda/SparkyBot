package uk.sparkydiscordbot.internal.economy;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.configuration.GuildSettings;
import uk.sparkydiscordbot.api.entities.economy.AccountRegistry;
import uk.sparkydiscordbot.api.entities.economy.account.Account;
import uk.sparkydiscordbot.util.MongoUtils;

public class AccountRegistryImpl implements AccountRegistry {

    private final MongoCollection<Document> economy;

    public AccountRegistryImpl() {
        this.economy = MongoUtils.getCollectionByName("economy");
    }

    @Override
    public Account getAccountOf(@NotNull(value = "member cannot be null") final User user,
                                @NotNull(value = "server cannot be null")  final Guild server) {

        final AccountType type = this.getAccountType(server);
        Document result = this.economy.find(this.getSearchQuery(user, server, type)).first();
        if (result == null) {

            result = new Document();
            result.put("account_type", type.getTypeName());
            result.put("server_id", type == AccountType.LOCAL ? server.getId() : null);
            result.put("user_id", user.getId());
            result.put("balance", 0L);

            this.economy.insertOne(result);

        }

        //noinspection ConstantConditions
        return new AccountImpl(AccountType.getByName(result.getString("account_type")), user, server, result.getLong("balance"));
    }

    @Override
    public Account getAccountByType(@NotNull(value = "member cannot be null") final User user,
                                    @NotNull(value = "server cannot be null") final Guild server,
                                    @NotNull(value = "account type cannot be null") final AccountType type) {

        final Document query = new Document();
        query.put("account_type", type);
        query.put("user_id", user.getId());
        query.put("server_id", type == AccountType.LOCAL ? server.getId() : null);

        final Document result = this.economy.find(query).first();
        if (result == null) {
            return null;
        }

        return new AccountImpl(type, user, server, result.getLong("balance"));
    }

    @Override
    public AccountType getAccountType(@NotNull(value = "server cannot be null") final Guild server) {
        return AccountType.getByName(GuildSettings.getConfig(server).getString("economy"));
    }

    private Document getSearchQuery(final User user, final Guild server, final AccountType type){
        return new Document().append("user_id", user.getId()).append("server_id", type == AccountType.LOCAL ? server.getId() : null).append("account_type", type.getTypeName());
    }

}

