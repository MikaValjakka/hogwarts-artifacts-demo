package xyz.mikavee.hogwartsartifactsonline.artifact.dto;

import jakarta.validation.constraints.NotEmpty;
import xyz.mikavee.hogwartsartifactsonline.wizard.dto.WizardDto;

public record ArtifactDto(
        String id,
        @NotEmpty(message = "name is required.")
        String name,
        @NotEmpty(message = "description is required.")
        String description,
        @NotEmpty(message = "image is required.")
        String imageUrl,
        WizardDto owner
) {
}
