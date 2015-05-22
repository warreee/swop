package be.swop.groep11.main.controllers;


import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.ui.UserInterface;

import java.time.LocalDateTime;

/**
 * Bevat de stappen om de use cases "Show Projects" en "Create Project" uit te voeren.
 */
public class CreateProjectController extends AbstractController {


    private LogonController logonController;

    /**
     * Constructor om een nieuwe project controller te maken.
     * @param projectRepository Project repository om projecten aan toe te voegen
     * @param userInterface
     */
    public CreateProjectController(UserInterface userInterface, LogonController logonController){
        super(userInterface);
        this.logonController = logonController;
    }

    /**
     * Voert de stappen voor de use case "Create Project" uit.
     */
    public void createProject(){
        try {
            ProjectRepository projectRepository = logonController.getBranchOffice().getProjectRepository();
            String projectName =  getUserInterface().requestString("Project naam");
            String description =  getUserInterface().requestString("Project beschrijving");
            LocalDateTime creationTime =  getUserInterface().requestDatum("Creation time");
            LocalDateTime dueTime =  getUserInterface().requestDatum("Due time");

            projectRepository.addNewProject(projectName, description ,creationTime, dueTime);
            getUserInterface().printMessage("Project toegevoegd");
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            createProject();
        } catch (CancelException e) {
            getUserInterface().printException(e);
        }

    }
}