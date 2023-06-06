package atonkish.quartzelv.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import atonkish.quartzelv.QuartzElevatorMod;
import atonkish.quartzelv.util.Teleport;
import atonkish.quartzelv.util.VerticalTeleporter;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    private void jump(CallbackInfo info) {
        if (!(this.getWorld() instanceof ServerWorld)) {
            return;
        }

        // `isPlayerOnly`: false -> all entities can teleport
        // `isPlayerOnly`: true -> only player entities can teleport
        if (QuartzElevatorMod.CONFIG.isPlayerOnly && !this.getClass().equals(ServerPlayerEntity.class)) {
            return;
        }

        VerticalTeleporter verticalTeleporter = (Double y) -> {
            this.refreshPositionAfterTeleport(this.getX(), y, this.getZ());
            this.teleport(this.getX(), y, this.getZ());
            return (Void) null;
        };
        Teleport.teleportUp(this.getWorld(), this.getBlockPos(), this.getBoundingBox(), verticalTeleporter);
    }
}
