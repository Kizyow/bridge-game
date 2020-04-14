package fr.watch54.bridge.managers;

import fr.watch54.bridge.Bridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class BlockManager {

    private Bridge bridge;
    private List<Block> placedBlock = new ArrayList<>();

    public BlockManager(Bridge bridge){
        this.bridge = bridge;

    }

    public void addBlock(Block block){
        if(!containsBlock(block)){
            placedBlock.add(block);
            block.setMetadata("placed", new FixedMetadataValue(bridge, true));

        }

    }

    public void removeAllBlock(){
        for(Block block : placedBlock) block.setType(Material.AIR);

    }

    public boolean isPlacedByPlayer(Block block){
        return block.hasMetadata("placed");

    }

    public boolean containsBlock(Block block){
        return placedBlock.contains(block);

    }

}
