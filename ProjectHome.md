![http://www.crypticrage.com/boom/boom_logo.png](http://www.crypticrage.com/boom/boom_logo.png)

Boom is an mobile arcade style war game that borrows shamelessly from Missile Command. The object is to defend your base (located in the bottom-center of the screen) from a slew of incoming enemy missiles. Most enemy missiles are fired in random directions and may not hit the player’s base. Others are equipped with precision targeting systems combined with greater payloads to ensure a direct hit and the destruction of the base.

Please check out our [README](http://www.crypticrage.com/boom/boom_readme.pdf) in the downloads section for more information on the game. This file was included in our submission to the Android Developer Challenge.

![http://www.crypticrage.com/boom/boom_screen.png](http://www.crypticrage.com/boom/boom_screen.png)

## [Click here for a demo video.](http://www.youtube.com/watch?v=r8vurpGwkzU) ##

## Instructions ##
The demo is very simple to play. After launching the game a splash screen will appear for five seconds. Afterwards, enemy missiles will begin to descend on your base. Click anywhere on the screen to fire the currently selected projectile. You can select a different projectile by clicking on its icon in the bottom left corner of the screen.

### Friendly Projectiles ###
The player may currently use two types of projectiles to destroy enemy missiles, a red Interceptor Missile and a Smart Shell. All projectiles launched originate from the player’s base, and will travel in a straight line to the target point touched on the screen. Note that both types of missiles may collide with incoming enemy missiles along the way, causing them to explode prematurely.

Interceptor Missiles will travel from the player’s base to the point clicked on the screen, at which point they will explode. Smart Shells are similar to the Interceptor except they travel faster, have a smaller explosion radius, and are capable of destroying both enemy missile types (See the Enemy Missile section for more details). Launching a Smart Shell is slightly different than an Interceptor: instead of a single click, it takes two clicks. The first click causes the camera to zoom in on the point selected, and the second click launches the Smart Shell. This allows the Smart Shell to be precisely aimed, a necessity given its smaller explosion radius.

### Enemy Missiles ###
Boom currently has two types of enemy missiles, the Red Destroyer and the Gold Seeker. Both missiles have random launch trajectories, allowing them to originate from anywhere at the top of the screen.

The Red Destroyers are launched at varying velocities and have random targets on the ground, meaning that not every Destroyer will target the player’s base. This allows the player to pick and choose their targets based on the threat they pose to their base, giving them the ability to destroy non-threat missiles for extra points. Destroyer missiles deal up to 10% of the base’s maximum hit points, depending on the missile’s proximity to the base at the time of the explosion. Destroyer missiles may be destroyed with any type of ammunition available to the player.

The Gold Seekers are launched at a higher velocity and always target the player’s base. These missiles deal enough damage to instantly destroy the player’s base, making them a top priority. Unlike the Destroyer, however, the Seeker may only be destroyed by the player’s Smart Shells. Seekers initially have a small chance of launching, which gradually increases as the game goes on.

### Scoring ###
A player’s score is determined by the number of enemy projectiles destroyed and the accuracy of the detonations which destroy them. For example, a direct hit on a Red Destroyer results in the maximum amount of points plus a direct hit bonus. An enemy missile which is destroyed by an explosion gives a score which is based on the explosion’s radius at the time it destroys the enemy missile. Enemy projectiles destroyed with the Smart Shell have a double score multiplier, meaning they give double the points.

### Requirements ###
Android SDK version m5 (tested with m5-rc15)

Android Emulator (screen must be set to **HVGA 480x320 Landscape**)