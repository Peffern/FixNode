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

package com.peffern.fixnode;

import com.bluepowermod.tile.tier1.TileAlloyFurnace;
import com.peffern.fixnode.core.Adapters;
import com.peffern.fixnode.core.Adapters.AlloyFurnaceAdapter;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = FixNode.MODID, name = FixNode.MODNAME, version = FixNode.VERSION, dependencies = "required-after:" + "Mekanism" + ";" + "required-after:" + "terrafirmacraft" + ";" + "required-after:" + "Railcraft" + ";" + "required-after:" + "Forestry" + ";" + "required-after:" + "bluepower" + ";" + "required-after:" + "ImmersiveEngineering")
public class FixNode
{	
    public static final String MODID = "FixNode";
    public static final String MODNAME = "FixNode";
    public static final String VERSION = "1.1";
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	RecipeManagers.stillManager.addRecipe(10, FluidRegistry.getFluidStack("freshwater", 100), FluidRegistry.getFluidStack("water", 70));
    	RecipeManagers.stillManager.addRecipe(20, FluidRegistry.getFluidStack("saltwater", 100), FluidRegistry.getFluidStack("water", 60));
    	RecipeManagers.stillManager.addRecipe(10, FluidRegistry.getFluidStack("hotwater", 100), FluidRegistry.getFluidStack("water", 80));
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		ExternalHeaterHandler.registerHeatableAdapter(TileAlloyFurnace.class, new Adapters.AlloyFurnaceAdapter());

    }
}
