package maxanvilcfg.mixins;

import maxanvilcfg.MaxAnvilLevelConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Map;

@Mixin(value = ContainerRepair.class, priority = 1001)
public class MixinContainerRepair {

	/**
	 * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
	 */
	@Overwrite
	public void updateRepairOutput()
	{

		ContainerRepair repair = (ContainerRepair)(Object)(this);

		IInventory inputSlots = ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, repair, "inputSlots", "field_82853_g");
		IInventory outputSlot = ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, repair, "outputSlot", "field_82852_f");
		EntityPlayer player = ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, repair, "player", "field_82855_n");
		String repairedItemName = ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, repair, "repairedItemName", "field_82857_m");

		ItemStack itemstack = inputSlots.getStackInSlot(0);
		ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, 1, "maximumCost", "field_82854_e");
		//this.maximumCost = 1;
		int i = 0;
		int j = 0;
		int k = 0;

		if (itemstack.isEmpty())
		{
			outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
			ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, 0, "maximumCost", "field_82854_e");
			//this.maximumCost = 0;
		}
		else
		{
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = inputSlots.getStackInSlot(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
			ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, 0, "materialCost", "field_82856_l");
			//this.materialCost = 0;
			boolean flag = false;

			if (!itemstack2.isEmpty())
			{
				if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(repair, itemstack, itemstack2, outputSlot, repairedItemName, j)) return;
				flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).hasNoTags();

				if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2))
				{
					int l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);

					if (l2 <= 0)
					{
						outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
						ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, 0, "maximumCost", "field_82854_e");
						//this.maximumCost = 0;
						return;
					}

					int i3;

					for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3)
					{
						int j3 = itemstack1.getItemDamage() - l2;
						itemstack1.setItemDamage(j3);
						++i;
						l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
					}

					ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, i3, "materialCost", "field_82856_l");
					//this.materialCost = i3;
				}
				else
				{
					if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable()))
					{
						outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
						ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, 0, "maximumCost", "field_82854_e");
						//this.maximumCost = 0;
						return;
					}

					if (itemstack1.isItemStackDamageable() && !flag)
					{
						int l = itemstack.getMaxDamage() - itemstack.getItemDamage();
						int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
						int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
						int k1 = l + j1;
						int l1 = itemstack1.getMaxDamage() - k1;

						if (l1 < 0)
						{
							l1 = 0;
						}

						if (l1 < itemstack1.getItemDamage()) // vanilla uses metadata here instead of damage.
						{
							itemstack1.setItemDamage(l1);
							i += 2;
						}
					}

					Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
					boolean flag2 = false;
					boolean flag3 = false;

					for (Enchantment enchantment1 : map1.keySet())
					{
						if (enchantment1 != null)
						{
							int i2 = map.containsKey(enchantment1) ? ((Integer)map.get(enchantment1)).intValue() : 0;
							int j2 = ((Integer)map1.get(enchantment1)).intValue();
							j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
							boolean flag1 = enchantment1.canApply(itemstack);

							if (player.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK)
							{
								flag1 = true;
							}

							for (Enchantment enchantment : map.keySet())
							{
								if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment))
								{
									flag1 = false;
									++i;
								}
							}

							if (!flag1)
							{
								flag3 = true;
							}
							else
							{
								flag2 = true;

								if (j2 > enchantment1.getMaxLevel())
								{
									j2 = enchantment1.getMaxLevel();
								}

								map.put(enchantment1, Integer.valueOf(j2));
								int k3 = 0;

								switch (enchantment1.getRarity())
								{
									case COMMON:
										k3 = 1;
										break;
									case UNCOMMON:
										k3 = 2;
										break;
									case RARE:
										k3 = 4;
										break;
									case VERY_RARE:
										k3 = 8;
								}

								if (flag)
								{
									k3 = Math.max(1, k3 / 2);
								}

								i += k3 * j2;

								if (itemstack.getCount() > 1)
								{
									i = 40;
								}
							}
						}
					}

					if (flag3 && !flag2)
					{
						outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
						ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, 0, "maximumCost", "field_82854_e");
						//this.maximumCost = 0;
						return;
					}
				}
			}

			if (StringUtils.isBlank(repairedItemName))
			{
				if (itemstack.hasDisplayName())
				{
					k = 1;
					i += k;
					itemstack1.clearCustomName();
				}
			}
			else if (!repairedItemName.equals(itemstack.getDisplayName()))
			{
				k = 1;
				i += k;
				itemstack1.setStackDisplayName(repairedItemName);
			}
			if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2)) itemstack1 = ItemStack.EMPTY;

			ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, j + i, "maximumCost", "field_82854_e");

			//this.maximumCost = j + i;

			if (i <= 0)
			{
				itemstack1 = ItemStack.EMPTY;
			}

			if (k == i && k > 0 && 	(int)ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, repair, "maximumCost", "field_82854_e") >= MaxAnvilLevelConfig.maximumAnvilLevel)
			{
				ObfuscationReflectionHelper.setPrivateValue(ContainerRepair.class, repair, MaxAnvilLevelConfig.maximumAnvilLevel - 1, "maximumCost", "field_82854_e");
				//this.maximumCost = 39;
			}

			if ((int)ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, repair, "maximumCost", "field_82854_e") >= MaxAnvilLevelConfig.maximumAnvilLevel && !player.capabilities.isCreativeMode)
			{
				itemstack1 = ItemStack.EMPTY;
			}

			if (!itemstack1.isEmpty())
			{
				int k2 = itemstack1.getRepairCost();

				if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost())
				{
					k2 = itemstack2.getRepairCost();
				}

				if (k != i || k == 0)
				{
					k2 = k2 * 2 + 1;
				}

				itemstack1.setRepairCost(k2);
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}

			outputSlot.setInventorySlotContents(0, itemstack1);
			repair.detectAndSendChanges();
		}
	}

}
