package mekanism.client.render.tileentity;

import java.util.HashMap;
import java.util.Map;

import mekanism.api.Coord4D;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DisplayInteger;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.common.tank.TankUpdateProtocol;
import mekanism.common.tile.TileEntitySalinationController;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSalinationController extends TileEntitySpecialRenderer
{
	private static Map<SalinationRenderData, HashMap<Fluid, DisplayInteger[]>> cachedCenterFluids = new HashMap<SalinationRenderData, HashMap<Fluid, DisplayInteger[]>>();
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
	{
		renderAModelAt((TileEntitySalinationController)tileEntity, x, y, z, partialTick);
	}
	
	public void renderAModelAt(TileEntitySalinationController tileEntity, double x, double y, double z, float partialTick)
	{		
		/*if(tileEntity.structured && tileEntity.waterTank.getFluid() != null)
		{
			SalinationRenderData data = new SalinationRenderData();
			
			data.height = tileEntity.height-2;
			
			bindTexture(MekanismRenderer.getBlocksTexture());
			
			if(data.height >= 2)
			{
				Coord4D renderLoc = tileEntity.getRenderLocation();
				
				push();
				
				GL11.glTranslated(getX(renderLoc.xCoord), getY(renderLoc.yCoord), getZ(renderLoc.zCoord));
				
				MekanismRenderer.glowOn(tileEntity.waterTank.getFluid().getFluid().getLuminosity());
				
				DisplayInteger[] displayList = getListAndRender(data, tileEntity.waterTank.getFluid().getFluid(), tileEntity.worldObj);
				
				GL11.glColor4f(1F, 1F, 1F, Math.min(1, ((float)tileEntity.waterTank.getFluidAmount() / (float)tileEntity.getMaxWater())+0.3F));
				displayList[getStages(data.height)-1].render();
					
				MekanismRenderer.glowOff();
				
				pop();
			}
		}*/
	}
	
	private void pop()
	{
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	private void push()
	{
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private DisplayInteger[] getListAndRender(SalinationRenderData data, Fluid fluid, World world)
	{
		if(cachedCenterFluids.containsKey(data) && cachedCenterFluids.get(data).containsKey(fluid))
		{
			return cachedCenterFluids.get(data).get(fluid);
		}
		
		Model3D toReturn = new Model3D();
		toReturn.baseBlock = Block.waterStill;
		toReturn.setTexture(fluid.getIcon());
		
		final int stages = getStages(data.height);
		DisplayInteger[] displays = new DisplayInteger[stages];
		
		if(cachedCenterFluids.containsKey(data))
		{
			cachedCenterFluids.get(data).put(fluid, displays);
		}
		else {
			HashMap<Fluid, DisplayInteger[]> map = new HashMap<Fluid, DisplayInteger[]>();
			map.put(fluid, displays);
			cachedCenterFluids.put(data, map);
		}
		
		MekanismRenderer.colorFluid(fluid);
		
		for(int i = 0; i < stages; i++)
		{
			displays[i] = DisplayInteger.createAndStart();
			
			if(fluid.getIcon() != null)
			{
				/*toReturn.minX = 0 + .01;
				toReturn.minY = 0 + .01;
				toReturn.minZ = 0 + .01;
				
				toReturn.maxX = data.length - .01;
				toReturn.maxY = ((float)i/(float)stages)*(data.height-2) - .01;
				toReturn.maxZ = data.width - .01;
				
				MekanismRenderer.renderObject(toReturn);*/
			}
			
			GL11.glEndList();
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		return displays;
	}
	
	private int getStages(int height)
	{
		return (height-2)*(TankUpdateProtocol.FLUID_PER_TANK/10);
	}
	
	private double getX(int x)
	{
		return x - TileEntityRenderer.staticPlayerX;
	}
	
	private double getY(int y)
	{
		return y - TileEntityRenderer.staticPlayerY;
	}
	
	private double getZ(int z)
	{
		return z - TileEntityRenderer.staticPlayerZ;
	}
	
	public static class SalinationRenderData
	{
		public int height;
		
		@Override
		public int hashCode() 
		{
			int code = 1;
			code = 31 * code + height;
			return code;
		}
		
		@Override
		public boolean equals(Object data)
		{
			return data instanceof SalinationRenderData && ((SalinationRenderData)data).height == height;
		}
	}
}
