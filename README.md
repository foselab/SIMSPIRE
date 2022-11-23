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
This project has been conceived to allow the user to create and test its own customized model. In order to avoid exceptions and get a better understanding on how the model should be built, a detailed guide is presented. 

A custom model is composed of three different YAML files:
* the circuit model file where all the circuit components are listed
* the archetype file with all the required intial or constant values of each circuit component
* the demographic patient data file where some basic patient's info are provided

#### The circuit model file
The circuit model file has two main fields: a schema number (`schema`) and a circuit components list (`elementsList`). The first one is an arbitrary number which is used to check that both circuit model file and archetype file are associated to the same circuit. Hence, this number must be equal in both files. The latter contains all the elements of the chosen circuit model. Each element is composed of the following fields:
* `elementName`: the name or id of the given element
* `associatedFormula`: a description of the formula that has to be used to compute the element value
* `type`: type of element (resistor, capacitor, etc...)
* `x`, `y`, `x1`, `y1`: coordinates of the element nodes

For instance, a constant resistance description is shown in the following code. The fields `isTimeDependent` and `isExternal` will be both set to false because if the element is constant, it won't have a time dependency and its value has to be reported in the archetype file.  
```yaml
schema: 2
elementsList:
- elementName: Endo-tracheal Tube Resistance
  associatedFormula:
    isTimeDependent: false
    isExternal: false
    formula: resistance1
    variables:
    - resistance1
  type: ResistorElm
  x: 0
  y: 1
  x1: 1 
  y1: 1
```
Instead, if an element is time dependent a proper description would like the following snippet of code. 
```yaml
```

#### The archetype file
#### The demographic patient data file



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

