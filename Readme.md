# Weather Forecast App

This repository is a weather application to display the current weather at the user’s location and a
5-day forecast using the  [Open Weather API](https://openweathermap.org/api)

Application Functions:
--------------

- Show the forecast based on the user’s current location.
- The application connect to the following [Open Weather APIs ](https://openweathermap.org/api) to
  collect the weather information.
    - [Current weather data](https://openweathermap.org/current)
    - [5 day weather forecast](https://openweathermap.org/forecast5)
- The app change the background image depending on the type of weather (Cloudy, Sunny and Rainy)

Tech Stack Used:
--------------

- Language - [Kotlin](https://developer.android.com/kotlin)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
- [Dependency injection with Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- Networking - [Retrofit](https://square.github.io/retrofit/) with [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

Screenshots
------------
![Login page](Screenshots/aloginpage.png "Login page" )   ![Home page](Screenshots/homepage.png "Home page" )   ![Edit Journal](Screenshots/editjournal.png "Edit page" )   ![Login page](Screenshots/ajournal.png "a journal" )

# Sample Apk

![Weather Forecast Apk](sampleAPK/app-debug.apk "Weather Forecast Apk")

License
--------

      Quick Journal App
      Copyright (c) 2021 Awesome Jim (https://github.com/AwesomeJim/weatherapp/).

      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.





