# LWJGLZ - JAWT

This library allows for using OpenGL and Vulkan LWJGL 3 surfaces in AWT (and by extension, Swing) frames. Works with macOS, Windows, and Linux.

## What does it get me?

Support for OpenGL:
- creating OpenGL v2+ core/compatibility contexts (including debug/forward compatible)
- OpenGL ES contexts
- floating-point and sRGB pixel formats
- v-sync/swap control
- context flush control
- robust buffer access (with application/share-group isolation)
- sync'ing buffer swaps over multiple windows and cards - Nvidia only

## Supported Platforms

LWJGLZ-JAWT has been ported to many different platforms.

So far, they have been implemented for the following platforms and renderers

| Operating System | Status      | OpenGL | Vulkan |
|------------------|-------------|--------|--------|
| Linux            | Supported   | Y      | n      |
| Windows          | Supported   | Y      | n      |
| macOS            | -           | n      | n      |
