![logo](resources/thumbnail.png) 
# Thousand Picture Comapre
### Version tag: 0.3.3f
## Instruction of usage: 
1. To start application you either need to use **Main class** or if you use .jar file, you should use attached script **run.bat**.
2. You should see the app right now. In the upper part of a window you should see logo, name and button with text: *settings*, lower you should see a path picker *(label: "Path:", static text field and button with text: "open")*, then lower you should see three indicators with labels *(total, processed and duplicates)* and on the right you should see empty space *(which is output log)*, then at the bottom there are three buttons *(reset, load files & compare and move files)*.
3. Now when you have full knowledge about the main panel, we can move forward. **Preparing stage**. Firstly, you should move to a settings panel, to do so click **settings button**.
4. **Setting up destination directory**, in simple words *where duplicates will be moved*. In the settings panel you should see the same kind of component like in the main panel (labeled "path"), click it and pick your desired destination directory.
5. Now when you've picked your destination, it's time to move on and get back to the main panel, to do so click **back button** at the bottom of the window.
6. The Next stage stands for **picking directory with pictures to compare**. To do so, click the button with label **open** and pick desired directory.
7. Hurray, you actually prepared the app to compare. Now it's **showtime**. To start the process of processing click button labeled **Load files & compare** and wait until a process is done.
8. When finally, after some time, the application actually mapped all the pictures you are ready to move on. There are two possible outcomes of mapping either app found duplicate, in that case you can click the move files button, or app didn't find any duplicates then you are happy that you don't have redundant pictures.
9. If you click **Move files** button, then the application will move all the redundant files into specified in the settings directory (if you haven't specified it, then the app will move them to place where .jar file stands).
## Notes:
- Project build system: IntelliJ IDEA
- Jdk version: 19
- Versions with d letter suffix are un-tested

Author: [Github](https://github.com/maksik997) 
