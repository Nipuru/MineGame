package top.nipuru.minegame.server.game.chat;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import top.nipuru.minegame.common.message.FragmentMessage;
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.server.game.chat.formatter.*;
import top.nipuru.minegame.server.game.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class MessageFormat {

    private static final List<MessageFormatter> formatters = new ArrayList<>();
    private static final MessageFormatter plainFormatter = new PlainMessageFormatter();
    private static final int plainFormatterIdx = 0;

    private MessageFormat() {}

    public static FragmentMessage[] parse(GamePlayer sender, String message) {
        Int2ObjectOpenHashMap<Map.Entry<Integer, FragmentMessage>> index2Fragment = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < formatters.size(); i++) {
            if (!(formatters.get(i) instanceof MessagePattern messagePattern)) continue;
            Matcher matcher = messagePattern.getPattern().matcher(message);
            while (matcher.find()) {
                String[] args = new String[matcher.groupCount()];
                for (int j = 0; j < args.length; j++) {
                    args[j] = matcher.group(j + 1);
                }
                Fragment fragment = messagePattern.parse(sender, args);
                if (fragment == null) {
                    matcher.region(matcher.start() + 1, matcher.regionEnd());
                    continue;
                }
                FragmentMessage fragmentMessage = new FragmentMessage(i, fragment.args());
                index2Fragment.putIfAbsent(matcher.start(), Map.entry(matcher.end(), fragmentMessage));
            }
        }
        List<FragmentMessage> fragments = new LinkedList<>();
        int start = 0;
        for (int i = 0; i < message.length(); i++) {
            Map.Entry<Integer, FragmentMessage> entry = index2Fragment.get(i);
            if (entry == null) continue;
            Fragment plainFragment = plainFormatter.parse(sender, message.substring(start, i));
            if (plainFragment != null) fragments.add(new FragmentMessage(plainFormatterIdx, plainFragment.args()));
            fragments.add(entry.getValue());
            start = entry.getKey();
            i = start - 1;
        }
        if (start < message.length()) {
            Fragment plainFragment = plainFormatter.parse(sender, message.substring(start));
            if (plainFragment != null) {
                fragments.add(new FragmentMessage(plainFormatterIdx, plainFragment.args()));
            }
        }

        return fragments.toArray(new FragmentMessage[0]);
    }

    public static Component format(PlayerInfoMessage sender, GamePlayer receiver, FragmentMessage[] fragments) {
        TextComponent.Builder builder = Component.text();
        for (FragmentMessage fragment : fragments) {
            MessageFormatter formatter = formatters.get(fragment.formatterIdx);
            Component component = formatter.format(sender, receiver, new Fragment(fragment.args));
            builder.append(component);
        }
        return builder.build();
    }

    static {
        formatters.add(plainFormatterIdx, plainFormatter);
        formatters.add(new ShowItemMessageFormatter());
    }
}
