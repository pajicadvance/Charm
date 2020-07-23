package svenhjol.charm.base;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import svenhjol.charm.module.*;
import svenhjol.meson.Meson;

@SuppressWarnings("unused")
public class CharmAsmHooks {
    public static boolean canHuskSpawnInLight(IWorld world, BlockPos pos) {
        if (Meson.enabled("charm:husk_improvements"))
            return HuskImprovements.canHuskSpawnInLight(world, pos);

        return world.canSeeSky(pos);
    }

    /**
     * Simply checks if the StackablePotions module is enabled and that the input stack can be added.
     * Returning true forces Forge's BrewingRecipeRegistry#isValidInput method to return true.
     * @param stack ItemStack to verify as valid brewing stand input.
     * @return True to force BrewingRecipeRegistry#isValidInput to be valid.
     */
    public static boolean checkBrewingStandStack(ItemStack stack) {
        return Meson.enabled("charm:stackable_potions") && StackablePotions.isValidItemStack(stack);
    }

    /**
     * StackableBooks can check if the middle (material) slot of the anvil is a stack of enchanted books.
     * If it is, it returns a stack depleted by 1 book.  If not, just returns an empty item (vanilla)
     * @param inventory The full anvil inventory. Middle slot is slot 1.
     * @return ItemStack The itemstack to set as the middle slot after anvil op is complete.
     */
    public static ItemStack getAnvilMaterialItem(IInventory inventory) {
        if (Meson.enabled("charm:stackable_books"))
            return StackableBooks.checkItemStack(inventory.getStackInSlot(1));

        return ItemStack.EMPTY;
    }

    /**
     * Overrides the vanilla default minimum XP of 1 (> 0) to zero (> -1).
     * @return -1 if NoAnvilMinimumXp is enabled, vanilla default of 0 if not enabled.
     */
    public static int getMinimumRepairCost() {
        return Meson.enabled("charm:no_anvil_minimum_xp") ? -1 : 0;
    }


    public static boolean isArmorInvisible(Entity entity, ItemStack stack) {
        return Meson.enabled("charm:lightweight_armor_invisibility")
            && LightweightArmorInvisibility.isArmorInvisible(entity, stack);
    }

    public static boolean isSignalFireInRange(World world, BlockPos pos) {
        return WanderingTraderImprovements.isSignalFireInRange(world, pos);
    }

    /**
     * If true, the potion glint will not be applied to the PotionItem.
     * Disable the RemovePotionGlint to restore vanilla behavior.
     * @return True to disable potion glint.
     */
    public static boolean removePotionGlint() {
        return Meson.enabled("charm:remove_potion_glint");
    }

}
