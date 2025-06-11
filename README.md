Overview
bVeinMiner is a lightweight, performant, and highly configurable plugin that brings the popular vein mining mechanic to your server. It allows players to excavate entire ore veins instantly while giving administrators complete control over how the feature works to maintain server balance. It's designed to be simple for players and powerful for administrators.

Features
Instant Vein Mining: Break one ore, and the entire connected vein of the same type breaks with it!
Player-Controlled Toggles: Players can enable or disable vein mining and auto-pickup at any time to suit their playstyle.
Fully Configurable Ores: You can define exactly which blocks are considered "ores" and can be vein-mined directly in the config.yml.
Full Enchantment Support: Correctly calculates drops from enchantments like Fortune and Silk Touch.
Toggleable Auto-Pickup: A permission-based mode to send mined items directly to the player's inventory, preventing ground clutter. If the inventory is full, excess items are dropped safely.
Dynamic Vein Size Limits: Control the maximum vein size with permissions. Set a default limit and override it for different ranks using nodes like bveinminer.max.<number>.
Tool Safety: Vein mining automatically stops if the player's tool breaks and requires the correct tool type to initiate.
Admin-Friendly: A simple /bvm reload command allows you to update all configuration and message files without a server restart.
Commands
Player Commands
Command	Description	Permission
/veinminer	Toggles the main vein miner feature on or off.	bveinminer.use
`/veinminer <on\	off>`	Explicitly sets the vein miner feature on or off.
/veinminer status	Checks if your vein miner is currently on or off.	bveinminer.use
/veinminer autopickup	Toggles the auto-pickup feature on or off.	bveinminer.autopickup

Export to Sheets
Admin Commands
Command	Description	Permission
/bvm reload	Reloads the config.yml and messages.yml files.	bveinminer.reload

Export to Sheets
Permissions
Permission Node	Description	Default
bveinminer.use	Grants access to the basic /veinminer commands.	op
bveinminer.autopickup	Allows the player to use the /veinminer autopickup command.	op
bveinminer.max.<number>	Sets a specific vein size limit. Ex: bveinminer.max.100.	op
bveinminer.max.unlimited	Allows a player to mine veins of any size.	op
bveinminer.admin	Parent permission for all admin commands.	op
bveinminer.reload	Grants access to the /bvm reload command.	op

Export to Sheets
Configuration
The plugin is highly configurable through two simple files in your plugins/bVeinMiner/ folder.

config.yml

YAML

# Enable debug logging in the console
debug: false

vein-miner:
  # Default maximum number of blocks to be mined in one go.
  # This can be overridden by the 'bveinminer.max.<number>' permission.
  default-max-blocks: 64

  # List of materials that are considered ores for vein mining.
  ores:
    - "COAL_ORE"
    - "DEEPSLATE_COAL_ORE"
    - "COPPER_ORE"
    # ... and so on
messages.yml
This file allows you to customize every message sent by the plugin, including the command prefix, status updates, and error messages. Both legacy (&c) and hex (&#RRGGBB) color codes are supported.
