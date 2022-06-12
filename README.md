# Tic Tac Toe

## A. Details
- Name of the project: Tic Tac Toe
- Name : Shubh Pragnesh Shah
- BITS ID : 2018A7PS0092G
- Email : f20180092@goa.bits-pilani.ac.in

## B. Description and Bugs:
- The app allows a user to play multiplayer Tic Tac Toe with another player online or single player Tic Tac Toe versus a random computer agent.
- The app also keeps a track of game outcomes for all users and displays it on login.
- To access the app users must first login with valid E-mail ids.
- A "log out" action bar menu is shown on both the dashboard and the game fragments. Clicking it
  should log the user out and show the LoginFragment. This click is handled in the MainActivity.
- Lombok is used to generate getters/setters, equalTo methods, constructors and builders for a few classes.
- *Bugs:*
    - No known bugs of yet.
    
## C. Task Completion
### Task 1
We have implemented the sign-in screen with a button which both allows a new user to register into our Firebase Auth Database, or an existing user to log into the app.
Logging in is done using Firebase Auth with email id and a corresponding password (implemented in UserViewModel).
The  handles the logging in and provides methods to retrieve user data by registering one time listeners for firebase data.
We have also included offline Firebase capabilities so that a user can log in offline and play single player games even if they are not connected to the internet.
On logging in, the user is shown a dashboard fragment, where they can see ongoing games by different users and decide to join a two player game.
We also retrieve user score from the database and display it here.
Apart from this, we have a floating action button in order to play new games - Either a single player game, or start a multi player game and wait for some other user to join from their device.

### Task 2
In a double player game, the user who made the game has to wait until another user joins the game and then they can play against each other.
If a user selects a new one-player game, they are directed to a game board where they have to make the first move and the UI waits for the user to mark a cell as 'X'.
Then the UI marks any other cell as 'O', and they keep playing until either the user wins, looses or the game is tied due to no space on the board.
On each of these cases, a Toast is shown to the user, and they are taken back to the dashboard fragment.
On starting the game, the user has an option to click on the back button, and hence forfeit the game, incrementing their loss by one.
The GameViewModel handles this. We have livedata as well.

### Task 3
We have used Firebase for task 3, and are adding new games to a new Games List. We are also storing a new OpenGames List, in which we only add open games, and we delete a game once another user joins the game.
Two different lists are used because if we delete the game from our existing list, the listener goes haywire and the users are not able to play the game.
After every move a user makes, the app checks if the game has ended. If so, it shows the appropriate toast to each user and takes them back to the dashboard fragment.
This is done by using LiveData Listeners on the Games.
The users' score is directly uploaded to the firebase and the dashboard fragment gets their corresponding score using a One Time Data Listener with firebase.
For the open games list, we have used a Persistent Data Listener with Firebase's RT database. This is because at any time, new games can be added by different users on different devices, and we need to be able to see them all.

## D. How To Run
- The app should run out of the box, Realtime database from Firebase and Firebase auth are used for the backend with a project that is configured already. Config files have also been provided at appropriate places. 
- If you wish to create a new Firebase project, please ensure that the realtime database is hosted on the singapore server and config files are placed in the appropriate directories (app/).

## E. Testing & Accessibility
- Testing was not done programmatically due to a lack of time but app was tested manually during development.
- Accessibility scanner was run which pointed out that labels were not set for some of the buttons. Appropriate labels were set for them.

## F. Hours Taken
15

## G. Difficulty
10