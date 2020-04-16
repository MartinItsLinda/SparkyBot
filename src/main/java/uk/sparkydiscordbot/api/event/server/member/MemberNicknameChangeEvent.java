package uk.sparkydiscordbot.api.event.server.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class MemberNicknameChangeEvent extends MemberEvent {

    private final String oldNickname;
    private final String newNickname;

    public MemberNicknameChangeEvent(final Member member,
                                     final Guild server,
                                     final String oldNickname,
                                     final String newNickname,
                                     final JDA jda) {
        super(member, member.getUser(), server, jda);
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
    }

    public String getOldNickname() {
        return this.oldNickname;
    }

    public String getNewNickname() {
        return this.newNickname;
    }
}
