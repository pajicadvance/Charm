package svenhjol.charm.feature.woodcutters.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import svenhjol.charm.feature.woodcutters.WoodcuttersClient;
import svenhjol.charm.feature.woodcutting.Woodcutting;
import svenhjol.charm.foundation.Resolve;
import svenhjol.charm.foundation.feature.RegisterHolder;

public final class Registers extends RegisterHolder<WoodcuttersClient> {
    public Registers(WoodcuttersClient feature) {
        super(feature);
        var woodcutting = Resolve.feature(Woodcutting.class);
        var registry = feature.registry();
        var common = feature.common();

        registry.recipeBookCategoryEnum("woodcutter_search", () -> Items.COMPASS);
        registry.recipeBookCategoryEnum("woodcutter", common.registers.block);

        registry.menuScreen(common.registers.menu, () -> Screen::new);
        registry.recipeBookCategory("woodcutter", woodcutting.registers.recipeType, common.registers.recipeBookType);

        // The woodcutter block should render transparent areas cut out.
        registry.blockRenderType(common.registers.block, RenderType::cutout);
    }

    @Override
    public void onEnabled() {
        feature().registry().itemTab(
            feature().common().registers.block,
            CreativeModeTabs.FUNCTIONAL_BLOCKS,
            Items.STONECUTTER
        );
    }
}
