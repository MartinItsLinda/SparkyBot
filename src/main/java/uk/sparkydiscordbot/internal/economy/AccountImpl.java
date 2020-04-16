package uk.sparkydiscordbot.internal.economy;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import uk.sparkydiscordbot.api.entities.economy.Transaction;
import uk.sparkydiscordbot.api.entities.economy.Transaction.FailReason;
import uk.sparkydiscordbot.api.entities.economy.account.Account;
import uk.sparkydiscordbot.api.entities.utils.Objects;
import uk.sparkydiscordbot.util.MongoUtils;

//TODO: Spend a day or so rethinking how to do the account System including caching of accounts

public class AccountImpl implements Account {

    private static MongoCollection<Document> economy = MongoUtils.getCollectionByName("economy");

    private final long userId;
    private final long guildId;

    private long balance;
    private long newBalance; //this is so we can compare it to the previous value when we remove it from the cache

    public AccountImpl(final long userId,
                       final long guildId,
                       final long balance) {
        Objects.checkArgument(balance >= 0, "balance cannot be negative");
        this.userId = userId;
        this.guildId = guildId;
        this.balance = this.newBalance = balance;
    }

    @Override
    public long getAccountOwnerId() {
        return this.userId;
    }

    @Override
    public long getGuildId() {
        return this.guildId;
    }

    @Override
    public long getAccountBalance() {
        return this.newBalance;
    }

    @Override
    public Transaction transfer(@NotNull(value = "account cannot be null") final Account recipient,
                                final long amount) throws IllegalArgumentException {
        Objects.checkArgument(amount > 0, "cannot transfer a negative amount");
        Objects.checkArgument(recipient.getAccountOwnerId() == this.userId, "cannot transfer money to the same account");

        final Transaction transaction = new Transaction(this, recipient);

        final long recipientBalance = recipient.getAccountBalance();
        if (recipientBalance >= Long.MAX_VALUE) {
            transaction.setFailReason(FailReason.RECIPIENT_TOO_RICH);
        } else {

            //in the event we get an integer overflow we check for it being less than 0
            final long finalAmount = (this.balance + amount) < 0 ? (Long.MAX_VALUE - this.balance) : amount;

            transaction.setTransactionAmount(finalAmount);

            if (this.balance < finalAmount) {
                transaction.setFailReason(FailReason.INSUFFICIENT_FUNDS);
            } else {
                recipient.deposit(transaction.getTransactionAmount());
                withdraw(transaction.getTransactionAmount());
            }

        }

        return transaction;
    }

    @Override
    public Transaction deposit(final long amount) throws IllegalArgumentException {
        Objects.checkArgument(amount > 0, "cannot deposit negative");

        final Transaction transaction = new Transaction(null, this);

        final long accountBalance = this.getAccountBalance();
        if (accountBalance >= Long.MAX_VALUE) {
            transaction.setFailReason(FailReason.RECIPIENT_TOO_RICH);
        } else {

            //should the deposited amount create an integer overflow, take off the difference
            final long finalAmount = (accountBalance + amount) < 0 ? Long.MAX_VALUE : amount;

            transaction.setTransactionAmount(finalAmount);

            this.newBalance = transaction.getTransactionAmount();
        }

        return transaction;
    }

    @Override
    public Transaction withdraw(final long amount) throws IllegalArgumentException {
        Objects.checkArgument(amount > 0, "cannot withdraw negative");

        final Transaction transaction = new Transaction(this, null);

        final long accountBalance = this.getAccountBalance();
        if (accountBalance < amount) {
            transaction.setFailReason(FailReason.INSUFFICIENT_FUNDS);
        } else {
            this.newBalance = transaction.getTransactionAmount();
        }

        return transaction;
    }

    @Override
    public String toString() {
        return String.format("Account{guildId=%d,userId=%d,balance=%d}", this.guildId, this.userId, this.balance);
    }

    @Override
    public boolean isModified() {
        return this.balance == this.newBalance;
    }

    @Override
    public void save() {

    }
}
