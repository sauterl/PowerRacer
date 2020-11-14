package shared.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import shared.game.model.RaceTrackModel;

/**
 * Manages loading other entity and IO releated things.
 *
 * @author loris.sauter
 */
public class EntityManager {

  private static EntityManager instance;
  private final LinkedList<EntityLoader> loaders;
  private final Map<Byte, RaceTrackModel> tracks;
  private EntityManager() {
    // Internal last, so it will overwrite what modded did load
    loaders = new LinkedList<>();
    loaders.addFirst(ModEntityLoader.getInstance());
    loaders.addLast(InternalEntityLoader.getInstance());

    tracks = new HashMap<>();
  }

  public static EntityManager getInstance() {
    if (instance == null) {
      instance = new EntityManager();
    }
    return instance;
  }

  public void loadTracks() {
    loaders.forEach(loader -> {
      List<RaceTrackModel> tracks = null;
      try {
        tracks = loader.loadRaceTracks();
      } catch (IOException e) {
        throw new RuntimeException("Failed to load tracks",e);
      }
      tracks.forEach(track -> {
        if (this.tracks.containsKey(track.getIdentifier())) {
          this.tracks.replace(track.getIdentifier(), track);
        } else {
          this.tracks.put(track.getIdentifier(), track);
        }
      });
    });
  }

  public RaceTrackModel getFromId(byte id) {
    return this.tracks.get(id);
  }

  public int numberOfTracks() {
    return this.tracks.size();
  }

  public List<String> trackNames() {
    return this.tracks.values().stream().map(RaceTrackModel::getTrackName)
        .collect(Collectors.toList());
  }


  public List<RaceTrackModel> getTracks() {
    return new ArrayList<>(this.tracks.values());
  }
}
