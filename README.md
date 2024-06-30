# Project kapregh

### Version: 1.0.2-SNAPSHOT
### This is a build tool ***for Kappa server***.
### [Website](https://github.com/smbServer/Kapregh)
### [LICENSE](https://github.com/smbServer/Kapregh/blob/main/LICENSE)
### Related Projects:
- Kappa-server
- Kappa-api
### Credit
> 1, Kappa developer\
> 2, Frish2021
### Thanks
> 1, Kercute\
> 2, ECUICU

---

## How to use it?

### step 1. (Add a maven repository.)

### settings.gradle.kts
```kotlin
pluginManagement {
    repositories {
        maven("https://smbServer.github.io/") {
            name = "sun moon bay"
        }
    }
}
```

### settings.gradle
```groovy
pluginManagement {
    repositories {
        maven {
            url = "https://smbServer.github.io/"
            name = "sun moon bay"
        }
    }
}
```

### step 2. (Add the plugin and enable it.)

### build.gradle.kts
```kotlin
plugins {
    id("net.kappamc.kapregh") version "1.0.2-SNAPSHOT"
}
```

### build.gradle
```groovy
plugins {
    id "net.kappamc.kapregh" version "1.0.2-SNAPSHOT"
}
```

---

## How to build it.

### step 1.
### Clone a repository via git.
### Git bash enters the following command to clone the repository.
> git clone https://github.com/smbServer/Kapregh.git

### step 2. (Check the JDK version)
### Enter the following command to check if the JDK version is higher than JDK 17.
> java -version
### If the JDK version is earlier than 17, upgrade the JDK version.

### step 3. (Start building)
### Enter the following command to build the project.
### Microsoft Windows
```cmd
.\gradlew.bat publish
```

### Linux or MacOSX
```bash
.\gradlew publish
```

---
