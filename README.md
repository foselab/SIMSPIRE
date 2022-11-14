# INSPIRE

INSPIRE is a simulation software for testing mechanical ventilators through the creation of a digital twin for the respiratory system. It comes with a Swing interface so that it can be used more easily, but it is also available as a web-app. 

## Project overview

The project is composed of four main components:
* a circuit simulator which is able to solve electrical circuits 
* a lung simulator which manages the chosen model for the respiratory system and the simulation business logic
* a ventilator simulator which simulates the ventilator that has to be tested
* a graphic user interface

## Installation (Swing interface)

## Installation (web-app)
In order to start the web-app, the following folders have to be downloaded: circuit-simulator-master, lungsimulator-lib, zeromq_schema_ventilation and web-app. 
Java 11 is also required.

First of all, run command `mvn install` for circuit-simulator-master and lungsimulator-lib and then run command `mvn jetty:run` for the web-app project.
If all commands succeeded, the web-app can be started from your browser at `localhost:8080`, otherwise check [Troubleshooting](#troubleshooting) section for help.

Finally, run manually the main class of zeromq_schema_ventilation folder.

## How to build a custom model with YAML

## Swing interface usage

## Web-app usage

## Troubleshooting

* circuit-simulator-master: risolutore di circuiti elettrici
* lungsimulator: simulatore del polmone con interfaccia costruita in Swing
* zeromq_schema_ventilation: simulatore del ventilatore
* lungsimulator-lib: business logic del simulatore del polmone
* web-app: web app costruita con Vaadin per il simulatore del polmone

- altri progetti di prova
* state_machine_controller\mvm-yakindu-controller: state machine con adaptive

Simulazione con l'interfaccia in Swing:
* scaricare circuit-simulator-master, lungsimulator e zeromq_schema_ventilation
* il main è nel progetto lungsimulator

