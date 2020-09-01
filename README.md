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

# Main problems solved
- It was necessary to store locations while the app was on background. For fulfilling this purpose, a receiver was added to “AndroidManifest.xml”.
- It was necessary a Google Maps API Key to show the location on screen. It can be obtained by following the steps on: https://developers.google.com/maps/documentation/android-sdk/start#get-key
