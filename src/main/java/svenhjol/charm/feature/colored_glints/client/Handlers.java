package svenhjol.charm.feature.colored_glints.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import svenhjol.charm.Charm;
import svenhjol.charm.feature.colored_glints.ColoredGlints;
import svenhjol.charm.feature.colored_glints.ColoredGlintsClient;
import svenhjol.charm.foundation.feature.FeatureHolder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

public final class Handlers extends FeatureHolder<ColoredGlintsClient> {
    public final Map<DyeColor, ResourceLocation> ITEM_TEXTURES = new HashMap<>();
    public final Map<DyeColor, ResourceLocation> ENTITY_TEXTURES = new HashMap<>();
    public final Map<DyeColor, RenderType> GLINT = new HashMap<>();
    public final Map<DyeColor, RenderType> ENTITY_GLINT = new HashMap<>();
    public final Map<DyeColor, RenderType> GLINT_DIRECT = new HashMap<>();
    public final Map<DyeColor, RenderType> ENTITY_GLINT_DIRECT = new HashMap<>();
    public final Map<DyeColor, RenderType> ARMOR_GLINT = new HashMap<>();
    public final Map<DyeColor, RenderType> ARMOR_ENTITY_GLINT = new HashMap<>();

    private SortedMap<RenderType, BufferBuilder> builders;
    private ItemStack targetStack;
    private boolean enabled = false;

    public static boolean initialized = false;

    public Handlers(ColoredGlintsClient feature) {
        super(feature);
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTargetStack(@Nullable ItemStack targetStack) {
        this.targetStack = targetStack;
    }

    public void setBuilders(@Nullable SortedMap<RenderType, BufferBuilder> builders) {
        this.builders = builders;
    }

    @SuppressWarnings("unused")
    public void handleClientStarted(Minecraft client) {
        // builders will be null if the mixins have been blacklisted.
        if (initialized || builders == null) return;

        for (DyeColor dyeColor : DyeColor.values()) {
            ResourceLocation itemTexture;
            ResourceLocation entityTexture;
            var colorName = dyeColor.getSerializedName().toLowerCase(Locale.ROOT);

            if (dyeColor.equals(DyeColor.PURPLE)) {
                itemTexture = ItemRenderer.ENCHANTED_GLINT_ITEM;
                entityTexture = ItemRenderer.ENCHANTED_GLINT_ENTITY;
            } else {
                itemTexture = Charm.id("textures/misc/glints/" + colorName + "_glint.png");
                entityTexture = Charm.id("textures/misc/glints/" + colorName + "_glint.png");
            }

            ITEM_TEXTURES.put(dyeColor, itemTexture);
            ENTITY_TEXTURES.put(dyeColor, entityTexture);

            GLINT.put(dyeColor, createGlint(colorName, ITEM_TEXTURES.get(dyeColor)));
            GLINT_DIRECT.put(dyeColor, createDirectGlint(colorName, ITEM_TEXTURES.get(dyeColor)));
            ENTITY_GLINT.put(dyeColor, createEntityGlint(colorName, ENTITY_TEXTURES.get(dyeColor)));
            ENTITY_GLINT_DIRECT.put(dyeColor, createDirectEntityGlint(colorName, ENTITY_TEXTURES.get(dyeColor)));
            ARMOR_GLINT.put(dyeColor, createArmorGlint(colorName, ENTITY_TEXTURES.get(dyeColor)));
            ARMOR_ENTITY_GLINT.put(dyeColor, createArmorEntityGlint(colorName, ENTITY_TEXTURES.get(dyeColor)));
        }

        initialized = true;
    }

    public RenderType createGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.create("glint_" + color, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
            .createCompositeState(false));

        getBuilders().put(renderLayer, new BufferBuilder(renderLayer.bufferSize()));
        return renderLayer;
    }

    public RenderType createEntityGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.create("entity_glint_" + color, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .createCompositeState(false));

        getBuilders().put(renderLayer, new BufferBuilder(renderLayer.bufferSize()));
        return renderLayer;
    }

    public RenderType createArmorGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.create("armor_glint_" + color, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ARMOR_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false));

        getBuilders().put(renderLayer, new BufferBuilder(renderLayer.bufferSize()));
        return renderLayer;
    }

    public RenderType createArmorEntityGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.create("armor_entity_glint_" + color, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false));

        getBuilders().put(renderLayer, new BufferBuilder(renderLayer.bufferSize()));
        return renderLayer;
    }

    public RenderType createDirectGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.create("glint_direct_" + color, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
            .createCompositeState(false));

        getBuilders().put(renderLayer, new BufferBuilder(renderLayer.bufferSize()));
        return renderLayer;
    }

    public RenderType createDirectEntityGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.create("entity_glint_direct_" + color, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .createCompositeState(false));

        getBuilders().put(renderLayer, new BufferBuilder(renderLayer.bufferSize()));
        return renderLayer;
    }

    public SortedMap<RenderType, BufferBuilder> getBuilders() {
        return builders;
    }

    public RenderType getArmorGlintRenderLayer() {
        return ARMOR_GLINT
            .getOrDefault(ColoredGlints.get(targetStack), RenderType.ARMOR_GLINT);
    }

    public RenderType getArmorEntityGlintRenderLayer() {
        return ARMOR_ENTITY_GLINT
            .getOrDefault(ColoredGlints.get(targetStack), RenderType.ARMOR_ENTITY_GLINT);
    }

    public RenderType getDirectGlintRenderLayer() {
        return GLINT_DIRECT
            .getOrDefault(ColoredGlints.get(targetStack), RenderType.GLINT_DIRECT);
    }

    public RenderType getDirectEntityGlintRenderLayer() {
        return ENTITY_GLINT_DIRECT
            .getOrDefault(ColoredGlints.get(targetStack), RenderType.ENTITY_GLINT_DIRECT);
    }

    public RenderType getEntityGlintRenderLayer() {
        return ENTITY_GLINT
            .getOrDefault(ColoredGlints.get(targetStack), RenderType.ENTITY_GLINT);
    }

    public RenderType getGlintRenderLayer() {
        return GLINT
            .getOrDefault(ColoredGlints.get(targetStack), RenderType.GLINT);
    }
}
