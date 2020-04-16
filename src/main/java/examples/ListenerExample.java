package examples;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import uk.sparkydiscordbot.api.entities.event.EventHandler;
import uk.sparkydiscordbot.api.entities.event.EventListener;
import uk.sparkydiscordbot.api.event.server.member.MemberChatEvent;

public class ListenerExample implements EventListener {

    @EventHandler
    public void  onMemeberMessage(final MemberChatEvent event) {

        final Member member = event.getMember();
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();

        if (message.getContentRaw().equals("Hi, Sparky!")) {
            channel.sendMessage(String.format("Hello, %s!", member.getEffectiveName()));
        }

    }

}
