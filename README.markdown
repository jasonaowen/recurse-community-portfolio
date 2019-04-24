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
both an OAuth application
and a personal access token
in your
[Recurse Center app settings](https://www.recurse.com/settings/apps).
Create an app with the redirect URI to
`http://127.0.0.1:8080/login/oauth2/code/recurse`,
then take the client ID and client secret
and set the environment variables `CLIENT_ID` and `CLIENT_SECRET`.
Create a personal access token
and set the environment variable `ACCESS_TOKEN`.

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

Populate the database
by running the API sync:

```
$ source .env
$ ./gradlew bootRun --args="apiSync"
```

Then, execute the `bootRun` task
to start the web server:

```
$ source .env
$ ./gradlew bootRun
```

The server should now be accessible at http://127.0.0.1:8080/.

## Deploying

### Heroku

This app can be deployed to Heroku.

First, create an application:

```sh
$ heroku apps:create
```

Create an OAuth application
and a personal access token
in your
[Recurse Center app settings](https://www.recurse.com/settings/apps).
Set the OAuth callback URL to be
`https://your-application-name.herokuapp.com/login/oauth2/code/recurse`.
Configure your Heroku app with the credentials:


```sh
$ heroku config:set \
    ACCESS_TOKEN=your_access_token
    CLIENT_ID=your_client_id \
    CLIENT_SECRET=your_client_secret
```

Install the
[Heroku PostgreSQL add-on](https://devcenter.heroku.com/articles/heroku-postgresql):

```sh
$ heroku addons:create heroku-postgresql:hobby-dev
```

(Heroku
[automatically configures](https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java)
the
`JDBC_DATABASE_URL`,
`JDBC_DATABASE_USERNAME`,
and
`JDBC_DATABASE_PASSWORD`
environment variables
when it detects a Java application.)

Creating the application
on the command line
should automatically configure a new git remote.
Push the code to Heroku:

```sh
$ git push heroku master
```

Then, populate the database:

```sh
$ heroku run 'java $JAVA_OPTS -jar build/libs/*.jar apiSync'
```

Your Heroku instance should be ready to use!

<a href='https://www.recurse.com' title='Made with love at the Recurse Center'><img src='https://cloud.githubusercontent.com/assets/2883345/11325206/336ea5f4-9150-11e5-9e90-d86ad31993d8.png' height='20px'/></a>
![Licensed under the AGPL, version 3](https://img.shields.io/badge/license-AGPL3-blue.svg)
