package com.wtbw.block;

import com.wtbw.WTBW;
import com.wtbw.util.PlayEvent;
import com.wtbw.util.RandomUtil;
import com.wtbw.util.TextComponentBuilder;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.impl.ParticleCommand;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/*
  @author: Naxanria
*/
public class GreenHouseGlass extends AbstractGlassBlock implements IBeaconBeamColorProvider
{
  public GreenHouseGlass(Properties properties)
  {
    super(properties.tickRandomly());
  }
  
  @Override
  public BlockRenderLayer getRenderLayer()
  {
    return BlockRenderLayer.TRANSLUCENT;
  }
  
  @Override
  public DyeColor getColor()
  {
    return DyeColor.GREEN;
  }
  
  @Override
  public void randomTick(BlockState state, World world, BlockPos pos, Random rand)
  {
    int maxRange = 25;
    int chance = 40;
    
    if (!world.isRemote)
    {
      if (!world.isDaytime())
      {
        return;
      }
      
      if (RandomUtil.chance(rand, chance))
      {
        if (world.canBlockSeeSky(pos.up()))
        {
          // FIXME: Particles
          for (int i = 1; i < maxRange; i++)
          {
            BlockPos check = pos.down(i);
            BlockState foundState = world.getBlockState(check);
            Block block = foundState.getBlock();
            if (block instanceof CropsBlock)
            {
              if (!((CropsBlock) block).isMaxAge(foundState))
              {
                PlayEvent.boneMeal(world, check, rand.nextInt(7) + 5);
                ((CropsBlock) block).grow(world, check, foundState);
              }
              
              break;
            }
            if (block instanceof SaplingBlock)
            {
              PlayEvent.boneMeal(world, check, rand.nextInt(7) + 5);
              ((SaplingBlock) block).grow(world, check, foundState, rand);

              break;
            }
          }
        }
      }
    }
  }
  
  @Override
  public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
  {
    String baseKey = WTBW.MODID + ".tooltip.greenhouse_glass";
    tooltip.add(TextComponentBuilder.createTranslated(baseKey).aqua().build());
    
    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
  
}