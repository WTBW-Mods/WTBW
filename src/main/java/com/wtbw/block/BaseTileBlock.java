package com.wtbw.block;

import com.wtbw.tile.util.IContentHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/*
  @author: Naxanria
*/
public class BaseTileBlock<TE extends TileEntity> extends Block
{
  public final TileEntityProvider<TE> tileEntityProvider;
  protected boolean hasGui = true;

  public BaseTileBlock(Properties properties, TileEntityProvider<TE> tileEntityProvider)
  {
    super(properties);

    this.tileEntityProvider = tileEntityProvider;
  }

  @Override
  public boolean hasTileEntity(BlockState state)
  {
    return true;
  }

  @Nullable
  @Override
  public TE createTileEntity(BlockState state, IBlockReader world)
  {
    return tileEntityProvider.get(world, state);
  }

  protected TE getTileEntity(IBlockReader world, BlockPos pos)
  {
    return (TE) world.getTileEntity(pos);
  }
  
  @Override
  public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit)
  {
    if (hasGui)
    {
      if (!playerEntity.isCrouching())
      {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof INamedContainerProvider)
        {
          if (!world.isRemote)
          {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, (INamedContainerProvider) tileEntity, tileEntity.getPos());
            onGuiOpen(state, world, pos, ((ServerPlayerEntity) playerEntity), hand, hit);
          }

          return ActionResultType.SUCCESS;
        }
      }
    }

    return super.func_225533_a_(state, world, pos, playerEntity, hand, hit);
  }
  
  protected void onGuiOpen(BlockState state, World world, BlockPos pos, ServerPlayerEntity player, Hand hand, BlockRayTraceResult hit)
  { }
  
  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
  {
    if (state.getBlock() != newState.getBlock())
    {
      TE tileEntity = getTileEntity(worldIn, pos);
      if (tileEntity != null && tileEntity instanceof IContentHolder)
      {
        ((IContentHolder) tileEntity).dropContents();
      }
    }
    
    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }
  
  
  public interface TileEntityProvider<TE extends TileEntity>
  {
    default TE get()
    {
      return get(null, null);
    }

    TE get(IBlockReader world, BlockState state);
  }
}
