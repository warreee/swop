-----------------------
Task Manager [groep 11]
-----------------------

* Run zonder input:

    java -jar system.jar

* Run met input:
   
    java -jar system.jar yaml

Het input-bestand moet in "./input/input.tman" staan

--------------------------------------------------------------------------

Commando's:

    help              Toon een lijst van beschikbare commando's

    exit              Eindig het programma
   
    cancel            Annuleer de uitvoering van een commando

    logon             Meld aan in een branch office
                      (zoals in de use case "Office Login")

    logout            Meld af uit een branch office
                      (voorzien om van branch office te kunnen wisselen)

    show projects     Toon de projecten van alle branch offices
                      (zoals in de use case "Show Projects")

    create project    Maak een nieuw project aan in een branch office,
                      enkel indien aangemeld als project manager
                      (zoals in de use case "Create Project")

    create task       Maak een taak aan aan in een branch office,
                      enkel indien aangemeld als project manager
                      (zoals in de use case "Create Task")
                    
    plan task         Plan een taak in een branch office,
                      enkel indien aangemeld als project manager
                      (zoals in de use case "Plan Task",
                      en indien nodig ook "Resolve Conflict")

    update task       Pas de status van een beschikbare taak aan in een
                      branch office, enkel indien aangemeld als developer
                      (zoals in de use case "Update Task Status")

    advance time      Wijzig de systeemtijd
                      (zoals in de use case "Advance Time")
                    
    start simulation  Start een simulatie in een branch office,
                      enkel indien aangemeld als project manager
                      (zoals in de use case "Running a Simulation")
                      
                      realize simulation  Realiseer de simulatie
                      
                      cancel              Stop de simulatie en revert
