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

package com.peffern.fixnode.asm;

import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import com.peffern.fixnode.FixNode;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class MekanismCoreCT implements IClassTransformer
{
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		
		if(name.equals("mekanism.common.Mekanism"))
		{
			return asmify(basicClass);
		}
		else
			return basicClass;
	}
	
	private byte[] asmify(byte[] bytes)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		for(MethodNode m : classNode.methods)
		{
			if(m.name.equals("addRecipes") && m.desc.equals("()V"))
			{
				ListIterator<AbstractInsnNode> it = m.instructions.iterator();
				while(it.hasNext())
				{
					AbstractInsnNode i = it.next();
					
					//look for a LdcInsnNode containing the String "water" followed by an IntInsnNode
					//only one should exist: the call to add the solar evaporation recipe
					
					
					if(i instanceof LdcInsnNode)
					{
						LdcInsnNode node = (LdcInsnNode)i;
						Object obj = node.cst;
						if(obj.equals("water"))
						{
							AbstractInsnNode next = i.getNext();
							if(next instanceof IntInsnNode)
							{
								//replace the node with a new LdcInsnNode containing the String "saltwater"
								
								LdcInsnNode newNode = new LdcInsnNode("saltwater");
								m.instructions.insert(node, newNode);
								m.instructions.remove(node);


							}
							

						}
					}
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
	}

}
