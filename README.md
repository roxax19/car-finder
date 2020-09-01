# Car finder
App that allows you to know where your car is parked.

It uses MySQL, Bluetooth and Location services.

# Goals
The main goal is to locate our vehicle using a strategy that involves low battery impact. The strategy takes advantage of the hands-free phone car function and avoid the constant usage of the location services.

# Main idea
Check the location services when a Bluetooth device is disconnected form the vehicle hands-free function.

# Functionalities
- Database to store locations.

- Map to display the location.

# Design
The design of the app matches those functionalities.

The app consists on two Activities: MainActivity, where we can find a list with the stored locations, and MapsActivity, where the selected location is shown to the user.

![Alt text](screenshots/screenshot1.jpg?raw=true "Screenshot 1")
![Alt text](screenshots/screenshot2.jpg?raw=true "Screenshot 2")

# Main problems solved
- It was necessary to store locations while the app was on background. For fulfilling this purpose, a receiver was added to “AndroidManifest.xml”.
- A Google Maps API Key was needed to show the location on screen. It was obtained by following the steps on: https://developers.google.com/maps/documentation/android-sdk/start#get-key
