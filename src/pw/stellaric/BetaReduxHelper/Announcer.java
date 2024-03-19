package pw.stellaric.BetaReduxHelper;

import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;

import java.util.Random;

public class Announcer {

    BetaReduxHelper brh;
    Configuration config;

    int lastAnnouncementIndex;

    int announcementIntervalMinutes;

    String[] announcements;

    public Announcer(BetaReduxHelper plugin) {
        this.brh = plugin;
        this.config = this.brh.getConfiguration();
        this.announcements = new String[]{
                ChatColor.WHITE + "You can use " + ChatColor.YELLOW + "/list" + ChatColor.WHITE + " to view the list of online players.",
                ChatColor.WHITE + "You can use " + ChatColor.YELLOW + "/recent" + ChatColor.WHITE + " to view the list of recently         departed players.",
                ChatColor.WHITE + "Only " + ChatColor.YELLOW + "60%" + ChatColor.WHITE + " of the players online need to sleep to pass   the night. Cool!",
                ChatColor.WHITE + "Thanks for playing on " + ChatColor.RED + "BetaRedux" + ChatColor.WHITE + "!",
                ChatColor.WHITE + "Take a cool " + ChatColor.RED + "screenshot" + ChatColor.WHITE + " of your build? Post it in our " + ChatColor.YELLOW + "   #betaredux-screenshots" + ChatColor.WHITE + " channel!",
                ChatColor.WHITE + "Users that have suffixes are " + ChatColor.YELLOW + "trusted members" + ChatColor.WHITE + "! Wow!",
                ChatColor.YELLOW + "Need help" + ChatColor.WHITE + "? Ask! We're all here " + ChatColor.BLUE + "for you" + ChatColor.WHITE + ".",
                ChatColor.WHITE + "Good luck finding those precious " + ChatColor.AQUA + "diamonds" + ChatColor.WHITE + "!",
                ChatColor.WHITE + "Still a " + ChatColor.GRAY + "guest" + ChatColor.WHITE + "? Message " + ChatColor.RED + "@Stellaric" + ChatColor.WHITE + " on Discord to get      your member role.",
                ChatColor.WHITE + "Remember, don't " + ChatColor.GRAY + "dig down" + ChatColor.WHITE + "! There's " + ChatColor.GOLD + "lava" + ChatColor.WHITE + " down there!",
                ChatColor.WHITE + "Join the " + ChatColor.RED + "Redux Network" + ChatColor.WHITE + " Discord!  " + ChatColor.YELLOW + "discord.gg/tgdkq65",
                ChatColor.WHITE + "There's a " + ChatColor.GREEN + "creeper" + ChatColor.WHITE + " behind you.",
                ChatColor.WHITE + "The more the merrier in the " + ChatColor.GREEN + "spawn town" + ChatColor.WHITE + "! Pick a nice      spot, but be " + ChatColor.YELLOW + "mindful" + ChatColor.WHITE + " of other's space.",
                ChatColor.WHITE + "Got " + ChatColor.GREEN + "feedback" + ChatColor.WHITE + "? Let us know.",
                ChatColor.WHITE + "You can view server " + ChatColor.GREEN + "statistics" + ChatColor.WHITE + " at spawn. There have    been " + ChatColor.YELLOW + brh.metricsHandler.metricBlocksBroken + ChatColor.WHITE + " blocks broken so far!",
                ChatColor.WHITE + "Go finish your " + ChatColor.YELLOW + "homework" + ChatColor.WHITE + ", " + ChatColor.YELLOW + "adult chores" + ", or " + ChatColor.YELLOW + "whatever" + ChatColor.WHITE + " you need to do.",
        };
        loadMessagesFromConfig();
        randomizeAnnouncementsArray();
        this.lastAnnouncementIndex = getRandomNumber(0, announcements.length - 1);
        this.announcementIntervalMinutes = 1;

    }

    public void registerEvents() {
        int minute = (int) 1200L;
        // 20L = 1 second, so 1200L = 60 seconds, or one minute
        // the 0L is the delay before the timer starts
        // the minute * 5 is 1200L or one minute * 5, which is the interval that this repeating task repeats at
        this.brh.getServer().getScheduler().scheduleSyncRepeatingTask(this.brh, new Runnable() {
            public void run() {
                sendAnnouncement();
            }
        }, minute * 2, minute * 45); // (long) this.announcementIntervalMinutes * minute); // minute * 5
    }

    public void loadMessagesFromConfig() {
//        this.message_interval_minutes = config.getInt("minutes_between_announcements", 30);
//        = config.getStringList("announcements")
    }

    public void sendAnnouncement() {
        // don't do anything if no players are online
        if(this.brh.getServer().getOnlinePlayers().length == 0){
            return;
        }

        int announcementIndex = lastAnnouncementIndex + 1;
        if (announcementIndex > this.announcements.length - 1) {
            announcementIndex = 0;
            randomizeAnnouncementsArray();
        }
        this.brh.getServer().broadcastMessage(ChatColor.DARK_AQUA + "NOTICE: " + this.announcements[announcementIndex]);
        this.brh.log(ChatColor.DARK_AQUA + "NOTICE: " + this.announcements[announcementIndex]);
        lastAnnouncementIndex = announcementIndex;
    }

    // Let’s use the Math.random method to generate a random number in a given range [min, max):
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public void randomizeAnnouncementsArray() {
        Random rand = new Random();

        for (int i = 0; i < announcements.length; i++) {
            int randomIndexToSwap = rand.nextInt(announcements.length);
            String temp = announcements[randomIndexToSwap];
            announcements[randomIndexToSwap] = announcements[i];
            announcements[i] = temp;
        }
    }
}

