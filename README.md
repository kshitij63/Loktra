# Loktra

WORKING:

1.) App Contains MainActivity which contains a map fragment.
2.) On clicking the button(click to start service), Location service runs which tracks user's fine location using GPS.
3.) Every 30 seconds the location i.e latitude and longitude is saved in local sqlite db.
4.) On clicking Stop service, polyline is drawn on map denoting the path travelled by the user.Shift duration is also displayed.

STACK USED:

1.) Room and Livedata for local storage of coordinate and tiemStamp.
2.) Used Google Maps API and Google Location API.
3.) Application is written in Kotlin.
