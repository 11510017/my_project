# CS304 Project: Progress Report
main-project-repository-pomodorotimemanagementlyd created by GitHub Classroom

## List the 3 features that you have chosen

1. Load files
2. Use camera to take photos
3. Save files
   
Explain the reason for choosing this feature? 
  
  1. Importance of the feature:

    These three features are fundamental for our app. The user should choose an image to modify. This image is either taken from the camera or selected from gallery on the phone.
    
  2. Dependencies between features:

    The three features are highly dependent. After the user enter into the app, he/she will see a grid view full of image folders on the phone, which requires preloading image files in a data list. Then if the user take a photo and confirm, the photo will be saved on the phone.
    
  3. The ease of constructing test cases:

    At the beginning, the code structure is very unfriendly for constructing tests, then we did some refactoring, which made it a little easier for us to test loading and saving files. However, we got stuck at camera test, at last we found that espresso might help us test it. 


    
   
## Test scenarios for 3 features
    
  1. Load files:

    In normal cases, there are some images on the phone, and the loading will be successful. When the permission is not guaranteed or there is no images on the phone, the test may fail.

  2. Take photos:
  
    When permission is given, the camera should be able to open, otherwise the app will crash.

  3. Save files:
    
    When permission is given, the path is valid, and the photo is taken, then saving should be successful, otherwise it will fail.
   

## Schedule for the following weeks

|Week|Features to implement|
|----|----|
|week 10|Startup image; Picture cropping|
|week 11|Style transfer|
|week 12|Randomly choosing art style; Multiple art styles mixing|
|week 13|Sharing to other platform|
|week 14|Switching theme, such as daytime and night|
|week 15|Multiple languages|
