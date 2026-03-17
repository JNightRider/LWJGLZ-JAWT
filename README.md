# LWJGLZ - JAWT

This library allows for using OpenGL and Vulkan LWJGL 3 surfaces in AWT (and by extension, Swing) frames. Works with macOS, Windows, and Linux.

## What does it get me?

Support for OpenGL:
- creating OpenGL v2+ core/compatibility contexts (including debug/forward compatible)
- OpenGL ES contexts
- floating-point and sRGB pixel formats
- multisampled framebuffers (also with different number of color samples - Nvidia only)
- v-sync/swap control
- context flush control
- robust buffer access (with application/share-group isolation)
- sync'ing buffer swaps over multiple windows and cards - Nvidia only
