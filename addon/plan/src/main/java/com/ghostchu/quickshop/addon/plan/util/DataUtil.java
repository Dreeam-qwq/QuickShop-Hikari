package com.ghostchu.quickshop.addon.plan.util;

import com.ghostchu.quickshop.addon.plan.Main;
import com.ghostchu.quickshop.api.database.ShopMetricRecord;
import com.ghostchu.quickshop.api.database.bean.DataRecord;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.common.util.CommonUtil;
import com.ghostchu.quickshop.util.Util;
import com.google.common.html.HtmlEscapers;
import org.jspecify.annotations.NullMarked;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.UUID;

@NullMarked
public class DataUtil {

  public final Main main;

  public DataUtil(final Main main) {

    this.main = main;
  }

  public String formatEconomy(final ShopMetricRecord record) {

    final Shop shop = main.getQuickShop().getShopManager().getShop(record.getShopId());
    if(shop == null || main.getQuickShop().getEconomyManager().provider() == null) {
      final DecimalFormat df = new DecimalFormat("#.00");
      return df.format(record.getTotal());
    }
    return main.getQuickShop().getEconomyManager().provider().format(BigDecimal.valueOf(record.getTotal()), shop.getLocation().getWorld().getName(), shop.getCurrency());
  }

  public String getItemName(final DataRecord dataRecord) {

    final ItemStack stack;
    try {
      stack = Util.deserialize(dataRecord.getItem());
    } catch(final InvalidConfigurationException e) {
      return "[Failed to deserialize]";
    }
    if(stack == null) {
      return "[Failed to deserialize]";
    }
    String name = CommonUtil.prettifyText(stack.getType().name());
    if(stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()) {
      name = stack.getItemMeta().getDisplayName();
    }
    return HtmlEscapers.htmlEscaper().escape(name);
  }

  public String getItemName(final ItemStack stack) {

    String name = CommonUtil.prettifyText(stack.getType().name());
    if(stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()) {
      name = stack.getItemMeta().getDisplayName();
    }
    return HtmlEscapers.htmlEscaper().escape(name);
  }

  public String getShopName(final ShopMetricRecord record, final DataRecord dataRecord) {

    final StringBuilder nameBuilder = new StringBuilder();
    final Shop shop = main.getQuickShop().getShopManager().getShop(record.getShopId());
    if(shop == null) {
      nameBuilder.append("[Deleted] ");
    }
    final String shopName = dataRecord.getName();
    if(shopName != null) {
      nameBuilder.append(ChatColor.stripColor(shopName));
    } else {
      if(shop != null) {
        final Location location = shop.getLocation();
        final String template = "%s %s,%s,%s";
        nameBuilder.append(String.format(template, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
      } else {
        nameBuilder.append("N/A");
      }
    }
    return HtmlEscapers.htmlEscaper().escape(nameBuilder.toString());
  }

  public String loc2String(final Location location) {

    final String template = "%s %s,%s,%s";
    return String.format(template, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public String getPlayerName(final UUID uuid) {

    if(CommonUtil.getNilUniqueId().equals(uuid)) {
      return "[Server]";
    }
    final String name = main.getQuickShop().getPlayerFinder().uuid2Name(uuid);
    if(name == null || name.isEmpty()) {
      return uuid.toString();
    }
    return HtmlEscapers.htmlEscaper().escape(name);
  }
}
