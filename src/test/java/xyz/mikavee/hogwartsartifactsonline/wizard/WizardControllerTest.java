package xyz.mikavee.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.mikavee.hogwartsartifactsonline.artifact.ArtifactNotFoundException;
import xyz.mikavee.hogwartsartifactsonline.system.StatusCode;
import xyz.mikavee.hogwartsartifactsonline.wizard.dto.WizardDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class WizardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WizardService wizardService;

    @Autowired
    ObjectMapper objectMapper;

    List<Wizard> wizards;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        this.wizards = new ArrayList<>();

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");
        this.wizards.add(w3);
            }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllWizardsSuccess() throws Exception {
        // Given
        given(this.wizardService.findAll()).willReturn(this.wizards);


        //When and Then
        this.mockMvc.perform(MockMvcRequestBuilders.get(this.baseUrl+"/wizards").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.wizards.size())))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Harry Potter"))
                .andExpect(jsonPath("$.data[2].id").value(3))
                .andExpect(jsonPath("$.data[2].name").value("Neville Longbottom"));

    }

    @Test
    void testSaveWizardSuccess() throws Exception {
        //Given
        WizardDto wizardDto = new WizardDto(null,"New Wizard",0);

        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard savedWizard = new Wizard();
        savedWizard.setId(123);
        savedWizard.setName("New Wizard");



        given(this.wizardService.save(Mockito.any(Wizard.class))).willReturn(savedWizard);

        // When and Then
        this.mockMvc.perform(MockMvcRequestBuilders.post(this.baseUrl+"/wizards").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedWizard.getName()));


    }

    @Test
    void testFindWizardByIdSuccess() throws Exception {
        // Given. Arrange inputs and targets. Define the behavior of Mock object wizardService.
        given(this.wizardService.findById(1)).willReturn(this.wizards.get(0));

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.get(this.baseUrl+"/wizards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));
    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        // Given.
        given(this.wizardService.findById(5)).willThrow(new WizardNotFoundException(5));

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.get(this.baseUrl+"/wizards/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }



    @Test
    void testUpdateWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Updated wizard name", 0);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(1);
        updatedWizard.setName("Updated wizard name");

        String json = this.objectMapper.writeValueAsString(updatedWizard);

        // Given. Arrange inputs and targets. Define the behavior of Mock object wizardService.
        given(this.wizardService.update(eq(1), Mockito.any(Wizard.class))).willReturn(updatedWizard);

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.put(this.baseUrl+"/wizards/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Updated wizard name"));
    }
    @Test
    void testDeleteWizardSuccess() throws Exception {
        // Given. Arrange inputs and targets. Define the behavior of Mock object wizardService.
        doNothing().when(this.wizardService).delete(3);

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.delete(this.baseUrl+"/wizards/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    void testDeleteWizardErrorWithNonExistentId() throws Exception {
        // Given
        int nonExistentId = 5;
        doThrow(new WizardNotFoundException(nonExistentId)).when(this.wizardService).delete(nonExistentId);

        // When and Then
        this.mockMvc.perform(MockMvcRequestBuilders.delete(this.baseUrl+"/wizards/" + nonExistentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with id " + nonExistentId + " :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactSuccess() throws Exception {
        // Given
        doNothing().when(this.wizardService).assignArtifact(2,"1234567890");


        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.put(this.baseUrl+"/wizards/2/artifacts/1234567890").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception {
        // Given
        doThrow(new WizardNotFoundException(5)).when(this.wizardService).assignArtifact(5,"1234567890");


        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.put(this.baseUrl+"/wizards/5/artifacts/1234567890").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with id 5 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception {
        // Given
        doThrow(new ArtifactNotFoundException("1234567891")).when(this.wizardService).assignArtifact(2,"1234567891");


        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.put(this.baseUrl+"/wizards/2/artifacts/1234567891").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with id 1234567891 :("))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}
