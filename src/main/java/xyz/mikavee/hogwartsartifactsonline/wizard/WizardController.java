package xyz.mikavee.hogwartsartifactsonline.wizard;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.mikavee.hogwartsartifactsonline.system.Result;
import xyz.mikavee.hogwartsartifactsonline.system.StatusCode;
import xyz.mikavee.hogwartsartifactsonline.wizard.converter.WizardDtoToWizardConverter;
import xyz.mikavee.hogwartsartifactsonline.wizard.converter.WizardToWizardDtoConverter;
import xyz.mikavee.hogwartsartifactsonline.wizard.dto.WizardDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

    @Autowired
    private final WizardService wizardService;

    @Autowired
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;
    @Autowired
    private final WizardDtoToWizardConverter wizardDtoToWizardConverter;

    public WizardController(WizardService wizardService, WizardToWizardDtoConverter wizardToWizardDtoConverter, WizardDtoToWizardConverter wizardDtoToWizardConverter) {
        this.wizardService = wizardService;
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
        this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
    }

    @GetMapping()
    public Result findAllWizards(){
        // List wizards
        List<Wizard> foundWizards = this.wizardService.findAll();
        // Convert  wizards object to wizardDto
        List<WizardDto> wizardsDtos =
                foundWizards.stream().map(wizard -> this.wizardToWizardDtoConverter.convert(wizard))
                        .collect(Collectors.toList());

        return new Result(true, StatusCode.SUCCESS,"Find All Success", wizardsDtos);
    }

    @PostMapping()
    public Result addWizard(@RequestBody WizardDto wizardDto){
        Wizard wizard = this.wizardDtoToWizardConverter.convert(wizardDto);
        Wizard savedWizard = this.wizardService.save(wizard);
        WizardDto savedWizardDto = this.wizardToWizardDtoConverter.convert(savedWizard);

        return new Result(true,StatusCode.SUCCESS,"Add Success",savedWizardDto);
    }

    @GetMapping("/{wizardId}")
    public Result findWizardById(@PathVariable Integer wizardId) {
        Wizard foundWizard = this.wizardService.findById(wizardId);
        WizardDto wizardDto = this.wizardToWizardDtoConverter.convert(foundWizard);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", wizardDto);
    }

    @PutMapping("/{wizardId}")
    public Result updateWizard(@PathVariable Integer wizardId, @Valid @RequestBody WizardDto wizardDto) {
        Wizard update = this.wizardDtoToWizardConverter.convert(wizardDto);
        Wizard updatedWizard = this.wizardService.update(wizardId, update);
        WizardDto updatedWizardDto = this.wizardToWizardDtoConverter.convert(updatedWizard);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedWizardDto);
    }

    @DeleteMapping("/{wizardId}")
    public Result deleteWizard(@PathVariable Integer wizardId) {
        this.wizardService.delete(wizardId);
        return new Result(true,StatusCode.SUCCESS,"Delete Success");
    }

}
