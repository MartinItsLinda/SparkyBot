package uk.sparkydiscordbot.api.event.server.member.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MemberMessageUpdateEvent extends MessageEvent<TextChannel> {

    public MemberMessageUpdateEvent(final Guild server,
                                    final Member member,
                                    final TextChannel channel,
                                    final Message message,
                                    final JDA jda) {
        super(member, member.getUser(), server, channel, message, jda);
    }

}
