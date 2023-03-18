package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActionCommands implements CommandExecutor {
    IntervalAnnouncer announcer;
    protected static List<String> stfu;

    public ActionCommands(IntervalAnnouncer announcer) {
        this.announcer = announcer;
        stfu = new ArrayList();
    }

    public void sms(CommandSender s, String msg) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        Player target;
        String msg;
        int index;
        String[] var10;
        int var11;
        int time;
        Player t;
        Iterator var26;
        String part;
        String[] var31;
        String[] var33;
        if (!(s instanceof Player)) {
            if (args.length == 0) {
                this.sms(s, "&8&m-----------------------------------------------------");
                this.sms(s, "&Forked from &fextended_clip");
                this.sms(s, "&8&m-----------------------------------------------------");
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                this.announcer.rello();
                this.sms(s, "&8&m-----------------------------------------------------");
                this.sms(s, "&b&lAnnouncer &bconfiguration reloaded!");
                this.sms(s, "&8&m-----------------------------------------------------");
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                this.sms(s, "&8&m-----------------------------------------------------");
                this.sms(s, "&bAnnouncer &fHelp");
                this.sms(s, "&c/aa send <player> <display time> <message/index>");
                this.sms(s, "&fSend an ActionBar message to a player");
                this.sms(s, "&c/aa announce <display length> <message/index>");
                this.sms(s, "&fSend an ActionBar message to all online players");
                if (IntervalAnnouncer.placeholderAPI) {
                    this.sms(s, "&c/aa pannounce <player> <display time> <message/index>");
                    this.sms(s, "&fSend an ActionBar message to all online players with placeholders set to the player specified");
                }

                this.sms(s, "&c/aa list");
                this.sms(s, "&fList active announcements");
                this.sms(s, "&c/aa add <message>");
                this.sms(s, "&fAdd an announcement");
                this.sms(s, "&c/aa remove <index>");
                this.sms(s, "&fRemove an announcement from the specified index");
                this.sms(s, "&c/aa reload");
                this.sms(s, "&fReload ActionAnnouncer");
                this.sms(s, "&8&m-----------------------------------------------------");
                return true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (IntervalAnnouncer.iTask == null) {
                    this.sms(s, "&cThere is no auto announcement currently running!");
                    return true;
                } else {
                    //Check this doesn't break shit
                    this.announcer.stopAnnouncements();
                    Bukkit.getScheduler().cancelTasks(this.announcer.plugin);
                    this.sms(s, "&bAuto announcements have been stopped");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (IntervalAnnouncer.iTask != null) {
                    this.sms(s, "&cThe auto announcement is already running!");
                    return true;
                } else {
                    this.announcer.startAnnouncements();
                    this.sms(s, "&bAuto announcements have been started");
                    return true;
                }
            } else {
                int e;
                if (args[0].equalsIgnoreCase("list")) {
                    if (IntervalAnnouncer.announcements != null && !IntervalAnnouncer.announcements.isEmpty()) {
                        this.sms(s, "&8&m-----------------------------------------------------");
                        this.sms(s, "&bActive announcements: &f" + IntervalAnnouncer.announcements.size());

                        for(e = 0; e < IntervalAnnouncer.announcements.size(); ++e) {
                            this.sms(s, e + "&7: &r" + IntervalAnnouncer.announcements.get(e));
                        }

                        this.sms(s, "&8&m-----------------------------------------------------");
                        return true;
                    } else {
                        this.sms(s, "&8&m-----------------------------------------------------");
                        this.sms(s, "&cThere are no active announcements!");
                        this.sms(s, "&8&m-----------------------------------------------------");
                        return true;
                    }
                } else {
                    if (args[0].equalsIgnoreCase("add")) {
                        if (args.length < 2) {
                            this.sms(s, "&cIncorrect usage! &b/aa add <message>");
                            return true;
                        } else {
                            msg = null;
                            String[] var34 = args;
                            index = args.length;

                            for(time = 0; time < index; ++time) {
                                msg = var34[time];
                                if (!args[0].equals(msg)) {
                                    if (msg == null) {
                                        msg = msg;
                                    } else {
                                        msg = msg + " " + msg;
                                    }
                                }
                            }

                            if (msg == null) {
                                this.sms(s, "&cMessage was invalid!");
                                return true;
                            } else {
                                IntervalAnnouncer.announcements.add(msg);
                                this.announcer.plugin.getConfig().set("announcements", IntervalAnnouncer.announcements);
                                this.sms(s, "&bMessage was successfully added!");
                                this.announcer.plugin.saveConfig();
                                return true;
                            }
                        }
                    } else if (!args[0].equalsIgnoreCase("remove") && !args[0].equalsIgnoreCase("delete")) {
                        if (!args[0].equalsIgnoreCase("send") && !args[0].equalsIgnoreCase("msg")) {
                            if (!args[0].equalsIgnoreCase("announce") && !args[0].equalsIgnoreCase("broadcast")) {
                                if (!args[0].equalsIgnoreCase("pannounce") && !args[0].equalsIgnoreCase("pbroadcast")) {
                                    this.sms(s, "&cIncorrect usage!");
                                    return true;
                                } else if (!IntervalAnnouncer.placeholderAPI) {
                                    this.sms(s, "&cThis command requires PlaceholderAPI integration to be used!");
                                    return true;
                                } else if (args.length < 4) {
                                    this.sms(s, "&cIncorrect usage! &b/aa pannounce <player> <display time> <message/index>");
                                    return true;
                                } else if (Bukkit.getServer().getOnlinePlayers().size() <= 0) {
                                    this.sms(s, "&cNo players online to announce to!");
                                    return true;
                                } else {
                                    target = Bukkit.getPlayer(args[1]);
                                    if (target == null) {
                                        this.sms(s, args[1] + " &cis not online!");
                                        return true;
                                    } else if (!this.isInt(args[2])) {
                                        this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                        return true;
                                    } else {
                                        time = Integer.parseInt(args[2]);
                                        if (time < 1) {
                                            this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                            return true;
                                        } else {
                                            msg = null;
                                            if (args.length == 4) {
                                                if (this.isInt(args[3])) {
                                                    index = Integer.parseInt(args[3]);
                                                    if (index < 0 || index >= IntervalAnnouncer.announcements.size()) {
                                                        this.sms(s, "&cIncorrect entry! That entry did not exist!");
                                                        return true;
                                                    }

                                                    msg = IntervalAnnouncer.announcements.get(index);
                                                } else {
                                                    msg = args[3];
                                                }
                                            } else {
                                                var31 = args;
                                                index = args.length;

                                                for(index = 0; index < index; ++index) {
                                                    msg = var31[index];
                                                    if (!args[0].equals(msg) && !args[1].equals(msg) && !args[2].equals(msg)) {
                                                        if (msg == null) {
                                                            msg = msg;
                                                        } else {
                                                            msg = msg + " " + msg;
                                                        }
                                                    }
                                                }
                                            }

                                            if (msg == null) {
                                                this.sms(s, "&cMessage was invalid!");
                                                return true;
                                            } else {
                                                msg = PlaceholderAPI.setPlaceholders(target, msg);

                                                for(var26 = Bukkit.getServer().getOnlinePlayers().iterator(); var26.hasNext(); ActionAPI.sendTimedPlayerAnnouncement(this.announcer.plugin, t, msg, time)) {
                                                    t = (Player)var26.next();
                                                    if (!IntervalAnnouncer.disableSounds && (stfu == null || !stfu.contains(t.getName()))) {
                                                        t.playNote(t.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                                                    }
                                                }

                                                this.sms(s, "&bPlayer specific announcement sent!");
                                                return true;
                                            }
                                        }
                                    }
                                }
                            } else if (args.length < 3) {
                                this.sms(s, "&cIncorrect usage! &b/aa announce <display time> <message/index>");
                                return true;
                            } else if (Bukkit.getServer().getOnlinePlayers().size() <= 0) {
                                this.sms(s, "&cNo players online to announce to!");
                                return true;
                            } else {
                                msg = null;
                                if (!this.isInt(args[1])) {
                                    this.sms(s, "&f" + args[1] + " &cis not a valid display time!");
                                    return true;
                                } else {
                                    time = Integer.parseInt(args[1]);
                                    if (time < 1) {
                                        this.sms(s, "&f" + args[1] + " &cis not a valid display time!");
                                        return true;
                                    } else {
                                        if (args.length == 3) {
                                            if (this.isInt(args[2])) {
                                                time = Integer.parseInt(args[2]);
                                                if (time < 0 || time >= IntervalAnnouncer.announcements.size()) {
                                                    this.sms(s, "&cIncorrect entry! That entry did not exist!");
                                                    return true;
                                                }

                                                msg = IntervalAnnouncer.announcements.get(time);
                                            } else {
                                                msg = args[2];
                                            }
                                        } else {
                                            var10 = args;
                                            index = args.length;

                                            for(index = 0; index < index; ++index) {
                                                msg = var10[index];
                                                if (!args[0].equals(msg) && !args[1].equals(msg)) {
                                                    if (msg == null) {
                                                        msg = msg;
                                                    } else {
                                                        msg = msg + " " + msg;
                                                    }
                                                }
                                            }
                                        }

                                        if (msg == null) {
                                            this.sms(s, "&cMessage was invalid!");
                                            return true;
                                        } else {
                                            for(Iterator var35 = Bukkit.getServer().getOnlinePlayers().iterator(); var35.hasNext(); ActionAPI.sendTimedPlayerAnnouncement(this.announcer.plugin, t, msg, time)) {
                                                t = (Player)var35.next();
                                                if (!IntervalAnnouncer.disableSounds && (stfu == null || !stfu.contains(t.getName()))) {
                                                    t.playNote(t.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                                                }
                                            }

                                            this.sms(s, "&bAnnouncement sent!");
                                            return true;
                                        }
                                    }
                                }
                            }
                        } else if (args.length < 4) {
                            this.sms(s, "&cIncorrect usage! &b/aa send <player> <display time> <message>");
                            return true;
                        } else {
                            msg = args[1];
                            target = Bukkit.getServer().getPlayer(msg);
                            if (target == null) {
                                this.sms(s, "&f" + msg + " &cis not online!");
                                return true;
                            } else {
                                msg = null;
                                if (!this.isInt(args[2])) {
                                    this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                    return true;
                                } else {
                                    index = Integer.parseInt(args[2]);
                                    if (index < 1) {
                                        this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                        return true;
                                    } else {
                                        if (args.length == 4) {
                                            if (this.isInt(args[3])) {
                                                index = Integer.parseInt(args[3]);
                                                if (index < 0 || index >= IntervalAnnouncer.announcements.size()) {
                                                    this.sms(s, "&cIncorrect entry! That entry did not exist!");
                                                    return true;
                                                }

                                                msg = IntervalAnnouncer.announcements.get(index);
                                            } else {
                                                msg = args[3];
                                            }
                                        } else {
                                            var33 = args;
                                            var11 = args.length;

                                            for(index = 0; index < var11; ++index) {
                                                part = var33[index];
                                                if (!args[0].equals(part) && !args[1].equals(part) && !args[2].equals(part)) {
                                                    if (msg == null) {
                                                        msg = part;
                                                    } else {
                                                        msg = msg + " " + part;
                                                    }
                                                }
                                            }
                                        }

                                        if (msg == null) {
                                            this.sms(s, "&cMessage was invalid!");
                                            return true;
                                        } else {
                                            if (!IntervalAnnouncer.disableSounds && (stfu == null || !stfu.contains(target.getName()))) {
                                                target.playNote(target.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                                            }

                                            ActionAPI.sendTimedPlayerAnnouncement(this.announcer.plugin, target, msg, index);
                                            this.sms(s, "&bMessage sent to &f" + target.getName());
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (args.length < 2) {
                        this.sms(s, "&cIncorrect usage! &b/aa remove <index>");
                        return true;
                    } else {
                        boolean var16 = false;

                        try {
                            e = Integer.parseInt(args[1]);
                        } catch (Exception var14) {
                            this.sms(s, "&cIncorrect usage! &b/aa remove <index>");
                            return true;
                        }

                        if (e >= 0 && e < IntervalAnnouncer.announcements.size()) {
                            IntervalAnnouncer.announcements.remove(e);
                            this.announcer.plugin.getConfig().set("announcements", IntervalAnnouncer.announcements);
                            this.announcer.plugin.saveConfig();
                            this.sms(s, "&bMessage was successfully removed!");
                            return true;
                        } else {
                            this.sms(s, "&cIncorrect entry! That entry did not exist!");
                            return true;
                        }
                    }
                }
            }
        } else {
            target = (Player)s;
            if (args.length == 0) {
                this.sms(target, "&8&m-----------------------------------------------------");
                this.sms(target, "&c&lA&cction&7&lA&7nnouncer &f&o" + this.announcer.plugin.getDescription().getVersion());
                this.sms(target, "&7Created by &fextended_clip");
                this.sms(target, "&8&m-----------------------------------------------------");
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                if (!target.hasPermission("actionannouncer.help")) {
                    this.sms(target, "&cYou dont have permission to do that!");
                    return true;
                } else {
                    this.sms(target, "&8&m-----------------------------------------------------");
                    this.sms(target, "&c&lA&cction&7&lA&7nnouncer &fHelp");
                    this.sms(target, "&c/aa quiet");
                    this.sms(target, "&fToggle announcement sounds on/off");
                    this.sms(s, "&c/aa send <player> <display time> <message/index>");
                    this.sms(s, "&fSend an ActionBar message to a player");
                    this.sms(s, "&c/aa announce <display time> <message/index>");
                    this.sms(s, "&fSend an ActionBar message to all online players");
                    if (IntervalAnnouncer.placeholderAPI) {
                        this.sms(s, "&c/aa pannounce <player> <display time> <message/index>");
                        this.sms(s, "&fSend an ActionBar message to all online players with placeholders set to the player specified");
                    }

                    this.sms(target, "&c/aa list");
                    this.sms(target, "&fList active announcements");
                    this.sms(target, "&c/aa add <message>");
                    this.sms(target, "&fAdd an announcement");
                    this.sms(target, "&c/aa remove <index>");
                    this.sms(target, "&fRemove an announcement from the specified index");
                    this.sms(target, "&c/aa reload");
                    this.sms(target, "&fReload ActionAnnouncer");
                    this.sms(target, "&c/aa stop");
                    this.sms(target, "&fStop auto announcements");
                    this.sms(target, "&c/aa start");
                    this.sms(target, "&fStart auto announcements");
                    this.sms(target, "&8&m-----------------------------------------------------");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!target.hasPermission("actionannouncer.reload")) {
                    this.sms(target, "&cYou dont have permission to do that!");
                    return true;
                } else {
                    this.announcer.rello();
                    this.sms(target, "&8&m-----------------------------------------------------");
                    this.sms(target, "&c&lA&cction&7&lA&7nnouncer &aconfiguration reloaded!");
                    this.sms(target, "&8&m-----------------------------------------------------");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!target.hasPermission("actionannouncer.stop")) {
                    this.sms(target, "&cYou dont have permission to do that!");
                    return true;
                } else if (IntervalAnnouncer.iTask == null) {
                    this.sms(target, "&cThere is no auto announcement currently running!");
                    return true;
                } else {
                    this.announcer.stopAnnouncements();
                    this.sms(target, "&7Auto announcements have been stopped");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (!target.hasPermission("actionannouncer.start")) {
                    this.sms(target, "&cYou dont have permission to do that!");
                    return true;
                } else if (IntervalAnnouncer.iTask != null) {
                    this.sms(target, "&cThe auto announcement is already running!");
                    return true;
                } else {
                    this.announcer.startAnnouncements();
                    this.sms(target, "&aAuto announcements have been started");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (!target.hasPermission("actionannouncer.list")) {
                    this.sms(target, "&cYou dont have permission to do that!");
                    return true;
                } else if (IntervalAnnouncer.announcements != null && !IntervalAnnouncer.announcements.isEmpty()) {
                    this.sms(target, "&8&m-----------------------------------------------------");
                    this.sms(target, "&bActive announcements: &f" + IntervalAnnouncer.announcements.size());

                    for(time = 0; time < IntervalAnnouncer.announcements.size(); ++time) {
                        this.sms(s, time + "&7: &r" + IntervalAnnouncer.announcements.get(time));
                    }

                    this.sms(target, "&8&m-----------------------------------------------------");
                    return true;
                } else {
                    this.sms(target, "&8&m-----------------------------------------------------");
                    this.sms(target, "&cThere are no active announcements!");
                    this.sms(target, "&8&m-----------------------------------------------------");
                    return true;
                }
            } else if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("new")) {
                if (!args[0].equalsIgnoreCase("remove") && !args[0].equalsIgnoreCase("delete")) {
                    if (!args[0].equalsIgnoreCase("stfu") && !args[0].equalsIgnoreCase("quiet")) {
                        if (!args[0].equalsIgnoreCase("send") && !args[0].equalsIgnoreCase("msg")) {
                            if (!args[0].equalsIgnoreCase("announce") && !args[0].equalsIgnoreCase("broadcast")) {
                                if (!args[0].equalsIgnoreCase("pannounce") && !args[0].equalsIgnoreCase("pbroadcast")) {
                                    this.sms(target, "&cIncorrect command usage!");
                                    return true;
                                } else if (!target.hasPermission("actionannouncer.pannounce")) {
                                    this.sms(target, "&cYou dont have permission to do that!");
                                    return true;
                                } else if (!IntervalAnnouncer.placeholderAPI) {
                                    this.sms(target, "&cThis command requires PlaceholderAPI integration to be used!");
                                    return true;
                                } else if (args.length < 4) {
                                    this.sms(s, "&cIncorrect usage! &b/aa pannounce <player> <display time> <message/index>");
                                    return true;
                                } else if (Bukkit.getServer().getOnlinePlayers().size() <= 0) {
                                    this.sms(s, "&cNo players online to announce to!");
                                    return true;
                                } else {
                                    target = Bukkit.getPlayer(args[1]);
                                    if (target == null) {
                                        this.sms(s, args[1] + " &cis not online!");
                                        return true;
                                    } else if (!this.isInt(args[2])) {
                                        this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                        return true;
                                    } else {
                                        time = Integer.parseInt(args[2]);
                                        if (time < 1) {
                                            this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                            return true;
                                        } else {
                                            msg = null;
                                            if (args.length == 4) {
                                                if (this.isInt(args[3])) {
                                                    index = Integer.parseInt(args[3]);
                                                    if (index < 0 || index >= IntervalAnnouncer.announcements.size()) {
                                                        this.sms(s, "&cIncorrect entry! That entry did not exist!");
                                                        return true;
                                                    }

                                                    msg = IntervalAnnouncer.announcements.get(index);
                                                } else {
                                                    msg = args[3];
                                                }
                                            } else {
                                                var33 = args;
                                                var11 = args.length;

                                                for(index = 0; index < var11; ++index) {
                                                    part = var33[index];
                                                    if (!args[0].equals(part) && !args[1].equals(part) && !args[2].equals(part)) {
                                                        if (msg == null) {
                                                            msg = part;
                                                        } else {
                                                            msg = msg + " " + part;
                                                        }
                                                    }
                                                }
                                            }

                                            if (msg == null) {
                                                this.sms(s, "&cMessage was invalid!");
                                                return true;
                                            } else {
                                                msg = PlaceholderAPI.setPlaceholders(target, msg);
                                                for(Iterator var32 = Bukkit.getServer().getOnlinePlayers().iterator(); var32.hasNext(); ActionAPI.sendTimedPlayerAnnouncement(this.announcer.plugin, t, msg, time)) {
                                                    t = (Player)var32.next();
                                                    if (!IntervalAnnouncer.disableSounds && (stfu == null || !stfu.contains(t.getName()))) {
                                                        t.playNote(t.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                                                    }
                                                }

                                                this.sms(s, "&bPlayer specific announcement sent!");
                                                return true;
                                            }
                                        }
                                    }
                                }
                            } else if (!target.hasPermission("actionannouncer.announce")) {
                                this.sms(target, "&cYou dont have permission to do that!");
                                return true;
                            } else if (args.length < 3) {
                                this.sms(s, "&cIncorrect usage! &b/aa announce <display time> <message/index>");
                                return true;
                            } else if (Bukkit.getServer().getOnlinePlayers().size() <= 0) {
                                this.sms(s, "&cNo players online to announce to!");
                                return true;
                            } else {
                                msg = null;
                                if (!this.isInt(args[1])) {
                                    this.sms(s, "&f" + args[1] + " &cis not a valid display time!");
                                    return true;
                                } else {
                                    time = Integer.parseInt(args[1]);
                                    if (time < 1) {
                                        this.sms(s, "&f" + args[1] + " &cis not a valid display time!");
                                        return true;
                                    } else {
                                        if (args.length == 3) {
                                            if (this.isInt(args[2])) {
                                                index = Integer.parseInt(args[2]);
                                                if (index < 0 || index >= IntervalAnnouncer.announcements.size()) {
                                                    this.sms(s, "&cIncorrect entry! That entry did not exist!");
                                                    return true;
                                                }

                                                msg = IntervalAnnouncer.announcements.get(index);
                                            } else {
                                                msg = args[2];
                                            }
                                        } else {
                                            var31 = args;
                                            index = args.length;

                                            for(index = 0; index < index; ++index) {
                                                msg = var31[index];
                                                if (!args[0].equals(msg) && !args[1].equals(msg)) {
                                                    if (msg == null) {
                                                        msg = msg;
                                                    } else {
                                                        msg = msg + " " + msg;
                                                    }
                                                }
                                            }
                                        }

                                        if (msg == null) {
                                            this.sms(s, "&cMessage was invalid!");
                                            return true;
                                        } else {
                                            for(var26 = Bukkit.getServer().getOnlinePlayers().iterator(); var26.hasNext(); ActionAPI.sendTimedPlayerAnnouncement(this.announcer.plugin, t, msg, time)) {
                                                t = (Player)var26.next();
                                                if (!IntervalAnnouncer.disableSounds && (stfu == null || !stfu.contains(t.getName()))) {
                                                    t.playNote(t.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                                                }
                                            }

                                            this.sms(s, "&bAnnouncement sent!");
                                            return true;
                                        }
                                    }
                                }
                            }
                        } else if (!target.hasPermission("actionannouncer.send")) {
                            this.sms(target, "&cYou dont have permission to do that!");
                            return true;
                        } else if (args.length < 4) {
                            this.sms(s, "&cIncorrect usage! &b/aa send <player> <display time> <message>");
                            return true;
                        } else {
                            msg = args[1];
                            t = Bukkit.getServer().getPlayer(msg);
                            if (t == null) {
                                this.sms(s, "&f" + msg + " &cis not online!");
                                return true;
                            } else {
                                msg = null;
                                if (!this.isInt(args[2])) {
                                    this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                    return true;
                                } else {
                                    index = Integer.parseInt(args[2]);
                                    if (index < 1) {
                                        this.sms(s, "&f" + args[2] + " &cis not a valid display time!");
                                        return true;
                                    } else {
                                        if (args.length == 4) {
                                            if (this.isInt(args[3])) {
                                                index = Integer.parseInt(args[3]);
                                                if (index < 0 || index >= IntervalAnnouncer.announcements.size()) {
                                                    this.sms(s, "&cIncorrect entry! That entry did not exist!");
                                                    return true;
                                                }

                                                msg = IntervalAnnouncer.announcements.get(index);
                                            } else {
                                                msg = args[3];
                                            }
                                        } else {
                                            String[] var13 = args;
                                            int var12 = args.length;

                                            for(var11 = 0; var11 < var12; ++var11) {
                                                part = var13[var11];
                                                if (!args[0].equals(part) && !args[1].equals(part) && !args[2].equals(part)) {
                                                    if (msg == null) {
                                                        msg = part;
                                                    } else {
                                                        msg = msg + " " + part;
                                                    }
                                                }
                                            }
                                        }

                                        if (msg == null) {
                                            this.sms(s, "&cMessage was invalid!");
                                            return true;
                                        } else {
                                            if (!IntervalAnnouncer.disableSounds && (stfu == null || !stfu.contains(t.getName()))) {
                                                t.playNote(t.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                                            }

                                            ActionAPI.sendTimedPlayerAnnouncement(this.announcer.plugin, t, msg, index);
                                            this.sms(s, "&bMessage sent to &f" + t.getName());
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (IntervalAnnouncer.disableSounds) {
                        return true;
                    } else {
                        if (stfu == null) {
                            stfu = new ArrayList();
                        }

                        if (stfu.contains(target.getName())) {
                            stfu.remove(target.getName());
                            target.playNote(target.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
                            IntervalAnnouncer.sendAnnouncement(target, "&bActionbar sounds &aEnabled&b!!");
                            return true;
                        } else {
                            stfu.add(target.getName());
                            IntervalAnnouncer.sendAnnouncement(target, "&bActionbar sounds &7Disabled&b!");
                            return true;
                        }
                    }
                } else if (!target.hasPermission("actionannouncer.remove")) {
                    this.sms(target, "&cYou dont have permission to do that!");
                    return true;
                } else if (args.length < 2) {
                    this.sms(s, "&cIncorrect usage! &7/aa remove <index>");
                    return true;
                } else {
                    boolean var19 = false;

                    try {
                        time = Integer.parseInt(args[1]);
                    } catch (Exception var15) {
                        this.sms(s, "&cIncorrect usage! &7/aa remove <index>");
                        return true;
                    }

                    if (time >= 0 && time < IntervalAnnouncer.announcements.size()) {
                        IntervalAnnouncer.announcements.remove(time);
                        this.announcer.plugin.getConfig().set("announcements", IntervalAnnouncer.announcements);
                        this.announcer.plugin.saveConfig();
                        this.sms(s, "&aMessage was successfully removed!");
                        return true;
                    } else {
                        this.sms(s, "&cIncorrect entry! That entry did not exist!");
                        return true;
                    }
                }
            } else if (!target.hasPermission("actionannouncer.add")) {
                this.sms(target, "&cYou dont have permission to do that!");
                return true;
            } else if (args.length < 2) {
                this.sms(s, "&cIncorrect usage! &7/aa add <message>");
                return true;
            } else {
                msg = null;
                var10 = args;
                index = args.length;

                for(index = 0; index < index; ++index) {
                    msg = var10[index];
                    if (!args[0].equals(msg)) {
                        if (msg == null) {
                            msg = msg;
                        } else {
                            msg = msg + " " + msg;
                        }
                    }
                }

                if (msg == null) {
                    this.sms(s, "&cMessage was invalid!");
                    return true;
                } else {
                    IntervalAnnouncer.announcements.add(msg);
                    this.announcer.plugin.getConfig().set("announcements", IntervalAnnouncer.announcements);
                    this.sms(s, "&bMessage was successfully added!");
                    this.announcer.plugin.saveConfig();
                    return true;
                }
            }
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }
}