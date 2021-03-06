package com.wtbw.block.redstone;

import com.wtbw.WTBW;
import com.wtbw.config.CommonConfig;
import com.wtbw.util.Colors;
import com.wtbw.util.PlayEvent;
import com.wtbw.util.RandomUtil;
import com.wtbw.util.TextComponentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/*
  @author: Naxanria
*/
public class RedstoneEmitterBlock extends Block
{
  public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
  
  public RedstoneEmitterBlock(Properties properties)
  {
    super(properties.tickRandomly());
    
    setDefaultState(stateContainer.getBaseState().with(POWER, 0));
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
  {
    builder.add(POWER);
  }
  
  @Override
  public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side)
  {
    return state.get(POWER);
  }
  
  @Override
  public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
  {
    if (player.isAllowEdit() && hand != Hand.OFF_HAND)
    {
      if (!world.isRemote)
      {
        if (player.getHeldItem(hand).isEmpty())
        {
          BlockState newState;
          if (player.isCrouching())
          {
            int next = state.get(POWER) - 1;
            if (next < 0)
            {
              next = 15;
            }
            newState = state.with(POWER, next);
          }
          else
          {
            newState = state.cycle(POWER);
          }
        
          world.setBlockState(pos, newState, 4);
          world.notifyNeighbors(pos, this);
          return ActionResultType.SUCCESS;
        }
      }
      else
      {
        return ActionResultType.SUCCESS;
      }
    }
    
    return super.func_225533_a_(state, world, pos, player, hand, rayTraceResult);
  }
  
  @Override
  public boolean canProvidePower(BlockState state)
  {
    return true;
  }
  
  
  @Override
  public void animateTick(BlockState state, World world, BlockPos pos, Random random)
  {
    int power = state.get(POWER);
    if (power == 0)
    {
      return;
    }
    
    int particles = (power - 5) / 3;
    if (particles < 1)
    {
      particles = 1;
    }
  
    for (int i = 0; i < particles; i++)
    {
      PlayEvent.redstoneParticle(world, pos.getX() + random.nextDouble(), pos.getY() + 1.1, pos.getZ() + random.nextDouble(), 0, random.nextDouble() * .2, 0, Colors.RED);
    }
  }
  
  @Override
  public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
  {
    String baseKey = WTBW.MODID + ".tooltip.emitter";
    tooltip.add(TextComponentBuilder.createTranslated(baseKey).aqua().build());
    
    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
  
}
