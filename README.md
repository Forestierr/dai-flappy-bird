# dai-flappy-bird

This project has been made in the context of [DAI](https://github.com/heig-vd-dai-course/) courses at HEIG-VD

## Authors

- [Antoine Leresche](https://github.com/a2va)
- [Robin Forestier](https://github.com/forestierr)

## Description

The objective of this project is to implement a simple Flappy Bird game via a client-server application.

### Single player

The game may be played in single-player mode.
The objective is to evade the pipes by depressing the space bar, thereby enabling the bird to fly.

### Two players

> [!CAUTION]
> The multiplayer mode is not yet implemented.
> Be free to contribute to the project to implement it.

The game may be played in a multiplayer mode, with one player assuming the role of the bird and the other placing the 
pipes.

# 1. Build

Flappy Bird has been developed using a Maven repository with a multi-module architectural structure. 
To run the game, it is necessary to open two separate terminals. 

The first terminal should be used to start the server 
module, which is responsible for managing player interactions and maintaining the overall game state. 

The second terminal should be used to launch the client module, which serves as the user interface and allows players to
interact with the game. This module communicates with the server to send player inputs and receive real-time updates on 
the game's state.

## 1.1 Requirements

It is imperative that the Java Runtime Environment 21 (JRE) or Java Development Kit 21 (JDK) is installed on the 
computer in question.
It is essential that the version of Java in use is compatible with the application.

It is possible to utilise Docker to facilitate the operation of the server.

## 1.2 Build the application

Start by cloning the repository:
```bash
git clone https://github.com/forestier/dai-flappy-bird.git
```

Then, you can compile the project into a JAR located into the target folder:
```bash
./mvnw package
```

# 2. Run the application

> [!IMPORTANT]
> On windows, you have to use `javaw` command to run the game.

```bash
java -jar target/dai-flappy-bird-1.0-SNAPSHOT.jar -h
```

You can use the `-h` option to display the help message.

## 2.1 Run the client

To run the client, you can use the same command with the `client` argument.

```bash
java -jar target/dai-flappy-bird-1.0-SNAPSHOT.jar client
```

By default, the server will listen on port 2000 and the client will connect to the localhost.
You can specify the server address and port with the `--host` and `-p` options for the port.

```bash
java -jar target/dai-flappy-bird-1.0-SNAPSHOT.jar --host="127.0.0.1" -p 2000
```

## 2.2 Launch the server

```bash
java -jar target/dai-flappy-bird-1.0-SNAPSHOT.jar server
```

You can specify the port with the `-p` option.

```bash
java -jar target/dai-flappy-bird-1.0-SNAPSHOT.jar server -p 2000
```

## 2.3 Run the server with Docker

### Build the Docker image

```bash
docker build -t flappy-bird .
```

### Run the server

```bash
docker run -p 2000:2000 flappy-bird server
```

## 2.4 How to play

### Single player

- Press the space bar to make the bird fly
- Avoid the pipes
- The game ends when the bird hits a pipe or when the bird touch the ground
- Have fun!

### Multiplayer

> [!CAUTION]
> We haven't got multiplayer mode up and running yet
> The following instructions are intended for the future implementation.

- Player 1: Press `m` to create a lobby
- Player 2: Press `j` to join a lobby
- Players 1 and 2: Press the space bar to start the game
- Player 1: Press the space bar to make the bird fly
- Player 2: Press `up` and `down` to move the pipe and press `enter` to place it
- The game ends when the bird hits a pipe or when the bird touch the ground
- Have fun!

# 3. Application Protocol

## 3.1 Overview

The "Flap" protocol is a communication protocol that allows a client to connect to a server to play a Flappy Bird game.

## 3.2 Transport protocol

The "Flap" protocol is a test based protocol. It uses TCP transport protocol and the port 2000 to communicate.
Every message must be encoded in UTF-8 and delimited by a newline character (\n). The messages are treated as text messages.

The communication must be established by the client. Once the connection has been established, the server responds with `ACKK` to tell the client that it is ready.
It can send either `STRT` or `JOIN`.

- If the client send `STRT`, the game starts and the client can send `FLYY` commands and the server responds with `DATA`
- If the client send `JOIN`, the server will create a lobby if there wasn't one, respond with a lobby id, then wait until a second client has joined the lobby. The second client can join the same lobby by providing a number to the `JOIN` command.
One client will play the bird and send `FLYY` command and the other will place the pipes with the `PIPE` command.
The client can quit the game at any time with the `QUIT` command.

## 3.3 Message protocol

The protocol is based on a simple text-based message exchange. Each message is a string of characters encoded in UTF-8.

### Client messages

| Message               | Description                               |
|-----------------------|-------------------------------------------|
| STRT                  | Start the game                            |
| FLYY                  | Indicate that the bird is going to fly up |
| JOIN \<num\>          | Join the lobby                            |
| LIST                  | List all existing lobby                   |
| PIPE \<position _y_\> | Place a new pipe                          |
| PING                  | Request data for display                  |
| QUIT                  | Quit the game                             |

_LOBY_, _JOIN_, _LIST_, _PIPE_ is for multiplayer case.

### Server messages

| Message          | Description                       |
|------------------|-----------------------------------|
| ACKK             | Send an ACK to the client         |
| DATA             | All the data to show on screen    |
| DEAD             | End of the game / death of flappy |
| EROR \<message\> | Error message                     |

- `ACKK`: It is used by the server that it acknowledge the previous command of the client, if some information must communicated by the server the DATA cmd is used.
- `DATA`: This message is sent is in reponse to FLYY, PIPE, JOIN, LIST. For each information to pass a comma , must be used.
For `FLYY` and `PIPE` commands it look like this: `DATA B x y, P x y w, ..., P x y w, S s` here B stands for Bird and P for Pipe and S for Score.
For the `JOIN` command: `DATA n` where n is the lobby that was created by the command.
For the `LIST` command: `DATA n,o,p,...` where n, o, p are all available lobby to join.

## 3.4 Sequence diagram

### One player

![OnePlayer.png](docs/OnePlayer.png)

### Two players

![TwoPlayers.png](docs/TwoPlayers.png)

### Full lobby scenario

![LobbyFull.png](docs/LobbyFull.png)
