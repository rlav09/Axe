package net.minehorizon.modules.ljaxe;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

enum IterableFaces {
    NORTH(BlockFace.NORTH),
    EAST(BlockFace.EAST),
    SOUTH(BlockFace.SOUTH),
    WEST(BlockFace.WEST);

    private BlockFace linkedFace;
    IterableFaces(BlockFace linkedFace) {
        this.linkedFace = linkedFace;
    }

    /**
     * Get corresponding block face for each cardinal direction.
     *
     * @return BlockFace
     */
    public BlockFace getLinkedFace() {
        return linkedFace;
    }
}

class MaterialHandler {
    /**
     * Used to check if block material name contains LOG - further recursion may be required to identify edge-cases.
     *
     * @param block - Block placed under evaluation.
     * @return boolean
     */
    public static boolean isWooden(Block block) {
        return block.getType().name().toLowerCase().contains("log");
    }
}

class AxeHandler {
    /**
     * Get blocks in each cardinal direction to original block broken.
     *
     * @param centreBlock - Block to get immediate blocks (via linkedFace) from.
     * @return HashSet<Block>
     */
    private HashSet<Block> getImmediateBlocks(Block centreBlock) {
        HashSet<Block> blockSet = new HashSet<>();
        for(IterableFaces face : IterableFaces.values()) {
            Block evaluatedBlock = centreBlock.getRelative(face.getLinkedFace());

            if(evaluatedBlock.getType() != Material.AIR) {
                if(MaterialHandler.isWooden(evaluatedBlock))
                    blockSet.add(evaluatedBlock);
            }
        }

        return blockSet;
    }

    /**
     * Used to get all affected blocks.
     *
     * @param centreBlock - Block broken by user to start counting from.
     * @param blockCount - Max number of blocks to break.
     * @return HashSet<Block>
     */
    public HashSet<Block> getAffectedBlocks(Block centreBlock, int blockCount) {
        HashSet<Block> blockSet = new HashSet<>();
        for(int i = 0; i < blockCount; i++) {
            //TODO get rest of block selection using block-counting and @getImmediateBlocks method
        }

        return blockSet;
    }
}

public class Axe implements Listener {
    /**
     * Class constructor, automatically create a new instance of AxeHandler class when Axe class is loaded.
     */
    private AxeHandler axeHandler;
    public Axe() {
        this.axeHandler = new AxeHandler();
    }

    /**
     * Event handler
     *
     * @param event - Auto-supplied when event is called.
     */
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();

        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if(itemStack.getType() == Material.AIR) return;
        //TODO Check current item to see if it is correct axe/tool

        Block block = event.getBlock();
        HashSet<Block> affectedBlocks = axeHandler.getAffectedBlocks(block, 10 /*TODO Change*/);
        if(affectedBlocks.size() < 1) return;

        for(Block affected : affectedBlocks) {
            affected.breakNaturally(itemStack);
            player.playSound(player.getLocation(), Sound.BLOCK_NETHER_GOLD_ORE_BREAK, 1F, 1F);
        }
    }
}
