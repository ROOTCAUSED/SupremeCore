package net.supremesurvival.supremecore.commonUtils;

import org.bukkit.Bukkit;

public class Logger {
    public Logger() {

    }
    public static void sendMessage(String message, LogType logType, String handle){
        switch (logType) {
            case ERR -> sendErrLog(message);
            case INFO -> sendInfoLog(message, handle);
            case CRIT -> sendCritLog(message);
            case WARN -> sendWarnLog(message);
        }
    }
    private static void sendErrLog(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }
    private static void sendInfoLog(String message, String handle){
        Bukkit.getConsoleSender().sendMessage("["+handle+"][INFO][+]"+message);
    }
    @SuppressWarnings("EmptyMethod")
    private static void sendCritLog(String message){

    }
    @SuppressWarnings("EmptyMethod")
    private static void sendWarnLog(String message){

    }
    public enum LogType{
        INFO,
        WARN,
        ERR,
        CRIT,

    }
}
