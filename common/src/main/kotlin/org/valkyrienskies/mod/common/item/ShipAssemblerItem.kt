package org.valkyrienskies.mod.common.item

import net.minecraft.Util
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.game.ChunkAllocator
import org.valkyrienskies.mod.common.assembly.createNewShipWithBlocks
import org.valkyrienskies.mod.common.dimensionId

class ShipAssemblerItem(properties: Properties) : Item(properties) {

    override fun useOn(ctx: UseOnContext): InteractionResult {
        val level = ctx.level as? ServerLevel ?: return super.useOn(ctx)
        val pos = ctx.clickedPos
        val blockState: BlockState = level.getBlockState(pos)

        if (!level.isClientSide) {
            if (ChunkAllocator.isChunkInShipyard(pos.x shr 4, pos.z shr 4)) {
                ctx.player?.sendMessage(TextComponent("That chunk is already part of a ship!"), Util.NIL_UUID)
            } else if (!blockState.isAir) {
                // Make a ship
                val dimensionId = level.dimensionId
                val set = DenseBlockPosSet()
                for (x in -1..1) {
                    for (z in -1..1) {
                        set.add(pos.x + x, pos.y, pos.z + z)
                    }
                }

                val shipData = createNewShipWithBlocks(pos, set, level)

                ctx.player?.sendMessage(TextComponent("SHIPIFIED!"), Util.NIL_UUID)
            }
        }

        return super.useOn(ctx)
    }
}
