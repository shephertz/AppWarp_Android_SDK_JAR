AppWarp_Android_SDK_JAR
=======================

AppWarp client SDK JAR files for Android.

Sample
========
The sample game included provides a great way to getting started with building your own games using AppWarp. The Tic Tac Toe sample illustrates how
to create rooms/join rooms and how the game play progresses inside the room.
After setting up the project in Eclipse IDE, you will first need to update the Api and Secret Key pair in Constants.java with the one you received
after signing up. Now you can install and run the app on your Android emulator or device.
This sample also illustrates how the AppWarp and App42 cloud APIs work in conjunction. This app uses the User Management module of App42 cloud API to 
handle user sign-up and login.
Once the first user is signed in, it can create a room with any desired name and wait for other users to join. Now you can login from another emulator/device
and select join random room option. This will loop through all the available rooms and join one in which there is a single user. Now the game can begin.