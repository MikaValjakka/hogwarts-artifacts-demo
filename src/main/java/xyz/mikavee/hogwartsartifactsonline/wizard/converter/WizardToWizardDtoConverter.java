package xyz.mikavee.hogwartsartifactsonline.wizard.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import xyz.mikavee.hogwartsartifactsonline.wizard.Wizard;
import xyz.mikavee.hogwartsartifactsonline.wizard.dto.WizardDto;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {

    @Override
    public WizardDto convert(Wizard source) {
        WizardDto wizardDto = new WizardDto(
                source.getId(),
                source.getName(),
                source.numOfArtifact());
        return wizardDto;
    }
}
