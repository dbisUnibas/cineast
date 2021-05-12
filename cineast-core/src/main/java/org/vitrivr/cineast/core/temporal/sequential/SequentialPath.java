package org.vitrivr.cineast.core.temporal.sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.vitrivr.cineast.core.temporal.ScoredSegment;

public class SequentialPath implements Comparable<SequentialPath> {

  private final String objectId;
  private double score;
  private final List<ScoredSegment> segments;
  private int currentContainerId;
  private final float startAbs;
  private float currentEndAbs;

  public SequentialPath(String objectId, ScoredSegment initSegment) {
    this.objectId = objectId;
    this.segments = new ArrayList<>();

    this.currentContainerId = -1;
    this.addSegment(initSegment);
    this.currentContainerId = initSegment.getContainerId();
    this.startAbs = initSegment.getStartAbs();
  }

  public SequentialPath(SequentialPath sequentialPath) {
    this.objectId = sequentialPath.getObjectId();
    this.segments = new ArrayList<>(sequentialPath.segments);
    this.currentContainerId = sequentialPath.getCurrentContainerId();
    this.score = sequentialPath.getScore();
    this.startAbs = sequentialPath.getStartAbs();
    this.currentEndAbs = sequentialPath.getCurrentEndAbs();
  }

  public boolean addSegment(ScoredSegment segment) {
    if (segment.getContainerId() > currentContainerId) {
      this.segments.add(segment);
      this.currentContainerId = segment.getContainerId();
      this.score += segment.getScore();
      this.currentEndAbs = segment.getEndAbs();
      return true;
    } else {
      return false;
    }
  }

  public String getObjectId() {
    return objectId;
  }

  public double getScore() {
    return score;
  }

  public int getCurrentContainerId() {
    return currentContainerId;
  }

  public List<ScoredSegment> getSegments() {
    return segments;
  }

  public List<String> getSegmentIds() {
    return segments.stream().map(ScoredSegment::getSegmentId).collect(Collectors.toList());
  }

  public ScoredSegment getCurrentLastSegment() {
    return this.segments.get(this.segments.size() - 1);
  }

  public float getStartAbs() {
    return startAbs;
  }

  public float getCurrentEndAbs() {
    return currentEndAbs;
  }

  @Override
  public int compareTo(@NotNull SequentialPath o) {
    return Double.compare(this.score, o.getScore());
  }
}
