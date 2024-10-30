package top.nipuru.minegame.server.util;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.fixes.References;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NmsMethods {

    public static boolean hasDisconnected(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        return serverPlayer.hasDisconnected();
    }


    //停止接受客户端发包
    public static void freezePlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        Connection connection = serverPlayer.connection.connection;
        UnsafeHolder.setStopReadPacket(connection);
    }

    public static void placeItemBackInInventory(Player player, ItemStack itemStack) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.getInventory().placeItemBackInInventory(CraftItemStack.asNMSCopy(itemStack));
    }

    public static void removeFromPlayerList(Player player) {
        PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        playerList.remove(((CraftPlayer) player).getHandle());
    }

    public static boolean isPlayerFreezing(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        Connection connection = serverPlayer.connection.connection;
        return UnsafeHolder.isStopReadPacket(connection);
    }

    public static int getDataVersion() {
        return CraftMagicNumbers.INSTANCE.getDataVersion();
    }

    public static byte[] serializePersistentDataContainer(PersistentDataContainer persistentDataContainer) {
        try {
            CraftPersistentDataContainer craftPersistentDataContainer = (CraftPersistentDataContainer) persistentDataContainer;
            CompoundTag compoundTag = craftPersistentDataContainer.toTagCompound();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            DataOutputStream dos = new DataOutputStream(gzip);
            compoundTag.write(dos);
            dos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save persistent container.");
        }
    }

    public static void clearPersistentDataContainer(PersistentDataContainer persistentDataContainer) {
        CraftPersistentDataContainer craftPersistentDataContainer = (CraftPersistentDataContainer) persistentDataContainer;
        craftPersistentDataContainer.clear();
    }

    public static void deserializePersistentDataContainer(PersistentDataContainer persistentDataContainer, byte[] data) {
        try {
            CraftPersistentDataContainer craftPersistentDataContainer = (CraftPersistentDataContainer) persistentDataContainer;
            craftPersistentDataContainer.clear();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            DataInputStream dis = new DataInputStream(gzip);
            CompoundTag compoundTag = CompoundTag.TYPE.load(dis, NbtAccounter.unlimitedHeap());
            craftPersistentDataContainer.putAll(compoundTag);
            dis.close();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load persistent container.");
        }
    }


    public static byte[] serializeIcons(ItemStack[] itemStacks) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            DataOutputStream dos = new DataOutputStream(gzip);
            ListTag listTag = new ListTag();
            int version = getDataVersion();
            for (ItemStack itemStack : itemStacks) {
                CompoundTag compoundTag = new CompoundTag();
                if (itemStack != null) {
                    CraftItemStack.asNMSCopy(itemStack).save(compoundTag);
                    compoundTag.putInt("dataVersion", version);
                }
                listTag.add(compoundTag);
            }
            listTag.write(dos);
            dos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save itemstacks.");
        }
    }

    public static ItemStack[] deserializeIcons(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            DataInputStream dis = new DataInputStream(gzip);
            ListTag listTag = ListTag.TYPE.load(dis, NbtAccounter.unlimitedHeap());
            org.bukkit.inventory.ItemStack[] itemStacks = new org.bukkit.inventory.ItemStack[listTag.size()];
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag compoundTag = (CompoundTag) listTag.get(i);
                if (!compoundTag.isEmpty()) {
                    int version = compoundTag.getInt("dataVersion");
                    if (version < getDataVersion()) {
                        compoundTag = (CompoundTag) MinecraftServer.getServer().fixerUpper.update(References.ITEM_STACK, new Dynamic<>(NbtOps.INSTANCE, compoundTag), version, getDataVersion()).getValue();
                    }
                    itemStacks[i] = CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.of(compoundTag));
                }
            }
            dis.close();
            return itemStacks;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load itemstacks.");
        }
    }

    public static byte[] serializePotionEffects(Collection<PotionEffect> effects) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(gzip);
            bukkitObjectOutputStream.writeInt(effects.toArray().length);

            for (int var3 = 0; var3 < effects.toArray().length; ++var3) {
                bukkitObjectOutputStream.writeObject(effects.toArray()[var3]);
            }

            bukkitObjectOutputStream.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save potion effect.");
        }
    }

    public static Collection<PotionEffect> deserializePotionEffects(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(gzip);

            int size = bukkitObjectInputStream.readInt();
            List<PotionEffect> effects = new ArrayList<>(size);

            for (int i = 0; i < size; ++i) {
                effects.add((PotionEffect) bukkitObjectInputStream.readObject());
            }

            bukkitObjectInputStream.close();
            return effects;
        } catch (ClassNotFoundException | IOException var6) {
            throw new IllegalStateException("Unable to load potion effect.");
        }
    }

    private static class UnsafeHolder {
        private static final Unsafe unsafe;
        private static final long stopReadPacketOffset;

        static void setStopReadPacket(Connection connection) {
            unsafe.putObject(connection, stopReadPacketOffset, true);
        }

        static boolean isStopReadPacket(Connection connection) {
            return unsafe.getBoolean(connection, stopReadPacketOffset);
        }

        static {
            try {
                Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe) unsafeField.get(null);
                stopReadPacketOffset = unsafe.objectFieldOffset(Connection.class.getDeclaredField("stopReadingPackets"));
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}