package xyz.mikavee.hogwartsartifactsonline.artifact;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.mikavee.hogwartsartifactsonline.artifact.converter.ArtifactDtoToArtifactConverter;
import xyz.mikavee.hogwartsartifactsonline.artifact.converter.ArtifactToArtifactDtoConverter;
import xyz.mikavee.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import xyz.mikavee.hogwartsartifactsonline.system.Result;
import xyz.mikavee.hogwartsartifactsonline.system.StatusCode;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    @Autowired // <- optional as artifactService is automatically injected to constructor
    private final ArtifactService artifactService;
    @Autowired
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    @Autowired
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    // Constructor
    public ArtifactController(ArtifactService artifactService, ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter, ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId) {
        Artifact foundArtifact = this.artifactService.findById(artifactId);
        ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(foundArtifact);

        return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
    }

    @GetMapping()
    public Result findAllArtifacts() {
        List<Artifact> foundArtifacts =  this.artifactService.findAll();
        List<ArtifactDto> artifactDtos =
        foundArtifacts.stream().map(fA -> this.artifactToArtifactDtoConverter.convert(fA))
                .collect(Collectors.toList());
        return new Result(true,StatusCode.SUCCESS,"Find All Success", artifactDtos);
    }

    @PostMapping()
    public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto){

        Artifact artifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = this.artifactService.save(artifact);
        ArtifactDto savedArtifactDto =  this.artifactToArtifactDtoConverter.convert(savedArtifact);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedArtifactDto);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(@PathVariable  String artifactId, @Valid @RequestBody ArtifactDto artifactDto){

        Artifact update = this.artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact updatedArtifact = this.artifactService.update(artifactId, update);
        ArtifactDto updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updatedArtifact);

        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedArtifactDto);
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId){
        this.artifactService.delete(artifactId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }



}
