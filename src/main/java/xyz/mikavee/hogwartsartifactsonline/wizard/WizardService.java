package xyz.mikavee.hogwartsartifactsonline.wizard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WizardService {

    @Autowired
    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
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
}
