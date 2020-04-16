package uk.sparkydiscordbot.api.entities.economy.account;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.cache.Saveable;
import uk.sparkydiscordbot.api.entities.economy.Transaction;

public interface Account extends Saveable {

    /**
     * @return The owner of this {@link Account}'s user id
     */
    long getAccountOwnerId();

    /**
     * @return The {@link Guild} id this account belongs to
     */
    long getGuildId();

    /**
     * @return The balance of this {@link Account}
     */
    long getAccountBalance();

    /**
     * Transfer money between {@link Account}'s
     *
     * @param recipient The {@link Account} to transfer to
     * @param amount    The amount to transfer
     *
     * @return The {@link Transaction}
     *
     * @throws IllegalArgumentException If the amount is less than 0
     * @throws IllegalArgumentException If the account holders are the same
     * @throws IllegalArgumentException If the transfer is between two different account types
     */
    Transaction transfer(@NotNull(value = "recipient cannot be null") final Account recipient,
                         final long amount) throws IllegalArgumentException;

    /**
     * Add money into this {@link Account}
     *
     * @param amount The amount to deposit
     *
     * @return The {@link Transaction}
     *
     * @throws IllegalArgumentException If the amount is less than 0
     */
    Transaction deposit(final long amount) throws IllegalArgumentException;

    /**
     * Withdraw money from this {@link Account} into the abyss
     *
     * @param amount The amount to withdraw
     *
     * @return The {@link Transaction}
     *
     * @throws IllegalArgumentException If the amount is less than 0
     */
    Transaction withdraw(final long amount) throws IllegalArgumentException;

}
