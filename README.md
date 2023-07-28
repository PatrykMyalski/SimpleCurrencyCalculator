# SimpleCurrencyCalculator

This is the simplest currency converter calculator you can imagine. You just choose three currencies from up to 177 options, including the most popular cryptocurrencies. All of that is provided by [currency-api](https://currencyapi.com/).

## Technologies

- Android
- Kotlin
- Jetpack Compose
- Javascript (Firebase functions)
- Firebase Firestore
- Firebase Functions 
- OkHttp
- Gson

## About

It is a slightly improved version of the currency calculator I had on my old Xiaomi phone. I created it because I really liked the format of this old app.

## Features 

- Possibility of calculating two currencies on every input change
- Search field with all those 177 currencies; to make things better, there is an available search bar
- Currencies data is updated every 24 hours from [currency-api](https://currencyapi.com/) thanks to firebase functions
- All that data is saved on Firebase Firestore, and the user receives it on launch
- State of inputs is saved on the user's device using [ROOM](https://developer.android.com/training/data-storage/room)
- Nice and minimalistic look in both light mode and dark mode

## Screenshots

<img src="screenshots/calculator_light.png" alt="Image 1" width="200" height="400"><img src="screenshots/currency_light.png" alt="Image 3" width="200" height="400">

<img src="screenshots/calculator_dark.png" alt="Image 2" width="200" height="400"><img src="screenshots/currency_dark.png" alt="Image 4" width="200" height="400">

