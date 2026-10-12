package solis3d.projectvaultbackend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.entities.Category;
import solis3d.projectvaultbackend.entities.Project;
import solis3d.projectvaultbackend.entities.ProjectStatus;
import solis3d.projectvaultbackend.entities.ProjectViewerType;
import solis3d.projectvaultbackend.entities.ProjectVisibility;
import solis3d.projectvaultbackend.entities.Role;
import solis3d.projectvaultbackend.repositories.AppUserRepository;
import solis3d.projectvaultbackend.repositories.CategoryRepository;
import solis3d.projectvaultbackend.repositories.ProjectRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ArtistControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private AppUser artist;
    private Category category;

    @BeforeEach
    void setUp() {
        this.category = this.categoryRepository.findByName("Environment")
                .orElseThrow();

        AppUser newArtist = new AppUser();
        newArtist.setFirstName("Tester");
        newArtist.setLastName("Test");
        newArtist.setUsername("test-artist");
        newArtist.setEmail("test-artist@projectvault.local");
        newArtist.setPassword("password");
        newArtist.setRole(Role.USER);
        newArtist.setAvatarUrl("https://example.com/avatar.jpg");
        newArtist.setPosition("3D test Artist");
        newArtist.setBio("I'm just a test artist");
        newArtist.setCreatedAt(LocalDateTime.now());

        this.artist = this.appUserRepository.save(newArtist);

        this.projectRepository.save(
                this.createProject(
                        "Public test project",
                        ProjectVisibility.PUBLIC
                )
        );

        this.projectRepository.save(
                this.createProject(
                        "Private test project",
                        ProjectVisibility.PRIVATE
                )
        );
    }

    @Test
    void shouldReturnPublicArtistWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get("/artists/{username}", this.artist.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(this.artist.getUsername()))
                .andExpect(jsonPath("$.firstName").value("Tester"))
                .andExpect(jsonPath("$.position").value("3D test Artist"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void shouldReturnOnlyPublicProjects() throws Exception {
        this.mockMvc.perform(
                        get(
                                "/artists/{username}/projects",
                                this.artist.getUsername()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Public test project"))
                .andExpect(jsonPath("$.content[0].projectVisibility").value("PUBLIC"));
    }

    @Test
    void shouldReturnNotFoundForUnknownArtist() throws Exception {
        this.mockMvc.perform(
                        get("/artists/{username}", "unknown-artist")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchPublicArtistsWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get("/artists")
                                .param("query", " TEST-ARTIST ")
                                .param("page", "0")
                                .param("size", "12")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username")
                        .value(this.artist.getUsername()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].email").doesNotExist())
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.content[0].role").doesNotExist());
    }

    @Test
    void shouldReturnEmptyPageWhenNoArtistMatchesQuery() throws Exception {
        this.mockMvc.perform(
                        get("/artists")
                                .param("query", "non-existent-artist")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldExcludeArtistsWithOnlyPrivateProjects() throws Exception {
        List<Project> artistProjects = this.projectRepository
                .findByOwner_Id(this.artist.getId(), Pageable.unpaged())
                .getContent();

        for (Project project : artistProjects) {
            project.setProjectVisibility(ProjectVisibility.PRIVATE);
        }

        this.projectRepository.saveAllAndFlush(artistProjects);

        this.mockMvc.perform(
                        get("/artists")
                                .param("query", this.artist.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldPaginatePublicArtistsWithoutDuplicates() throws Exception {
        AppUser secondArtist = new AppUser();
        secondArtist.setFirstName("Second");
        secondArtist.setLastName("Artist");
        secondArtist.setUsername("z-test-artist");
        secondArtist.setEmail("second-artist@projectvault.local");
        secondArtist.setPassword("password");
        secondArtist.setRole(Role.USER);
        secondArtist.setCreatedAt(LocalDateTime.now());

        secondArtist = this.appUserRepository.save(secondArtist);

        this.projectRepository.save(
                this.createProject(
                        "Another public project",
                        ProjectVisibility.PUBLIC
                )
        );

        Project secondArtistProject = this.createProject(
                "Second artist public project",
                ProjectVisibility.PUBLIC
        );
        secondArtistProject.setOwner(secondArtist);

        this.projectRepository.saveAndFlush(secondArtistProject);

        this.mockMvc.perform(
                        get("/artists")
                                .param("query", "test-artist")
                                .param("page", "0")
                                .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username")
                        .value(this.artist.getUsername()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.last").value(false));

        this.mockMvc.perform(
                        get("/artists")
                                .param("query", "test-artist")
                                .param("page", "1")
                                .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username")
                        .value(secondArtist.getUsername()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALICE", "Example", "Alice Example"})
    void shouldSearchArtistsByFirstLastAndFullName(String query) throws Exception {
        this.artist.setFirstName("Alice");
        this.artist.setLastName("Example");
        this.appUserRepository.saveAndFlush(this.artist);

        this.mockMvc.perform(
                        get("/artists")
                                .param("query", query)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username")
                        .value(this.artist.getUsername()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }


    private Project createProject(
            String title,
            ProjectVisibility visibility
    ) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription("Project created for testing");
        project.setProjectStatus(ProjectStatus.COMPLETED);
        project.setProjectVisibility(visibility);
        project.setViewerType(ProjectViewerType.NONE);
        project.setFeatured(false);
        project.setCreatedAt(LocalDateTime.now());
        project.setOwner(this.artist);
        project.setCategory(this.category);

        return project;
    }
}