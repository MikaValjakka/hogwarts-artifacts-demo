package xyz.mikavee.hogwartsartifactsonline.artifact;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.mikavee.hogwartsartifactsonline.artifact.utils.IdWorker;
import xyz.mikavee.hogwartsartifactsonline.wizard.Wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    // InjectMocks injects the Mocks.
    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");


        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");

        this.artifacts = new ArrayList<>();
        artifacts.add(a1);
        artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {

        // Given
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        a.setOwner(w);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));

        // When
        Artifact returnedArtifact = artifactService.findById("1250808601744904192");

        // Then
        assertThat(returnedArtifact.getId()).isEqualTo(a.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(a.getImageUrl());

        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindByIdNotFound(){

        //Given
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        //When
        Throwable thrown = catchThrowable(() ->{
            Artifact returnedArtifact = artifactService.findById("1250808601744904192");
        });

        //Then
        assertThat(thrown).isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with id 1250808601744904192 :(");
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindAllSuccess(){
        //given
        given(artifactRepository.findAll()).willReturn(this.artifacts);

        // when
        List<Artifact> actualArtifacts = artifactService.findAll();

        //then
        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess(){
        //given
        //creating fake artifact to be saved. It has no Id
        Artifact newArtifact = new Artifact();
        newArtifact.setName("The Artifact");
        newArtifact.setDescription("The Artifacts description");
        newArtifact.setImageUrl("ImageUrl");

        //Fake Id created
        given(idWorker.nextId()).willReturn(123456L);
        // Fake saving in database
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        //when
        Artifact savedArtifact = artifactService.save(newArtifact);

        //then
        // Assert that artifact that was created is equal to artifact saved, with generated Id
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());

        verify(artifactRepository,times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess(){
        //Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        oldArtifact.setImageUrl("ImageUrl");

        Artifact update = new Artifact();
        update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("A new description");
        update.setImageUrl("ImageUrl");

        // Fake with given id the oldArtifact object will be found from DB
        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        // When
        Artifact updatedArtifact = artifactService.update("1250808601744904192", update);

        //Then
        assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());

        verify(artifactRepository,times(1)).findById("1250808601744904192");
        verify(artifactRepository,times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound(){
        // Given
        Artifact update = new Artifact();

        update.setName("Invisibility Cloak");
        update.setDescription("A new description");
        update.setImageUrl("ImageUrl");

        // Fake with unknown id the oldArtifact object will NOT be found from DB
        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        // When
        // We are expecting exception and should throw ArtifactNotFoundException
        assertThrows(ArtifactNotFoundException.class, ()->{
            artifactService.update("1250808601744904192", update);
        });

        //Then
        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }

    @Test
    void testDeleteSuccess(){
        //Given
        Artifact artifact = new Artifact();
        artifact.setId("125080860174490419223");
        artifact.setName("Artifact Of Deletion");
        artifact.setDescription("An artifact that will be deleted from hogwarts");
        artifact.setImageUrl("ImageUrl");

        given(artifactRepository.findById("125080860174490419223")).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("125080860174490419223");

        //When
        artifactService.delete("125080860174490419223");

        //then
        verify(artifactRepository,times(1)).deleteById("125080860174490419223");

    }

    @Test
    void testDeleteNotFound(){
        //Given
        // Trying to find Artifact by id but return nothing = not found
        given(artifactRepository.findById("125080860174490419223")).willReturn(Optional.empty());

        //When
        assertThrows(ArtifactNotFoundException.class, ()->{
            artifactService.delete("125080860174490419223");
        });

        //then
        verify(artifactRepository,times(1)).findById("125080860174490419223");
    }
}