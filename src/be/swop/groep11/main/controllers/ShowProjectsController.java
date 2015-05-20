package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Bevat de stappen om de use cases "Show Projects" en "Create Project" uit te voeren.
 */
public class ShowProjectsController extends AbstractController {

    private Company company;

    /**
     * Constructor om een nieuwe project controller te maken.
     * @param projectRepository Project repository om projecten aan toe te voegen
     * @param userInterface
     */
    public ShowProjectsController(Company company, UserInterface userInterface){
        super(userInterface);
        this.company = company;
    }


    /**
     * Voert de stappen voor de use case "Show Projects" uit.
     */
    public void showProjects() {
        try {
            ImmutableList<Project> projects = company.getAllProjects();
            Project project = getUserInterface().selectProjectFromList(projects);
            getUserInterface().showProjectDetails(project);

            List<Task> tasks = project.getTasks();
            Task task = getUserInterface().selectTaskFromList(ImmutableList.copyOf(tasks));
            getUserInterface().showTaskDetails(task);
        } catch (CancelException | EmptyListException e) {
            getUserInterface().printException(e);
        }
    }
}