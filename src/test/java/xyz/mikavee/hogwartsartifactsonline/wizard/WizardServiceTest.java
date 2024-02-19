package xyz.mikavee.hogwartsartifactsonline.wizard;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.mikavee.hogwartsartifactsonline.artifact.Artifact;
import xyz.mikavee.hogwartsartifactsonline.artifact.ArtifactNotFoundException;
import xyz.mikavee.hogwartsartifactsonline.artifact.ArtifactRepository;
import xyz.mikavee.hogwartsartifactsonline.artifact.utils.IdWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp(){

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
    void testFindAllSuccess() {
        //Given
        given(wizardRepository.findAll()).willReturn(this.wizards);

        //When
        List<Wizard> actualWizards = wizardService.findAll();

        //Then
        assertThat(actualWizards.size()).isEqualTo(this.wizards.size());
        verify(wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess(){
        //Given
        // Create new wizard with only name
        Wizard newWizard = new Wizard();
        newWizard.setName("Hermione Granger");

        given(wizardRepository.save(newWizard)).willReturn(newWizard);

        //When
        Wizard savedWizard = wizardService.save(newWizard);

        //Then
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testFindByIdSuccess() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object wizardRepository.
        Wizard w = new Wizard();
        w.setId(1);
        w.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(w)); // Define the behavior of the mock object.

        // When. Act on the target behavior. Act steps should cover the method to be tested.
        Wizard returnedWizard = this.wizardService.findById(1);

        // Then. Assert expected outcomes.
        assertThat(returnedWizard.getId()).isEqualTo(w.getId());
        assertThat(returnedWizard.getName()).isEqualTo(w.getName());
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(this.wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> {
            Wizard returnedWizard = this.wizardService.findById(1);
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(WizardNotFoundException.class)
                .hasMessage("Could not find wizard with id 1 :(");
        verify(this.wizardRepository, times(1)).findById(Mockito.any(Integer.class));
    }


    @Test
    void testUpdateSuccess() {
        // Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1);
        oldWizard.setName("Albus Dumbledore");

        Wizard update = new Wizard();
        update.setName("Albus Dumbledore - update");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);

        // When
        Wizard updatedWizard = this.wizardService.update(1, update);

        // Then
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        verify(this.wizardRepository, times(1)).findById(1);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }
    @Test
    void testUpdateNotFound() {
        // Given
        Wizard update = new Wizard();
        update.setName("Albus Dumbledore - update");

        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        assertThrows(WizardNotFoundException.class, () -> {
            this.wizardService.update(1, update);
        });

        // Then
        verify(this.wizardRepository, times(1)).findById(1);
    }
    @Test
    void testDeleteSuccess() {
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        doNothing().when(this.wizardRepository).deleteById(1);

        // When
        this.wizardService.delete(1);

        // Then
        verify(this.wizardRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound() {
        // Given
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        assertThrows(WizardNotFoundException.class, () -> {
            this.wizardService.delete(1);
        });

        // Then
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testAssignArtifactSuccess() {
        // Given
        Artifact a = new Artifact();
        a.setId("1234567890");
        a.setName("Amazing Artifact");
        a.setDescription("Amazing artifact that will be assign to Incredible wizard.");
        a.setImageUrl("ImageUrl");

        Wizard w1 = new Wizard();
        w1.setId(2);
        w1.setName("Harry Potter");
        w1.addArtifact(a);

        Wizard w2 = new Wizard();
        w2.setId(3);
        w2.setName("Neville Longbottom");

        given(this.artifactRepository.findById("1234567890")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.of(w2));

        // When
        this.wizardService.assignArtifact(3,"1234567890");

        // Then
        assertThat(a.getOwner().getId()).isEqualTo(3);
        assertThat(w2.getArtifacts()).contains(a);
    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizard() {
        // Given
        Artifact a = new Artifact();
        a.setId("1234567890");
        a.setName("Amazing Artifact");
        a.setDescription("Amazing artifact that will be assign to Incredible wizard.");
        a.setImageUrl("ImageUrl");

        Wizard w1 = new Wizard();
        w1.setId(2);
        w1.setName("Harry Potter");
        w1.addArtifact(a);


        given(this.artifactRepository.findById("1234567890")).willReturn(Optional.of(a));
        // No wizard will be found
        given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

        // When
        Throwable thrown = assertThrows(WizardNotFoundException.class, () -> {
            this.wizardService.assignArtifact(3,"1234567890");
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(WizardNotFoundException.class)
                        .hasMessage("Could not find wizard with id 3 :(");
        assertThat(a.getOwner().getId()).isEqualTo(2);

    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifact() {
        // Given

        // Artifact won't exist


        given(this.artifactRepository.findById("1234567890")).willReturn(Optional.empty());


        // When
        Throwable thrown = assertThrows(ArtifactNotFoundException.class, () -> {
            this.wizardService.assignArtifact(3,"1234567890");
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with id 1234567890 :(");

    }
}
