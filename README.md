# easy-battery


## Name

easy-battery : Android application from my easy brand


## Description

Small Android application to monitor the battery level

## Technologies covered

- Kotlin
- KSP
- Compose
- Coroutines
- Flow
- Room (database ORM)
- Architecture (database-dao-repository-viewmodel)
- Hilt (injection framework)
- Navigation 3
- WorkManager
- DataStore
- Chart

Rq : It is the best project about DataStore
Android is silly : At first I would like to register for power intent. They are no more sent to app
(battery leakage). The suggested hack is to create a OneShot Worker to have an event is power change.
But it is a OneShot. So I follow with a new suggestion chain OneShot, register a next OneShop before
the worker dies. I enter in a loop if the power cord is connected :). Last suggestion : register a
Repeat one. Yes it is exactly what I do from the beginning with my periodic worker to scan the battery status.
So it is a giant coding loop from nowhere to ... nowhere.
Best : first idea come from the official documentation stating how to register for power broadcasts. So they
forget to remove deprecated api. 

The application borrowed from Waiter is much more simplified (hilt is better now).

## Entities

- Battery 

## Relations 


## Special tricks

Periodic worker registered in settings and launch every 30 minutes by default
Vico display a plot of the recorded battery states

## Dependencies

- Mainly my beloved android lib (all standard required libs)
- Chart library : vico

### In libs.version.toml
    [versions]
    hassenforder-android = "XX"

    [libraries]
    fr-uha-android = { module = "fr.uha.hassenforder:easy.android", version.ref = "hassenforder.android" }

### In settings-gradle.kts

    dependencyResolutionManagement {
       repositories {
            ...
            maven(url = "https://maven.pkg.github.com/hassenforder/easy-android") {
                credentials {
                    username = providers.gradleProperty("github-user").get()
                    password = providers.gradleProperty("github-pat").get()
                }
            }
            maven(
                url = "https://jitpack.io"
            )
            ...
        }
    }

## License

Copyright © 2026 Hassenforder

Software released under the MIT license.

See the LICENSE file for more information.

https://opensource.org/licenses/MIT

## Project status

Ongoing
