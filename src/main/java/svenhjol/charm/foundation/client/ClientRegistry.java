package svenhjol.charm.foundation.client;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import svenhjol.charm.foundation.Log;
import svenhjol.charm.foundation.Registry;
import svenhjol.charm.foundation.helper.EnumHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public final class ClientRegistry implements Registry {
    private static final List<Pair<String, ItemLike>> RECIPE_BOOK_CATEGORY_ENUMS = new ArrayList<>();
    private static final Map<RecipeBookType, List<RecipeBookCategories>> RECIPE_BOOK_CATEGORY_BY_TYPE = new HashMap<>();
    private static final Map<RecipeType<?>, RecipeBookCategories> RECIPE_BOOK_MAIN_CATEGORY = new HashMap<>();

    private final String id;
    private final Log log;

    public ClientRegistry(String id) {
        this.id = id;
        this.log = new Log(id, this);
    }

    public <T extends BlockEntity> void blockEntityRenderer(Supplier<BlockEntityType<T>> supplier, Supplier<BlockEntityRendererProvider<T>> provider) {
        BlockEntityRenderers.register(supplier.get(), provider.get());
    }

    public <T extends Block> void blockRenderType(Supplier<T> block, Supplier<RenderType> renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block.get(), renderType.get());
    }

    public <T extends Entity> void entityRenderer(Supplier<EntityType<T>> entity, Supplier<EntityRendererProvider<T>> provider) {
        EntityRendererRegistry.register(entity.get(), provider.get());
    }

    public ResourceLocation id(String path) {
        return new ResourceLocation(this.id, path);
    }

    public <T extends ItemLike> void itemTab(Supplier<T> item, ResourceKey<CreativeModeTab> key, @Nullable ItemLike showAfter) {
        if (showAfter != null) {
            ItemGroupEvents.modifyEntriesEvent(key)
                .register(entries -> entries.addAfter(showAfter, item.get()));
        } else {
            ItemGroupEvents.modifyEntriesEvent(key)
                .register(entries -> entries.accept(item.get()));
        }
    }

    public Supplier<ModelLayerLocation> modelLayer(Supplier<ModelLayerLocation> location, Supplier<LayerDefinition> definition) {
        EntityModelLayerRegistry.registerModelLayer(location.get(), definition::get);
        return location;
    }

    public <R extends Recipe<?>> void recipeBookCategory(String id, Supplier<RecipeType<R>> recipeType, Supplier<RecipeBookType> recipeBookType) {
        var upper = id.toUpperCase(Locale.ROOT);
        var searchCategory = EnumHelper.getValueOrDefault(() -> RecipeBookCategories.valueOf(upper + "_SEARCH"), RecipeBookCategories.CRAFTING_SEARCH);
        var mainCategory = EnumHelper.getValueOrDefault(() -> RecipeBookCategories.valueOf(upper), RecipeBookCategories.CRAFTING_MISC);

        RECIPE_BOOK_MAIN_CATEGORY.put(recipeType.get(), mainCategory);
        RECIPE_BOOK_CATEGORY_BY_TYPE.put(recipeBookType.get(), List.of(searchCategory, mainCategory));

        var aggregateCategories = new HashMap<>(RecipeBookCategories.AGGREGATE_CATEGORIES);
        aggregateCategories.put(searchCategory, List.of(mainCategory));
        RecipeBookCategories.AGGREGATE_CATEGORIES = aggregateCategories;
    }

    public static List<Pair<String, ItemLike>> recipeBookCategoryEnums() {
        return RECIPE_BOOK_CATEGORY_ENUMS;
    }

    public static Map<RecipeBookType, List<RecipeBookCategories>> recipeBookCategoryByType() {
        return RECIPE_BOOK_CATEGORY_BY_TYPE;
    }

    public static Map<RecipeType<?>, RecipeBookCategories> recipeBookMainCategory() {
        return RECIPE_BOOK_MAIN_CATEGORY;
    }

    public void signMaterial(Supplier<WoodType> woodType) {
        Sheets.SIGN_MATERIALS.put(woodType.get(), new Material(Sheets.SIGN_SHEET, new ResourceLocation("entity/signs/" + woodType.get().name())));
        Sheets.HANGING_SIGN_MATERIALS.put(woodType.get(), new Material(Sheets.SIGN_SHEET, new ResourceLocation("entity/signs/hanging/" + woodType.get().name())));
    }
}
