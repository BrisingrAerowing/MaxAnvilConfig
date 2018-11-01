package maxanvilcfg;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MaxAnvilLevelConfig.MODID, name = MaxAnvilLevelConfig.NAME, version = MaxAnvilLevelConfig.VERSION)
public class MaxAnvilLevelConfig {

	public static final String MODID = "maxanvilcfg";
	public static final String NAME = "Max Anvil Level Config";
	public static final String VERSION = "0.1";

	public static int maximumAnvilLevel = 40;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		maximumAnvilLevel = config.getInt("maximumAnvilLevel", "general", 40, 1, Integer.MAX_VALUE, "The maximum XP level the anvil can handle.");

		if (config.hasChanged()) {
			config.save();
		}
	}

}
