package xyz.mikavee.hogwartsartifactsonline.artifact.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import xyz.mikavee.hogwartsartifactsonline.artifact.Artifact;
import xyz.mikavee.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import xyz.mikavee.hogwartsartifactsonline.wizard.converter.WizardToWizardDtoConverter;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {

    @Autowired
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardToWizardDtoConverter) {
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
    }

    @Override
    public ArtifactDto convert(Artifact source) {
        ArtifactDto artifactDto = new ArtifactDto(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getImageUrl(),
                source.getOwner() != null
                        ? wizardToWizardDtoConverter.convert(source.getOwner())
                        : null);
        return artifactDto;
    }
}
