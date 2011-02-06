Factions - Guilding and user-controlled antigrief plugin for Minecraft
====================
This plugin will allow the players on the server to create factions/guilds. The factions can claim territory that will be protected from non-members. Factions can forge alliances and declare themselves enemies with others. Land may be taken from other factions through war.

With this plugin...
 - the players will effectively take care of anti-griefing themselves.
 - there will be politics and intrigues on your server.
 - the players can enjoy guilding and team spirit.

Usage
---------
The chat console command is:
/f

This command has subcommands like:
/f create My faction name
/f invite my friends name
/f claim
/f map
... etc

Read the full guide here:
http://mcteam.org/factions

You may also read the documentation ingame as the plugin ships with an ingame help manual. Read the help pages like this:
/f help 1
/f help 2
/f help 1

Note that you may optionally skip the slash and just write
f

Installing
---------
1. Download the latest release: https://github.com/oloflarsson/Factions/raw/master/releases/latest.zip
1. Put the included gson.jar here /your_server_folder/gson.jar
1. Put the included Factions.jar here /your_server_folder/plugins/Factions.jar

Compiling
---------
If you feel like compiling the plugin on your own
*You will need the Bukkit library to compile (Bukkit.jar)*
You can download it here: http://bamboo.lukegb.com/browse/BUKKIT-BUKKITMAIN/

*You will also need Google GSON in your build path.*
You can download it here: http://code.google.com/p/google-gson/
*OR* you can use the gson.jar in the folder /packaging
However you won't get javadocs and source that way.

You will need to include the MANIFEST.MF and plugin.yml in your jar. All bukkit plugins need a plugin.yml and the MANIFEST.MF is needed for the plugin to find the gson.jar (wich should be in the same folder as the main server jar when running the server).



Towny is a plugin for Bukkit (originally from but discontinued from hMod) that allows players to create towns and nations, along with simple alliance controls. The world is broken up into a grid using 16x16 grid cells as default. Each town is given a certain number of these cells and gains more as the town holds more players. These cells can be controlled by a town's mayor to remove mobs, animals, set build/destroy permissions, and define PvP zones. The mayor can also section off some of these cells for sale among its residents. Once a town joins a nation, it's residents can then start building in it's allied towns, but is susceptible to it's enemies destroying it when war time is announced.