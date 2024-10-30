package top.nipuru.minegame.server.game.chat.formatter;

import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.server.game.chat.Fragment;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.util.NmsMethods;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

public class ShowItemMessageFormatter extends MessagePattern {

    private static final Pattern pattern = Pattern.compile("#展示([0-9]*)#");

    public ShowItemMessageFormatter() {
        super(pattern);
    }

    @Nullable
    @Override
    public Fragment parse(GamePlayer player, String... args) {
        ItemStack itemStack = getItemStack(player, args[0]);
        Serializable data = NmsMethods.serializeIcons(new ItemStack[]{itemStack});
        return new Fragment(data);
    }

    @Override
    public Component format(PlayerInfoMessage sender, GamePlayer receiver, Fragment fragment) {
        byte[] data = fragment.getArg(0);
        ItemStack itemStack = NmsMethods.deserializeIcons(data)[0];
        TextComponent.Builder builder = text();
        builder.append(text("["))
                .append(itemStack.displayName());
        if (itemStack.getAmount() > 1) {
            builder.append(text(" x" + itemStack.getAmount()));
        }
        builder.append(text("]"));
        builder.color(NamedTextColor.WHITE);
        return builder.build();
    }

    public static ItemStack getItemStack(GamePlayer player, String slotString) {
        try {
            int slot = Integer.parseInt(slotString);
            ItemStack itemStack = player.getBukkitPlayer().getInventory().getItem(slot);
            if (itemStack != null) return itemStack;
        } catch (Exception ignored) {
        }
        return new ItemStack(Material.AIR);
    }
}
