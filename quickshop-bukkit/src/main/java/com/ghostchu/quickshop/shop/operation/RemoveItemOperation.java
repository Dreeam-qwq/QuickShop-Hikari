package com.ghostchu.quickshop.shop.operation;

import com.ghostchu.quickshop.api.inventory.InventoryWrapper;
import com.ghostchu.quickshop.api.operation.Operation;
import com.ghostchu.quickshop.util.Util;
import com.ghostchu.quickshop.util.logger.Log;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

/**
 * Operation to remove items
 */
@NullMarked
public class RemoveItemOperation implements Operation {

  private final ItemStack item;
  private final int amount;
  private final InventoryWrapper inv;
  private final int itemMaxStackSize;
  private boolean committed;
  private boolean rollback;
  private ItemStack @Nullable [] snapshot;

  /**
   * Constructor
   *
   * @param item   ItemStack to remove
   * @param amount Amount to remove
   * @param inv    The {@link InventoryWrapper} that remove from
   */
  public RemoveItemOperation(final ItemStack item, final int amount, final InventoryWrapper inv) {

    this.item = item.clone();
    this.amount = amount;
    this.inv = inv;
    this.itemMaxStackSize = Util.getItemMaxStackSize(item.getType());

  }

  @Override
  public boolean commit() {

    committed = true;
    this.snapshot = inv.createSnapshot();
    int remains = amount;
    int lastRemains = -1;
    while(remains > 0) {
      final int stackSize = Math.min(remains, itemMaxStackSize);
      item.setAmount(stackSize);
      Log.debug("Committing remove item operation, remains: " + remains + ", stackSize: " + stackSize + ", target: " + item);
      final Map<Integer, ItemStack> notFit = inv.removeItem(item.clone());
      if(notFit.isEmpty()) {
        remains -= stackSize;
      } else {
        remains -= stackSize - notFit.entrySet().iterator().next().getValue().getAmount();
      }
      if(remains == lastRemains) {
        return false;
      }
      lastRemains = remains;
    }
    return true;
  }

  @Override
  public boolean isCommitted() {

    return this.committed;
  }

  @Override
  public boolean isRollback() {

    return this.rollback;
  }

  @Override
  public boolean rollback() {

    rollback = true;
    return inv.restoreSnapshot(Objects.requireNonNull(snapshot));
  }
}
