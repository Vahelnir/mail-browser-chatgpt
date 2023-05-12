# Github
https://github.com/Vahelnir/mail-browser-chatgpt <br/> 

School project where we had to create a java application with javafx. 
The application has to list your mails & automatically respond to newly received mail, or create new events in your google calendar.
It was split into multiple parts:
- the javafx client which is fetching all of your mails
- a module to categorise newly received mails into categories
- a module to automatically respond to received mails depending on the mail's category
- a module to automatically create events in your google calendar depending on the mail's category.
- an android application doing all of the above

# JavaFX Client
This part was developed by Christophe, Yasmine, Romain and Valentin.

## Installation

You have to have at least Java 17.<br/> <br/> 
You need to set up your gmail account to allow the application to use it. <br> Please folow https://support.google.com/mail/answer/185833?hl=en-GB.

- in `mail-browser/`:
```sh 
./gradlew build
```
- extract the zip/tar file located in `build/distributions/`
- execute the file (or the .bat on windows) located in `{extracted}/bin/`

## Build avec jlink
- then run
```sh
./gradlew jlink
```
- run `build/image/bin/mail-browser` (or the .bat file on windows)
