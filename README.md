Android Template Project
---
This project is modified base on [Android Architecture Sample](https://github.com/android/architecture-samples/) with
the following changes

* Remove data-binding and use view binding only
* Business logic implemented with simple UseCase
* Implement MVC architecture
* Custom navigator to navigate between screens
* Dependency injection with [Dagger-Hilt](https://github.com/google/dagger/)

MVC
---
MVC Controller is responsible for all the communication between view and business model. However in Android platform, we
need to consider the activity/fragment lifecycle as view is created and destroyed based on lifecycle. Therefore,
controller can be separated to two parts, android platform related part and business logic part.

![MVC structure](MVC.jpg)

Unit Test
---

* Unit test for use cases
* UI Integration unit test for each screen