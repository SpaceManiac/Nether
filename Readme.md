Nether 2.0
----------
Nether 2.0 is a simplistic plugin for improved Nether-portal functionality. It is an update to the original Nether
plugin which provided easy access to the Nether prior to Mojang's release of official SMP Nether functionality. It uses
no permissions, adds no commands, but allows easy customization of the properties of Nether portals.

Nether features three modes:
* Classic Mode acts like Nether 1.2 and waits for a player to move into a portal, immediately whisking them to an
  existing or new portal in the opposite world. The standard allow-nether must be off for Classic Mode, and the plugin
  will create its own Nether world. Under Classic Mode, it is possible to change the default 1:8 distance ratio.
* Agent Mode keeps the normal delay when portalling, plugging itself into the official portal travel agent API to
  change the way portals are searched for and constructed to act like Nether 1.2. The standard allow-nether must be on
  for Agent Mode.
* Adjust Mode uses the official Mojang implementation of portalling, but allows for adjustment of various properties
  of the portal system. The standard allow-nether must be on for Adjust Mode.

Various aspects of portalling are configurable:
* The radius around the destination coordinates to search for a pre-existing portal.
* The radius around the destination coordinates within which a new portal may be constructed (does not apply to Classic
  Mode).
* Whether new portals may be created, or only existing portals may be used.

Nether also features optional respawn control, ensuring players who die in the Nether are safely transported to the
spawn of the main world.

MIT License
-----------
Copyright (C) 2011 by Tad Hardesty

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.