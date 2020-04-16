package uk.sparkydiscordbot.api.entities.economy;

import net.dv8tion.jda.api.entities.User;
import uk.sparkydiscordbot.api.entities.economy.account.Account;
import uk.sparkydiscordbot.api.entities.utils.Objects;

/**
 * Represents a transfer of funds from one {@link Account} to another
 */
public class Transaction {

    private final Account recipient;
    private final Account donor;

    private long transactionAmount;
    private FailReason reason;

    /**
     * Create a new {@link Transaction}
     *
     * @param donor     The {@link Account} this {@link Transaction} was made from
     * @param recipient The {@link Account} this {@link Transaction} was made recipient
     */
    public Transaction(final Account donor,
                       final Account recipient) {
        this(donor, recipient, 0);
    }

    /**
     * Create a new {@link Transaction}
     *
     * @param donor             The {@link Account} this {@link Transaction} was made from
     * @param recipient         The {@link Account} this {@link Transaction} was made to
     * @param transactionAmount The amount transferred
     */
    public Transaction(final Account donor,
                       final Account recipient,
                       final long transactionAmount) {
        this.donor = donor;
        this.recipient = recipient;
        this.transactionAmount = transactionAmount;
    }

    /**
     * @return The {@link Account} this {@link Transaction} was made from
     */
    public Account getDonor() {
        return this.donor;
    }

    /**
     * @return The receiving {@link User} of this {@link Transaction}, or {@code null} if this {@link Transaction} was
     * made to the bot account
     */
    public Account getRecipient() {
        return this.recipient;
    }

    /**
     * @return The amount transferred
     */
    public long getTransactionAmount() {
        return this.transactionAmount;
    }

    /**
     * @param transactionAmount The amount transferred
     *
     * @throws IllegalArgumentException If the transaction amount is less than 0
     */
    public void setTransactionAmount(final long transactionAmount) throws IllegalArgumentException {
        Objects.checkArgument(transactionAmount >= 0, "cannot transfer negative");
        this.transactionAmount = transactionAmount;
    }

    /**
     * @return {@code true} if there is no {@link FailReason}
     */
    public boolean isSuccess() {
        return this.reason == null;
    }

    /**
     * @return The localized error message
     */
    @Deprecated
    public String getError() {
        return this.reason.getLocaleMessage();
    }

    /**
     * @return The {@link FailReason} that prevented this {@link Transaction} from being successful, or {@code null}
     * if this {@link Transaction} was successful.
     */
    public FailReason getFailReason() {
        return this.reason;
    }

    /**
     * Set the {@link FailReason} that prevented this {@link Transaction} from being successful.
     *
     * @param reason The {@link FailReason}
     */
    public void setFailReason(final FailReason reason) {
        this.reason = reason;
    }

    public enum FailReason {

        INSUFFICIENT_FUNDS("transactionInsufficientFunds"),
        RECIPIENT_TOO_RICH("transactionRecipientTooRich"),
        CANNOT_TRANSFER_NEGATIVE("transactionCannotTransferNegative");

        private final String localeMessage;
        FailReason(final String localeMessage) {
            this.localeMessage = localeMessage;
        }

        /**
         * @return The locale message for this {@link FailReason}
         */
        public String getLocaleMessage() {
            return this.localeMessage;
        }
    }

}
