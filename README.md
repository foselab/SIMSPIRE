# mvm-adapt
materiale relativo al progetto MVM adapt
- simulatore di paziente in stile DT

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

Simulazione con web-app:
* Java 11 required
* scaricare circuit-simulator-master, lungsimulator-lib, zeromq_schema_ventilation e web-app
* da web-app, aprire il cmd ed eseguire il comando mvn jetty:run
* una volta avviata la web-app digitare su browser localhost:8080
