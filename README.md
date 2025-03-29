uCombat is a comprehensive combat management plugin for Minecraft that enhances the gameplay experience by implementing a combat tagging system. This system is designed to ensure that players cannot logout during combat to avoid defeat, thereby promoting fair play and reducing frustration for players who are engaged in PvP (player versus player) interactions. Here’s a deep dive into its functionalities:
Features:

**    Combat Tagging:** Players who are involved in combat are tagged, indicating their current combat status. When tagged, a player will receive a notification with the time left until their tag expires, helping them to manage their engagements strategically.
**
    Punishing Logout:** If a player attempts to log out while tagged, they will be forcibly killed, ensuring that they do not escape combat by exiting the game.

**    Death and Respawn Handling:** If a tagged player dies, their combat tag is removed upon respawn, along with the combat tag of their last attacker, providing a clean slate for both players moving forward.

**    Customizable Combat Duration:** Server administrators can configure the duration for which players remain tagged after a combat encounter, allowing flexibility to suit various gameplay styles.
**
    Admin Commands:** Administrators have access to commands for reloading the plugin’s configuration without needing to restart the server.

**Commands:**

**    /ucombat reload**
        Description: Reloads the plugin configuration.
        Permission: ucombat.admin.
        Usage: This command can be executed by players with the appropriate permission to update the plugin's settings on-the-fly. After reloading, players will receive confirmation through an action bar notification.

**Permissions:**

**    ucombat.admin:**
        Grants permission to use administrative commands, specifically the /ucombat reload command. Players without this permission will receive an error message when attempting to execute the command.

**Configuration:**
**
    combat.tag-duration: **
        A configurable setting (measured in seconds) that determines how long a player remains tagged after engaging in combat. By default, this is set to 15 seconds, but server owners can adjust this in the plugin's configuration file.

**    combat.punish-logout:**
        A boolean setting that, when true, forces tagged players to be killed if they log out. This can be turned off based on server rules and preferences.
