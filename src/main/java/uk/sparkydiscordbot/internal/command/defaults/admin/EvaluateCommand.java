package uk.sparkydiscordbot.internal.command.defaults.admin;

import com.google.common.base.Stopwatch;
import okhttp3.Request;
import okhttp3.Response;
import uk.sparkydiscordbot.Bot;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.Command;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;
import uk.sparkydiscordbot.api.entities.command.parsers.ArgumentParsers;
import uk.sparkydiscordbot.api.entities.messaging.Messaging;
import uk.sparkydiscordbot.api.entities.utf8.Emoji;
import uk.sparkydiscordbot.api.entities.utils.BotUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class EvaluateCommand implements CommandExecutor {

    /**
     * The script engine we use for eval
     */
    private static final String SCRIPT_ENGINE = "ECMAScript";

    /**
     * How long to wait before we give up and assume the process just ran into some kind of infinite loop
     */
    private static final long EVAL_TIMEOUT_AFTER = 5_000L;

    /**
     * Pastebin pattern so we can use pastebin urls in eval
     */
    private static final Pattern PASTEBIN_PATTERN = Pattern.compile("https://(?:www)pastebin.com/(?:raw/).+$");

    /**
     * An array of imports that will probably be used when executing eval
     */
    private static final String[] EVAL_IMPORTS = new String[]{
            "java.lang",
            "java.io",
            "java.math",
            "java.util",
            "java.util.concurrent",
            "java.time",
            "Packages.net.dv8tion.jda.api",
            "Packages.uk.sparkydiscordbot.api.entities"
    };

    private final ScriptEngine scriptEngine;

    public EvaluateCommand() {
        //Change this to graal in the event we ever move to JDK 11+
        this.scriptEngine = new ScriptEngineManager().getEngineByName(SCRIPT_ENGINE);
        try {
            this.scriptEngine.eval(String.format("var imports = new JavaImporter(%s)", String.join(", ", EVAL_IMPORTS)));
        } catch (final ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {

            this.scriptEngine.put("context", context);
            this.scriptEngine.put("channel", context.getChannel());
            this.scriptEngine.put("member", context.getMember());
            this.scriptEngine.put("user", context.getSender());
            this.scriptEngine.put("server", context.getServer());
            this.scriptEngine.put("message", context.getMessage());
            this.scriptEngine.put("bot", context.getServer().getSelfMember());
            this.scriptEngine.put("jda", context.getJDA());

            String input = args.next(ArgumentParsers.REMAINING_STRING_NO_QUOTE);
            if (input.startsWith("```") && input.endsWith("```")) {
                input = input.substring(3, input.length() - 3);
            } else if (this.isPastebinURL(input)) {
                input = this.getPageContent(this.getAsPastebinUrl(input));
            }

            final String finput = input; //I hate lambda sometimes
            final Stopwatch stopwatch = Stopwatch.createUnstarted();

            final Future<?> future = Bot.CACHED_EXECUTOR_SERVICE.submit(() -> {

                String output;
                Color color;

                boolean success = false;

                try {
                    stopwatch.start();

                    final Object result = this.scriptEngine.eval(String.format("with (imports) { %s }", finput));

                    stopwatch.stop();

                    output = result == null ? "null" : result.toString();
                    color = Color.GREEN;

                    success = true;

                } catch (final ScriptException ex) {
                    output = ex.getMessage();
                    color = Color.RED;
                }

                final Command.Flag flag = args.getFlag("do-output");
                if (!success || (flag != null && flag.is(true))) {
                    Messaging.send(context.getChannel(), Messaging.getLocalEmbedBuilder()
                            .setColor(color)
                            .setTitle("Evaluate")
                            .addField("Input", Messaging.getLocalMessageBuilder().appendCodeBlock(finput, "javascript").build().getContentRaw(), false)
                            .addField("Output", Messaging.getLocalMessageBuilder().appendCodeBlock(output, "javascript").build().getContentRaw(), false)
                            .setFooter(String.format("Completed in %dms", stopwatch.elapsed(TimeUnit.MILLISECONDS)), null).build());
                } else {
                    context.getMessage().addReaction(Emoji.THUMBS_UP).queue();
                }

            });

            Bot.CACHED_EXECUTOR_SERVICE.submit(() -> {

                try {
                    future.get(EVAL_TIMEOUT_AFTER, TimeUnit.MILLISECONDS);
                } catch (final InterruptedException | TimeoutException ignored) {
                    context.reply(context.i18n("evalTaskExceededTimeLimit", EVAL_TIMEOUT_AFTER));
                } catch (final Exception ex) {
                    context.reply(context.i18n("evalTaskThrewError", ex.getMessage()));
                }

                if (!future.isDone()) {
                    future.cancel(true);
                }

            });

        }

        return true;
    }

    /**
     * Checks whether the {@code input} matches the {@link  #PASTEBIN_PATTERN}
     *
     * @param input The input to match
     * @return {@code true} if the provided input matches {@link #PASTEBIN_PATTERN}
     */
    private boolean isPastebinURL(final String input) {
        return PASTEBIN_PATTERN.matcher(input).matches();
    }

    /**
     * Add the /raw/ to a pastebin url if it doesn't contain it
     *
     * @param url The url
     * @return The raw url
     */
    private String getAsPastebinUrl(final String url) {
        if (PASTEBIN_PATTERN.matcher(url).matches()) { //assume it's correct
            return url;
        }
        return "https://pastebin.com/raw/" + url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * Get the content of a page.
     *
     * @param url The url
     * @return The page content as a {@link String}
     */
    @SuppressWarnings("ConstantConditions")
    private String getPageContent(final String url) {

        final Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/javascript")
                .header("Cache-Control", "no-cache")
                .build();

        try (final Response response = Bot.HTTP_CLIENT.newCall(request).execute()) {
            if (response.code() != 200) {
                return null;
            }
            return response.body().string();
        } catch (final IOException ignored) {
        }

        return null;
    }


}
