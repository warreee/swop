package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
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
    private SystemTime systemTime;

    /**
     * Constructor om een nieuwe project controller te maken.
     * @param userInterface Het userInterface object dat gebruikt wordt om alles te laten zien.
     * @param systemTime de systeemtijd van het systeem.
     * @param company De company waar alles in zit.
     *
     */
    public ShowProjectsController(Company company, UserInterface userInterface, SystemTime systemTime){
        super(userInterface);
        this.company = company;
        this.systemTime = systemTime;
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
            getUserInterface().showTaskDetails(task, systemTime.getCurrentSystemTime());
        } catch (CancelException | EmptyListException e) {
            getUserInterface().printException(e);
        }
    }
}