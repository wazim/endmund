# Endmund

<a href="https://travis-ci.org/wazim/endmund">
<img src="https://travis-ci.org/wazim/endmund.svg?branch=master">
</a>

End to End testing utility for Project Edmund.

Written in Java using an AngularJS frontend making use of WebSockets to keep the user interface constantly in sync with Endmund's progress. 

## What it does
Endmund's goal is to test Project Edmund. Endmund doesn't care about how it gets there - only that it does. Endmund leaves Edmund's internals alone and only throws a message at it and has an expected solution that it hopes it matches against. There are no assertions as Endmund's only use is measurement.

## How it works
Endmund wants to ensure that it is testing against the latest version of Project Edmund. In order to do this involves Project Edmund's JAR artifact to be published to an Amazon S3 bucket where Endmund can pick it up on a schedule. 

Endmund deploys Edmund, starts it up and starts hammering it with clues. Endmund publishes its results to a Postgres database stored in the cloud and updates the front end.

Upon completion, Endmund will forcefully shut down Project Edmund and delete the leftovers. 

The cycle will begin again when the user defined interval has finished.
