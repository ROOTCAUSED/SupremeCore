package net.supremesurvival.supremecore.commonUtils;

import org.bukkit.Bukkit;

public class Logger {
    public Logger() {

    }
    public static void sendMessage(String message, LogType logType, String handle){
        switch (logType){
            case ERR:
                sendErrLog(message);
                break;
            case INFO:
                sendInfoLog(message, handle);
                break;
            case CRIT:
                sendCritLog(message);
                break;
            case WARN:
                sendWarnLog(message);
                break;
        }
    }
    private static void sendErrLog(String message){
        Bukkit.getConsoleSender().sendMessage();
    }
    private static void sendInfoLog(String message, String handle){
        Bukkit.getConsoleSender().sendMessage("["+handle+"][INFO][+]"+message);
    }
    private static void sendCritLog(String message){

    }
    private static void sendWarnLog(String message){

    }
    public static enum LogType{
        INFO,
        WARN,
        ERR,
        CRIT,

    }
}
