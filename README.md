# LWJGLZ - JAWT

> [!CAUTION]
> This project is in alpha phase, so many things may not work or may fail (some are not yet implemented), so use with caution.

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
| Linux            | In progress | S      | n      |
| Windows          | In progress | S      | n      |
| MaxOs            | -           | n      | n      |

