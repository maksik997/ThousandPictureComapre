![logo](resources/thumbnail.png) 
# Thousand Picture Compare`
### Version tag: 0.4.2d

## Description: 
**Warning!
This version of this app contains some bugs.**
However, if you want to use it, you should know that your data is safe.
If you don't trust comparer, you could always move your images instead of removing them (Gallery feature).

If you want to find or remove redundant images, and you want to do it somehow automatic, then this app is for you.
App features with: 

- Comparer which will help you find those images that are duplicates of existing ones,
- Gallery which will help you in a kind of comfortable way to manage your images.

Both of them are easy to use, and they're relatively fast. 

**Comparer** features with comfortable view of all loaded images and duplicates that were found.
Additionally, you can see how many of both were found.
There is an option to use recursive search for images,
which could be handy if you use many directories where you store your images.

**Gallery** features with a list of your loaded images, with some options: Distinct, Unify, Open. Distinct will let you find any duplicates in selection and will ask if you want to remove them or move. Unify will let you standardise your image names in format: `tp_img_[n]_[timestamp].[extension]`. And last but not the least Open button, which let you open your images in your system image viewer.  

This is my first bigger project, and I'm thrilled how it looks.
It's a buggy from time to time,
but I hope that with some more time I will be able to remove all bugs and create a great user experience.

And for those that read the source code: Yes, there are a lot of todo comments there :P

## Instruction of usage: 
1. Run the app.
2. You can choose either to open a Comparer or Gallery or Settings.

### Comparer
1. You can pick directory where you want to search for images,
2. Then you're able to click **Load & compare** button, which will lock most Comparer functions for time it's working.
3. After waiting for some time, you will see that **Total** and **Duplicates** values will change, and you will be able to see that **Loaded originals** and **Duplicates found** changed (Duplicates only if any duplicate was found).
4. Now you can perform **Move** action which will move those duplicates to the directory you specified.
5. Then, if you want to use this Comparer instance, again you have to click **Reset** button.

### Gallery 
You will see a table and five buttons.
To add images, you should click **Add** button, and pick there any file you want to add
(the app will check if this is a valid picture, where valid means that image is supported by Picture Comparer).
To remove image, you should click **Remove** button, which will remove all the selected images in table.
To remove duplicates, you should click **Distinct** button, which require at least two pictures selected.
Next there is **Unify** button which will rename all of your loaded images with specified format:
`tp_img_[n]_[timestamp].[extension]`.
And lastly, there is **Open** button, which will open your image in your system image viewer.

### Settings
Here you can change your destination for your Comparer output (for both Comparer and Gallery),
and you can pick if you want to recursively search for images.
Remember after any change if you want to save your settings you should click **Save settings** button.

## Running app:
1. Click .exe file.
2. End...

## Notes:
- Project build system: IntelliJ IDEA
- Jdk version: 22
- Versions with d letter suffix are un-tested

Author: [GitHub](https://github.com/maksik997)
