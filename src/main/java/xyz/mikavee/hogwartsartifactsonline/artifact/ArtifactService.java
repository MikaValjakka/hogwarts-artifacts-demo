package xyz.mikavee.hogwartsartifactsonline.artifact;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.mikavee.hogwartsartifactsonline.artifact.utils.IdWorker;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

    @Autowired
    private final ArtifactRepository artifactRepository;

    @Autowired
    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public  Artifact findById(String artifactId) {
        return this.artifactRepository.findById(artifactId)
                .orElseThrow(()->
                new ArtifactNotFoundException(artifactId));
    }

    public List<Artifact> findAll(){
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact) {

        // create Id and convert long to string
        newArtifact.setId(String.valueOf(idWorker.nextId()));

        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update) {
        //Find Artifact to be updated from database

        return this.artifactRepository.findById(artifactId)
                .map(oldArtifact ->{
                    // Update fields
                    oldArtifact.setName(update.getName());
                    oldArtifact.setDescription(update.getDescription());
                    oldArtifact.setImageUrl(update.getImageUrl());
                    // save Artifact to database (smart save uses ether update or create to Artifact)
                    return this.artifactRepository.save(oldArtifact);
                })
                .orElseThrow(()->new ArtifactNotFoundException(artifactId));
    }

    public void delete(String artifactId){
        this.artifactRepository.findById(artifactId).orElseThrow(()->
            new ArtifactNotFoundException(artifactId)
        );
        this.artifactRepository.deleteById(artifactId);
    }
}
