package com.lifesaver;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Life Saver",
	description = "Forces a notification when at or below specified hitpoints",
	tags = {"health", "hitpoints", "notifications"}
)
public class LifeSaverPlugin extends Plugin
{
	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private LifeSaverConfig config;

	private boolean notifyHitpoints = true;

	@Provides
	LifeSaverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LifeSaverConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();

		if (client.getGameState() != GameState.LOGGED_IN
			|| local == null)
		{
			return;
		}

		if (checkLowHitpoints())
		{
			notifier.notify("You have low hitpoints!");
		}

	}

	private boolean checkLowHitpoints()
	{
		if (config.getHitpointsThreshold() == 0)
		{
			return false;
		}
		if (client.getRealSkillLevel(Skill.HITPOINTS) > config.getHitpointsThreshold())
		{
			if (client.getBoostedSkillLevel(Skill.HITPOINTS) + client.getVar(Varbits.NMZ_ABSORPTION) <= config.getHitpointsThreshold())
			{
				if (!notifyHitpoints)
				{
					notifyHitpoints = true;
					return true;
				}
			}
			else
			{
				notifyHitpoints = false;
			}
		}

		return false;
	}
}
