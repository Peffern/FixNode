/*
 *  Copyright (C) 2016 Peffern

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.peffern.fixnode.core;

import java.util.Iterator;

import com.bluepowermod.api.recipe.IAlloyFurnaceRecipe;
import com.bluepowermod.recipe.AlloyFurnaceRegistry;
import com.bluepowermod.tile.tier1.TileAlloyFurnace;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler.HeatableAdapter;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class Adapters
{
	public static class AlloyFurnaceAdapter extends HeatableAdapter<TileAlloyFurnace>
	{
		boolean canCook(TileAlloyFurnace tileEntity)
		{
			ItemStack[] inputs = new ItemStack[9];
			
			for(int i = 0; i < 9; i++)
			{
				inputs[i] = tileEntity.getStackInSlot(i+2);
			}
		
			
			IAlloyFurnaceRecipe recipe = null;
			
			Iterator<IAlloyFurnaceRecipe> recipes = AlloyFurnaceRegistry.getInstance().getAllRecipes().iterator();
			
			while(recipes.hasNext())
			{
				IAlloyFurnaceRecipe next = recipes.next();
				if(next.matches(inputs))
				{
					recipe = next;
					break;
				}
			}
						
			if(recipe == null)
				return false;
			
			ItemStack output = recipe.getCraftingResult(inputs);
			
			ItemStack existingOutput = tileEntity.getStackInSlot(1);
			if(existingOutput == null)
				return true;
			
			if(!existingOutput.isItemEqual(output))
				return false;
			
			int stackSize = existingOutput.stackSize + output.stackSize;
			
			return stackSize <= tileEntity.getInventoryStackLimit() && stackSize <= output.getMaxStackSize();
		}

		@Override
		public int doHeatTick(TileAlloyFurnace tileEntity, int energyAvailable, boolean redstone)
		{
			
			int energyConsumed = 0;
			boolean canCook = canCook(tileEntity);
			if(canCook||redstone)
			{
				boolean burning = tileEntity.getIsActive();
				int burnTime = tileEntity.currentBurnTime;
				if(burnTime<200)
				{
					int heatAttempt = 4;
					int heatEnergyRatio = Math.max(1, 8);
					int energyToUse = Math.min(energyAvailable, heatAttempt*heatEnergyRatio);
					int heat = energyToUse/heatEnergyRatio;
					if(heat>0)
					{
						tileEntity.currentBurnTime = burnTime+heat;
						energyConsumed += heat*heatEnergyRatio;
						if(!burning)
							updateFurnace(tileEntity, tileEntity.currentBurnTime>0);
					}
				}
				if(canCook&&tileEntity.currentBurnTime>=200&&tileEntity.currentProcessTime<199)
				{
					int energyToUse = 24;
					if(energyAvailable-energyConsumed > energyToUse)
					{
						energyConsumed += energyToUse;
						tileEntity.currentProcessTime = tileEntity.currentProcessTime+1;
					}
				}
			}
			return energyConsumed;			
		}
		public void updateFurnace(TileEntity tileEntity, boolean active)
		{
			Block containing = tileEntity.getBlockType();
//			if(containing==Blocks.furnace)
//				BlockFurnace.setState(active, tileEntity.getWorld(), tileEntity.getPos());
//			else
			{
				//Fix for Natura, might work on other furnaces that extend the vanilla one and use the variable name "active". Let's hope. xD
				NBTTagCompound nbt = new NBTTagCompound();
				tileEntity.writeToNBT(nbt);
				nbt.setBoolean("active", active);
				nbt.setBoolean("Active", active);
				tileEntity.readFromNBT(nbt);
				tileEntity.getWorldObj().markBlockForUpdate(tileEntity.xCoord,tileEntity.yCoord,tileEntity.zCoord);
			}
		}
		
	}
}
