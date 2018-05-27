package pl.kamcio96.disableplayerdata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DisablePlayerData extends JavaPlugin implements InvocationHandler {

    private static final String PERMISSION = "disableplayerdata.forcesave";

    private Object original;
    private Method getBukkitEntityMethod;

    private boolean usePermission;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        usePermission = getConfig().getBoolean("usePermission", false);

        try {
            String ver = Bukkit.getServer().getClass().getName().split("\\.")[3];

            Class<?> clazz = Class.forName("net.minecraft.server." + ver + ".IPlayerFileData");
            Object proxyIPlayerFileData = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);

            Class<?> minecraftServer = Class.forName("net.minecraft.server." + ver + ".MinecraftServer");
            Object server = minecraftServer.getMethod("getServer").invoke(null);
            Object playerList = minecraftServer.getMethod("getPlayerList").invoke(server);
            Field f = playerList.getClass().getField("playerFileData");

            original = f.get(playerList);
            f.set(playerList, proxyIPlayerFileData);

            getBukkitEntityMethod = Class.forName("net.minecraft.server." + ver + ".EntityHuman").getMethod("getBukkitEntity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("save")) {
            Object oPlayer = getBukkitEntityMethod.invoke(args[0]);
            Player player = (Player) oPlayer;
            SavePlayerEvent event = new SavePlayerEvent(player);

            if (usePermission && player.hasPermission(PERMISSION)) {
                event.setCancelled(false);
            }

            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        return method.invoke(original, args);
    }
}
