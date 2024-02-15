package xyz.mikavee.hogwartsartifactsonline.wizard.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import xyz.mikavee.hogwartsartifactsonline.wizard.Wizard;
import xyz.mikavee.hogwartsartifactsonline.wizard.dto.WizardDto;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDto, Wizard> {
    @Override
    public Wizard convert(WizardDto source) {
        Wizard wizard = new Wizard();
        wizard.setId(source.id());
        wizard.setName(source.name());
        return wizard;
    }
}
