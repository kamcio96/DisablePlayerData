package pl.kamcio96.disableplayerdata;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DisablePlayerData extends JavaPlugin implements InvocationHandler {

    private Object original;

    @Override
    public void onEnable() {

        String ver = Bukkit.getServer().getClass().getName().split("\\.")[3];

        getLogger().info(ver);

        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + ver + ".IPlayerFileData");
            Object proxyIPlayerFileData = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);

            Class<?> minecraftServer = Class.forName("net.minecraft.server." + ver + ".MinecraftServer");
            Object server = minecraftServer.getMethod("getServer").invoke(null);
            Object playerList = minecraftServer.getMethod("getPlayerList").invoke(server);
            Field f = playerList.getClass().getField("playerFileData");

            original = f.get(playerList);
            f.set(playerList, proxyIPlayerFileData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("load")) {
            return method.invoke(original, args);
        }

        return null;
    }
}
