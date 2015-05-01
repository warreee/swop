-----------------------
Task Manager [groep 11]
-----------------------

* Run zonder input:

    java -jar system.jar

* Run met input:
   
    java -jar system.jar yaml

    En het invoer bestand moet dan in "./input/input.tman" staan.

-------------------------------------------------------------------------

Commando's:

    help              Toon een lijst van beschikbare commando's

    exit              Eindig het programma
   
    cancel            Annuleer de uitvoering van een commando

    show projects     Toon de projecten
                      (zoals in de use case "Show Projects")

    create project    Maak een nieuw project aan
                      (zoals in de use case "Create Project")

    create task       Maak een taak aan
                      (zoals in de use case "Create Task")
                    
    plan task         Plan een taak
                      (zoals in de use case "Plan Task",
                      en indien nodig ook "Resolve Conflict")

    update task       Pas de status van een beschikbare taak aan 
                      (zoals in de use case "Update Task Status")

    advance time      Wijzig de systeemtijd
                      (zoals in de use case "Advance Time")
                    
    start simulation  Start een simulatie
                      (zoals in de use case "Running a Simulation")
                      
                      realize simulation  Realiseer de simulatie
                      
                      cancel              Stop de simulatie en revert
