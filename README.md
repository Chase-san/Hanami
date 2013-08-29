# Hanami

Java Image Viewer

Very similar to IrfanView, but capable of running on any system with a JRE.

### Capabilities
* Can open PNG, GIF, BMP, and JPG files.
* Plays animated GIF.
* Fullscreen Mode.
* Left and Right to navigate through directory.
* UI doesn't lockup while loading images.
* Can load images from unicode paths on windows using Custom Launcher.

### Key Bindings
* **Home** Moves to the first image in the directory.
* **End** Moves to the last image in the directory.
* **Left**, **Right**, **Up**, **Down** Scrolls image. **Left** and **Right** will move to next/previous file if it cannot scroll left or right. 
* **Page Up**, **Backspace**, **Left** Moves to previous image, loops at start of directory.
* **Page Down**, **Space**, **Right** Moves to next image, loops at end of directory.
* **Enter** Enter fullscreen mode.
* **Esc** Exits full screen mode, exits program in windowed mode.

### Notes
There are many more options available that can only be configured by editing the cfg file that is produced. These are planned to be added to the options dialog in the near future.

### Plans
* More file types
* Animated PNG
* Copy and Paste support
* Basic Editing (Cropping, Resizing, Rotation, Flipping)
* Directory Sorting Options
* Key Binding Editing
* Status Bar, Image Information
* Basic Plugins (for supporting extra file types)
* More Options