package quest.yuzhou.realmcraft.player.menu.mainmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.AbstractMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainMenu extends AbstractMenu {

    public MainMenu(RealmCraft plugin) {
        super(plugin);

        String title = plugin.getConfig().getString("main-menu-name");
        if (title == null) {
            plugin.getLogger().severe("Main menu name in config.yml not found.");
            title = "&aRealmCraft &7/ &2領域世界";
        }
        menuName = Utilities.colorize(title);
    }

    @Override
    public void open(Player player) {
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 0.5F);

        Inventory inventory = Bukkit.createInventory(player, 45, menuName);

        ItemStack profileItem = null;
        ItemStack discordItem = null;
        ItemStack tutorialItem = null;
        ItemStack websiteItem = null;
        ItemStack docItem = null;
        ItemStack manualItem = null;
        ItemStack strategicEventItem = null;
        ItemStack disposeItem = null;
        ItemStack seasonItem = null;
        ItemStack musicItem = null;
        ItemStack skillItem = null;
        try {
            profileItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta profileMeta = (SkullMeta) profileItem.getItemMeta();
            profileMeta.setOwningPlayer(player);
            profileMeta.setDisplayName(ChatColor.YELLOW + "個人信息");
            List<String> profileLore = new ArrayList<>();
            profileLore.add(ChatColor.GRAY + "您的個人數據。積分、段位、等級、公會等等");
            profileMeta.setLore(profileLore);
            profileItem.setItemMeta(profileMeta);

            discordItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta discordMeta = (SkullMeta) discordItem.getItemMeta();
            discordMeta.setOwningPlayer(Bukkit.getOfflinePlayer("SeogE"));
            discordMeta.setDisplayName(ChatColor.AQUA + "Discord");
            List<String> discordLore = new ArrayList<>();
            discordLore.add(ChatColor.GRAY + "伺服器的Discord聊天群。");
            discordLore.add(ChatColor.GRAY + "伺服器的最新消息、福利都在這裏");
            discordMeta.setLore(discordLore);
            discordItem.setItemMeta(discordMeta);

            tutorialItem = new ItemStack(Material.FEATHER);
            ItemMeta tutorialMeta = tutorialItem.getItemMeta();
            tutorialMeta.setCustomModelData(10005);
            tutorialMeta.setDisplayName(ChatColor.YELLOW + "新手教學");
            List<String> tutorialLore = new ArrayList<>();
            tutorialLore.add(ChatColor.GRAY + "還是不知道伺服器怎麽玩？");
            tutorialLore.add(ChatColor.GRAY + "你可以回顧之前新手教學的内容哦");
            tutorialMeta.setLore(tutorialLore);
            tutorialItem.setItemMeta(tutorialMeta);

            docItem = new ItemStack(Material.KNOWLEDGE_BOOK);
            ItemMeta docMeta = docItem.getItemMeta();
            docMeta.setDisplayName("官方文檔");
            List<String> docLore = new ArrayList<>();
            docLore.add(ChatColor.GRAY + "伺服器的文檔。各種詳細資料、數據，");
            docLore.add(ChatColor.GRAY + "都有在内詳細寫明");
            docMeta.setLore(docLore);
            docItem.setItemMeta(docMeta);

            websiteItem = new ItemStack(Material.PAPER);
            ItemMeta websiteMeta = websiteItem.getItemMeta();
            websiteMeta.setCustomModelData(10077);
            websiteMeta.setDisplayName(ChatColor.BLUE + "官方網站");
            List<String> websiteLore = new ArrayList<>();
            websiteLore.add(ChatColor.GRAY + "伺服器的官方網站");
            websiteMeta.setLore(websiteLore);
            websiteItem.setItemMeta(websiteMeta);

            manualItem = new ItemStack(Material.IRON_INGOT);
            ItemMeta manualMeta = manualItem.getItemMeta();
            manualMeta.setCustomModelData(10008);
            manualMeta.setDisplayName(ChatColor.GOLD + "説明書");
            List<String> manualLore = new ArrayList<>();
            manualLore.add(ChatColor.GRAY + "簡短地解釋伺服器的一些機制/玩法");
            manualMeta.setLore(manualLore);
            manualItem.setItemMeta(manualMeta);

            strategicEventItem = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta strategicMeta = strategicEventItem.getItemMeta();
            strategicMeta.setCustomModelData(10007);
            strategicMeta.setDisplayName(ChatColor.AQUA + "戰略事件");
            List<String> strategicLore = new ArrayList<>();
            strategicLore.add(ChatColor.GRAY + "消耗經驗值，觸發資源刷新，懸賞追蹤，");
            strategicLore.add(ChatColor.GRAY + "契約綁定等刺激玩法。");
            strategicMeta.setLore(strategicLore);
            strategicEventItem.setItemMeta(strategicMeta);

            disposeItem = new ItemStack(Material.PAPER);
            ItemMeta disposeMeta = disposeItem.getItemMeta();
            disposeMeta.setDisplayName(ChatColor.RED + "垃圾桶");
            disposeMeta.setCustomModelData(10097);
            List<String> disposeLore = new ArrayList<>();
            disposeLore.add(ChatColor.GRAY + "請把不要的垃圾丟進垃圾桶");
            disposeMeta.setLore(disposeLore);
            disposeItem.setItemMeta(disposeMeta);

            seasonItem = new ItemStack(Material.IRON_INGOT);
            ItemMeta seasonMeta = seasonItem.getItemMeta();
            seasonMeta.setDisplayName(ChatColor.DARK_PURPLE + "賽季");
            seasonMeta.setCustomModelData(10009);
            List<String> seasonLore = new ArrayList<>();
            seasonLore.add(ChatColor.GRAY + "查看本賽季各個段位的獎勵。");
            seasonMeta.setLore(seasonLore);
            seasonItem.setItemMeta(seasonMeta);

            ItemMeta musicMeta;
            if (plugin.getMusicPlayer().isMuted(player)) {
                musicItem = new ItemStack(Material.PAPER);
                musicMeta = musicItem.getItemMeta();
                musicMeta.setCustomModelData(10173);
                musicMeta.setDisplayName(ChatColor.RED + "音樂：已關閉");
            } else {
                musicItem = new ItemStack(Material.PAPER);
                musicMeta = musicItem.getItemMeta();
                musicMeta.setCustomModelData(10172);
                musicMeta.setDisplayName(ChatColor.RED + "音樂：已開啓");
            }
            List<String> musicLore = new ArrayList<>();
            musicLore.add(ChatColor.GRAY + "點我切換音樂開/關狀態");
            musicMeta.setLore(musicLore);
            musicItem.setItemMeta(musicMeta);

            skillItem = new ItemStack(Material.WOODEN_SWORD);
            ItemMeta skillMeta = skillItem.getItemMeta();
            skillMeta.setDisplayName(ChatColor.DARK_AQUA + "技能檢索表");
            skillMeta.setCustomModelData(10006);
            List<String> skillLore = new ArrayList<>();
            skillLore.add(ChatColor.GRAY + "這個網站裏會列出所有武器/裝備的技能，");
            skillLore.add(ChatColor.GRAY + "詳細地解釋每個技能的效果");
            skillMeta.setLore(skillLore);
            skillItem.setItemMeta(skillMeta);

        } catch (NullPointerException e) {
            plugin.getLogger().severe("Can't open main menu!");
            e.printStackTrace();
        }
        inventory.setItem(4, profileItem);
        inventory.setItem(18, discordItem);
        inventory.setItem(20, tutorialItem);
        inventory.setItem(22, websiteItem);
        inventory.setItem(24, docItem);
        inventory.setItem(26, manualItem);
        inventory.setItem(36, strategicEventItem);
        inventory.setItem(38, disposeItem);
        inventory.setItem(40, seasonItem);
        inventory.setItem(42, musicItem);
        inventory.setItem(44, skillItem);

        player.openInventory(inventory);
    }

}
