package teamroots.embers.world;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import teamroots.embers.ConfigHandler;
import teamroots.embers.entity.EntityAncientGolem;
import teamroots.embers.registry.RegistrarEmbersBlocks;
import teamroots.embers.util.Misc;

import java.util.Random;

public class WorldGenSmallRuin extends StructureBase implements IWorldGenerator {

    public WorldGenSmallRuin() {
        super(7, 7);
        this.replaceWithAir = false;
        addBlockMapping(" ", Blocks.AIR.getDefaultState());
        addBlockMapping(".", RegistrarEmbersBlocks.STRUCTURE_MARKER.getStateFromMeta(1));
        addBlockMapping("A", RegistrarEmbersBlocks.ARCHAIC_BRICKS.getDefaultState());
        addBlockMapping("T", RegistrarEmbersBlocks.ASHEN_TILE.getDefaultState());
        addBlockMapping("L", RegistrarEmbersBlocks.ARCHAIC_LIGHT.getDefaultState());
        addBlockMapping("G", RegistrarEmbersBlocks.STRUCTURE_MARKER.getStateFromMeta(0));
        addBlockMapping("R", RegistrarEmbersBlocks.ARCHAIC_EDGE.getDefaultState());
        addBlockMapping("B", RegistrarEmbersBlocks.ASHEN_BRICK.getDefaultState());
        addLayer(new String[]{
                "       ",
                " BBTBB ",
                " BTTTB ",
                " TTTTT ",
                " BTTTB ",
                " BBTBB ",
                "       ",
        });
        addLayer(new String[]{
                "  A.A  ",
                " AA.AA ",
                "AA...AA",
                "...G...",
                "AA...AA",
                " AA.AA ",
                "  A.A  ",
        });
        addLayer(new String[]{
                "  A.A  ",
                " AA.AA ",
                "AA...AA",
                ".......",
                "AA...AA",
                " AA.AA ",
                "  A.A  ",
        });
        addLayer(new String[]{
                "  AAA  ",
                " AAAAA ",
                "AA...AA",
                "AA...AA",
                "AA...AA",
                " AAAAA ",
                "  AAA  ",
        });
        addLayer(new String[]{
                "       ",
                "  RRR  ",
                " R...R ",
                " R...R ",
                " R...R ",
                "  RRR  ",
                "       ",
        });
        addLayer(new String[]{
                "       ",
                "       ",
                "  AAA  ",
                "  ALA  ",
                "  AAA  ",
                "       ",
                "       ",
        });
    }

    @Override
    public void placeBlock(World world, BlockPos pos, IBlockState state) {
        if (state.getBlock() == RegistrarEmbersBlocks.STRUCTURE_MARKER) {
            if (state.getBlock().getMetaFromState(state) == 0) {
                EntityAncientGolem golem = new EntityAncientGolem(world);
                golem.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ());
                golem.onInitialSpawn(world.getDifficultyForLocation(pos), null);
                golem.enablePersistence();
                world.spawnEntity(golem);
                world.setBlockToAir(pos);
            } else if (state.getBlock().getMetaFromState(state) == 1) {
                world.setBlockToAir(pos);
            }
        } else {
            super.placeBlock(world, pos, state);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
                         IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == DimensionType.OVERWORLD.getId() && !world.isRemote) {
            int xx = chunkX * 16 + 13 + Misc.random.nextInt(2);
            int zz = chunkZ * 16 + 13 + Misc.random.nextInt(2);
            if (ConfigHandler.structures.smallRuinChance == 0) {
                return;
            }
            if (world.getHeight(xx, zz) > 16) {
                int yy = 4 + Misc.random.nextInt(world.getHeight(xx, zz));
                if (random.nextInt(ConfigHandler.structures.smallRuinChance) == 0) {
                    if (world.getBlockState(new BlockPos(xx, yy, zz)).getBlock() instanceof BlockStone) {
                        boolean canGenerate = false;
                        BlockPos pos = new BlockPos(xx, yy, zz);
                        if (world.isAirBlock(pos.west(4)) ||
                                world.isAirBlock(pos.west(3)) ||
                                world.isAirBlock(pos.east(4)) ||
                                world.isAirBlock(pos.east(3)) ||
                                world.isAirBlock(pos.north(4)) ||
                                world.isAirBlock(pos.north(3)) ||
                                world.isAirBlock(pos.south(4)) ||
                                world.isAirBlock(pos.south(3))) {
                            canGenerate = true;
                        }
                        if (canGenerate) {
                            this.generateIn(world, xx, yy - 2, zz, false);
                        }
                    }
                }
            }
        }
    }
}
