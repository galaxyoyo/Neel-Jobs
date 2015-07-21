package galaxyoyo.minecraft.bukkit.jobs.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;

public class Zone
{
	private final Object[][][][] materials;
	
	public Zone(int x, int y, int z)
	{
		materials = new Object[Math.abs(x)][Math.abs(z)][Math.abs(y)][3];
	}
	
	public Zone(NBTTagCompound nbt)
	{
		int xSize = nbt.c().size();
		int zSize = 0;
		int ySize = 0;
		for (String str : nbt.c())
		{
			zSize = nbt.getCompound(str).c().size();
			break;
		}
		for (String str : nbt.c())
		{
			for (String str2 : nbt.getCompound(str).c())
			{
				ySize = nbt.getCompound(str2).c().size();
				break;
			}
		}
		materials = new Object[xSize][zSize][ySize][3];
		
		for (String xStr : nbt.c())
		{
			int x = Integer.parseInt(xStr);
			for (String zStr : nbt.getCompound(xStr).c())
			{
				int z = Integer.parseInt(zStr);
				for (String yStr : nbt.getCompound(xStr).getCompound(zStr).c())
				{
					int y = Integer.parseInt(yStr);
					NBTTagCompound tag = nbt.getCompound(xStr).getCompound(zStr).getCompound(yStr);
					String matName = tag.getString("material");
					byte damageName = tag.getByte("damage");
					NBTTagCompound tagTag = tag.getCompound("tag");
					materials[x][z][y][0] = Material.getMaterial(matName);
					materials[x][z][y][1] = Byte.valueOf(damageName);
					materials[x][z][y][2] = tagTag;
				}
			}
		}
	}
	
	public Material getMaterial(int x, int y, int z)
	{
		if (materials[x][z][y][0] == null)
		{
			materials[x][z][y][0] = Material.AIR;
			materials[x][z][y][1] = Byte.valueOf((byte) 0);
			materials[x][z][y][2] = new NBTTagCompound();
		}
		
		return (Material) materials[x][z][y][0];
	}
	
	public byte getDamage(int x, int y, int z)
	{
		if (materials[x][z][y][0] == null)
		{
			materials[x][z][y][0] = Material.AIR;
			materials[x][z][y][1] = Byte.valueOf((byte) 0);
			materials[x][z][y][2] = new NBTTagCompound();
		}
		
		return ((Byte) materials[x][z][y][1]).byteValue();
	}
	
	public NBTTagCompound getNBT(int x, int y, int z)
	{
		if (materials[x][z][y][0] == null)
		{
			materials[x][z][y][0] = Material.AIR;
			materials[x][z][y][1] = Byte.valueOf((byte) 0);
			materials[x][z][y][2] = new NBTTagCompound();
		}
		
		return (NBTTagCompound) materials[x][z][y][2];
	}
	
	public void setMaterial(int x, int y, int z, Material material)
	{
		materials[x][z][y][0] = material;
	}
	
	public void setData(int x, int y, int z, byte data)
	{
		materials[x][z][y][1] = data;
	}
	
	public void setMaterialAndData(int x, int y, int z, Material material, byte data)
	{
		materials[x][z][y][0] = material;
		materials[x][z][y][1] = data;
	}
	
	public void setNBTTag(int x, int y, int z, NBTTagCompound tag)
	{
		materials[x][z][y][2] = tag;
	}
	
	public List<ItemStack> getStacked()
	{
		Map<ItemStack, Byte> map = new HashMap<ItemStack, Byte>();
		for (int x = 0; x < materials.length; ++x)
		{
			for (int z = 0; z < materials[x].length; ++z)
			{
				for (int y = 0; y < materials[x][z].length; ++y)
				{
					if (materials[x][z][y][0] == null)
					{
						materials[x][z][y][0] = Material.AIR;
						materials[x][z][y][1] = Byte.valueOf((byte) 0);
						materials[x][z][y][2] = new NBTTagCompound();
					}
					ItemStack stack = new ItemStack((Material) materials[x][z][y][0], 1, (byte) materials[x][z][y][1]);
					if (!map.containsKey(stack))
						map.put(stack, Byte.valueOf((byte) 0));
					map.put(stack, Byte.valueOf((byte) (map.get(stack) + 1)));
				}
			}
		}
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (Entry<ItemStack, Byte> entry : map.entrySet())
		{
			ItemStack from = entry.getKey();
			ItemStack stack = new ItemStack(from);
			stack.setDurability(entry.getValue().byteValue());
			list.add(stack);
		}
		
		return list;
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public boolean place(World world, CraftPlayer player, boolean onlyShow)
	{
	//	BlockFace face = getFace(player.getLocation().getYaw());
		CraftBlock startBlock = (CraftBlock) player.getTargetBlock((Set<Material>) null, 5);
		if (startBlock == null || startBlock.getType() == Material.AIR)
			return false;
		Location origin = startBlock.getLocation().add(0.0D, 1.0D, 0.0D);
		
		for (int zoneX = 0; zoneX < getXLength(); ++zoneX)
		{
			for (int zoneZ = 0; zoneZ < getZLength(); ++zoneZ)
			{
				for (int zoneY = 0; zoneY < getYLength(); ++zoneY)
				{
					int x = origin.getBlockX() + zoneX;
					int y = origin.getBlockY() + zoneY;
					int z = origin.getBlockZ() + zoneZ;
					CraftBlock block = (CraftBlock) world.getBlockAt(x, y, z);
					if (block.getType() != Material.AIR)
						return false;
				}
			}
		}
		
		for (int zoneX = 0; zoneX < getXLength(); ++zoneX)
		{
			for (int zoneZ = 0; zoneZ < getZLength(); ++zoneZ)
			{
				for (int zoneY = 0; zoneY < getYLength(); ++zoneY)
				{
					int x = origin.getBlockX() + zoneX;
					int y = origin.getBlockY() + zoneY;
					int z = origin.getBlockZ() + zoneZ;
					CraftBlock block = (CraftBlock) world.getBlockAt(x, y, z);
					if (block.getType() == Material.AIR)
						continue;
					
					if (!onlyShow)
					{
						block.setType(getMaterial(x, y, z));
						block.setData(getDamage(x, y, z));
						CraftBlockState state = (CraftBlockState) block.getState();
						if (state.getTileEntity() != null)
							state.getTileEntity().a(getNBT(x, y, z));
					}
					else
					{
						PacketPlayOutBlockChange pkt = new PacketPlayOutBlockChange(player.getHandle().getWorld(),
								new BlockPosition(x, y, z));
						pkt.block = Block.getByCombinedId(block.getTypeId() << 4 | block.getData());
						player.getHandle().playerConnection.networkManager.a(pkt, null);
					}
				}
			}
		}
		
		return true;
	}
	
	public void delete(World world, Location origin)
	{
		for (int zoneX = 0; zoneX < getXLength(); ++zoneX)
		{
			for (int zoneZ = 0; zoneZ < getZLength(); ++zoneZ)
			{
				for (int zoneY = 0; zoneY < getYLength(); ++zoneY)
				{
					int x = origin.getBlockX() + zoneX;
					int y = origin.getBlockY() + zoneY;
					int z = origin.getBlockZ() + zoneZ;
					CraftBlock block = (CraftBlock) world.getBlockAt(x, y, z);
					block.setType(Material.AIR);
				}
			}
		}
	}
	
	public int getXLength()
	{
		return materials.length;
	}
	
	public int getZLength()
	{
		return materials.length > 0 ? materials[0].length : 0;
	}
	
	public int getYLength()
	{
		return materials.length > 0 && materials[0].length > 0 ? materials[0][0].length : 0;
	}
	
	public NBTTagCompound getTag(NBTTagCompound nbt)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		for (int x = 0; x < getXLength(); ++x)
		{
			NBTTagCompound xTag = new NBTTagCompound();
			for (int z = 0; z < getZLength(); ++z)
			{
				NBTTagCompound zTag = new NBTTagCompound();
				for (int y = 0; y < getYLength(); ++y)
				{
					NBTTagCompound yTag = new NBTTagCompound();
					Material mat = (Material) materials[x][z][y][0];
					byte damage = ((Byte) materials[x][z][y][1]).byteValue();
					NBTTagCompound blockTag = (NBTTagCompound) materials[x][z][y][2];
					yTag.setString("material", mat.name());
					yTag.setByte("damage", damage);
					yTag.set("tag", blockTag);
					zTag.set(String.valueOf(y), yTag);
				}
				xTag.set(String.valueOf(z), zTag);
			}
			tag.set(String.valueOf(x), xTag);
		}
		
		nbt.set("zone", tag);
		return nbt;
	}
	
	public static BlockFace getFace(float yaw)
	{
		int rotation = ((MathHelper.floor((double) (yaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		switch (rotation)
		{
			case 0:
				return BlockFace.NORTH;
			case 1:
				return BlockFace.EAST;
			case 2:
				return BlockFace.SOUTH;
			case 3:
				return BlockFace.WEST;
		}
		
		return null;
	}
}
