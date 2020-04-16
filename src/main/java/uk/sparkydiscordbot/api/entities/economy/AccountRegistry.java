package uk.sparkydiscordbot.api.entities.economy;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.economy.account.Account;

public interface AccountRegistry {

    /**
     * Retrieve the {@link Account} of the {@link User}
     *
     * @param member The owning {@link Member} of the {@link Account}
     *
     * @return The {@link Account} or {@code null} if there is no account
     */
    default Account getAccountOf(@NotNull(value = "member cannot be null") final Member member) {
        return getAccountOf(member.getUser(), member.getGuild());
    }

    /**
     * Retrieve the {@link Account} of the {@link User}
     *
     * @param user   The owning {@link User} of the {@link Account}
     * @param server The {@link Guild}
     *
     * @return The {@link Account} or {@code null} if there is no account
     */
    Account getAccountOf(@NotNull(value = "member cannot be null") final User user,
                         @NotNull(value = "server cannot be null") final Guild server);

}
