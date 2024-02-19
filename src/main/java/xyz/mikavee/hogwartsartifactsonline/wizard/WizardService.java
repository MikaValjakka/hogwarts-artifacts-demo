package xyz.mikavee.hogwartsartifactsonline.wizard;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.mikavee.hogwartsartifactsonline.artifact.Artifact;
import xyz.mikavee.hogwartsartifactsonline.artifact.ArtifactNotFoundException;
import xyz.mikavee.hogwartsartifactsonline.artifact.ArtifactRepository;

import java.util.List;

@Service
@Transactional
public class WizardService {

    @Autowired
    private final WizardRepository wizardRepository;

    @Autowired
    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public List<Wizard> findAll() {
            return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard) {
        return this.wizardRepository.save(newWizard);
    }

    public Wizard findById(Integer wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(()-> new WizardNotFoundException(wizardId));

    }

    public Wizard update(int i, Wizard update) {

            return this.wizardRepository.findById(i)
                    .map(wizardToUpdate -> {
                        wizardToUpdate.setName(update.getName());
                        return this.wizardRepository.save(wizardToUpdate);
                    })
                    .orElseThrow(()-> new WizardNotFoundException(i));

    }

    public void delete(Integer wizardId) {
        // get entity from database or else create WizardNotFoundException
        Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException(wizardId));

        // Before deletion artifacts related to entity must be unsigned
        wizardToBeDeleted.removeAllArtifacts();

        this.wizardRepository.deleteById(wizardId);

    }

    public void assignArtifact(Integer wizardId, String artifactId) {

        // Find this artifact by Id from DB.

        Artifact foundArtifact = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));

        // Find this wizard by id from DB
        Wizard foundWizard = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new WizardNotFoundException(wizardId));

        // Artifact assignment
        if(foundArtifact.getOwner() != null) {
            System.out.println("------This artifact has owner" + foundArtifact.getOwner());
            foundArtifact.getOwner().removeArtifact(foundArtifact);
            System.out.println("------This artifact has  NO owner: " + foundArtifact.getOwner());
            System.out.println("------Trying to remove");

        }
        foundWizard.addArtifact(foundArtifact);
    }
}
