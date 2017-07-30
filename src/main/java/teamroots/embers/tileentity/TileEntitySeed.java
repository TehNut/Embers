package teamroots.embers.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import teamroots.embers.EventManager;
import teamroots.embers.block.BlockSeed;
import teamroots.embers.registry.RegistrarEmbersItems;

import javax.annotation.Nullable;
import java.util.Random;

public class TileEntitySeed extends TileEntity implements ITileEntityBase, ITickable {
    public boolean dirty = false;
    int size = 0;
    int ticksExisted = 0;
    int material = -1;
    boolean[] willSpawn = new boolean[12];
    Random random = new Random();

    public TileEntitySeed() {
        super();
        resetSpawns();
    }

    public void resetSpawns() {
        for (int i = 0; i < 12; i++) {
            willSpawn[i] = random.nextInt(3) == 0;
        }
    }

    public TileEntitySeed setMaterial(int material) {
        this.material = material;
        return this;
    }

    public String getSpawnString() {
        String result = "";
        for (int i = 0; i < 12; i++) {
            result += willSpawn[i] ? "1" : "0";
        }
        return result;
    }

    public void loadSpawnsFromString(String s) {
        for (int i = 0; i < 12; i++) {
            willSpawn[i] = s.substring(i, i + 1).compareTo("1") == 0;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("spawns", getSpawnString());
        tag.setInteger("size", size);
        tag.setInteger("material", material);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        loadSpawnsFromString(tag.getString("spawns"));
        size = tag.getInteger("size");
        material = tag.getInteger("material");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.invalidate();
        world.setTileEntity(pos, null);
    }

    @Override
    public void update() {
        if (material == -1) {
            material = world.getBlockState(getPos()).getValue(BlockSeed.type);
        }
        ticksExisted++;
        if (size > 1000) {
            size = 0;
            for (int i = 0; i < 12; i++) {
                if (willSpawn[i] && !getWorld().isRemote) {
                    ItemStack nuggetStack = ItemStack.EMPTY;
                    if (material == 0) {
                        nuggetStack = new ItemStack(RegistrarEmbersItems.NUGGET_IRON, 1);
                    }
                    if (material == 1) {
                        nuggetStack = new ItemStack(Items.GOLD_NUGGET, 1);
                    }
                    if (material == 2) {
                        nuggetStack = new ItemStack(RegistrarEmbersItems.NUGGET_COPPER, 1);
                    }
                    if (material == 3) {
                        nuggetStack = new ItemStack(RegistrarEmbersItems.NUGGET_LEAD, 1);
                    }
                    if (material == 4) {
                        nuggetStack = new ItemStack(RegistrarEmbersItems.NUGGET_SILVER, 1);
                    }
                    float offX = 0.4f * (float) Math.sin(Math.toRadians(i * 30.0));
                    float offZ = 0.4f * (float) Math.cos(Math.toRadians(i * 30.0));
                    world.spawnEntity(new EntityItem(world, getPos().getX() + 0.5 + offX, getPos().getY() + 0.5f, getPos().getZ() + 0.5 + offZ, nuggetStack));
                }
            }
            resetSpawns();
            markDirty();
        }
    }

    @Override
    public void markForUpdate() {
        EventManager.markTEForUpdate(getPos(), this);
    }

    @Override
    public void markDirty() {
        markForUpdate();
        super.markDirty();
    }
}
