# Recurse Center Community Portfolio

Discover all the things Recursers have built!

The Recurse Center Community Portfolio
is a website dedicated to
showcasing work done by attendees of
the [Recurse Center](https://www.recurse.com).
The collection is inclusive,
meaning that projects which are small or messy or incomplete are welcome,
and it is author-driven,
meaning that it respects authors' privacy and is uncurated.

Read more about the project [goals](docs/Goals.markdown)
and [features](docs/Features.markdown).

# Getting Started

This project is written in Java 11,
and you will need OpenJDK 11 installed.
Note that as of this writing,
Ubuntu 18.04 contains OpenJDK 10;
you may follow these
[instructions on installing OpenJDK 11 on Ubuntu](https://www.linuxuprising.com/2019/01/how-to-install-openjdk-11-in-ubuntu.html).

## IDE

I recommend the community edition of
[IntelliJ IDEA](https://www.jetbrains.com/idea/).
Project definitions are included in the repo.

## Building

This project uses [gradle](https://gradle.org/)
to manage dependencies and build scripts.
It includes a copy of the
[gradle wrapper](https://docs.gradle.org/5.3.1/userguide/gradle_wrapper.html),
which you can invoke directly:

```
$ ./gradlew build
```

The `build` task will both run the tests and build the project.

You can just run the tests with the `test` task:

```
$ ./gradlew test
```

## Running

There are several environment variables the app needs.
For convenience,
these are consolidated in the file
`.env.template`.
Copy it to `.env`
(which is ignored by git)
with `cp .env.template .env`,
and then edit it to add
the following information.

To run the web app locally,
you will need to configure a
[Recurse Center OAuth application](https://www.recurse.com/settings/apps).
Create an app with the redirect URI to
`http://127.0.0.1:8080/login/oauth2/code/recurse`,
then take the client ID and client secret
and set the environment variables `CLIENT_ID` and `CLIENT_SECRET`.

The app requires a
[PostgreSQL](https://www.postgresql.org/)
database.
Configure your PostgreSQL server to
[allow TCP/IP connections](https://jdbc.postgresql.org/documentation/head/prepare.html),
[create a user with a password](https://www.postgresql.org/docs/current/app-createuser.html),
and then
[create a database](https://www.postgresql.org/docs/current/tutorial-createdb.html).
Put the database URL and credentials in the `JDBC_DATABASE` variables
in your `.env` file.

Then, execute the `bootRun` task:

```
$ source .env
$ ./gradlew bootRun
```

<a href='https://www.recurse.com' title='Made with love at the Recurse Center'><img src='https://cloud.githubusercontent.com/assets/2883345/11325206/336ea5f4-9150-11e5-9e90-d86ad31993d8.png' height='20px'/></a>
![Licensed under the AGPL, version 3](https://img.shields.io/badge/license-AGPL3-blue.svg)
